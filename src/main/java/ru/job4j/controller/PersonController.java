package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.service.PersonServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/person")
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final ObjectMapper objectMapper;
    private final PersonServiceImpl persons;
    private BCryptPasswordEncoder encoder;

    @GetMapping("/")
    public List<Person> findAll() {
        return persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = persons.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not found. Please, check id.");
        }
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Person> signUp(@RequestBody Person person) {
        String password = person.getPassword();
        checkLoginAndPassword(person);
        checkPassword(password);
        person.setPassword(encoder.encode(password));
        var result = persons.save(person);
        return new ResponseEntity<>(
                result.orElse(new Person()),
                result.isPresent() ? HttpStatus.CREATED : HttpStatus.CONFLICT
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        String password = person.getPassword();
        checkLoginAndPassword(person);
        checkPassword(password);
        person.setPassword(encoder.encode(person.getPassword()));
        return new ResponseEntity<>(
                persons.update(person).isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        if (persons.delete(person).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not found. Please, check id.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }

    private void checkLoginAndPassword(Person person) {
        String login = person.getLogin();
        String password = person.getPassword();
        if (login == null || password == null) {
            throw new NullPointerException("Login and password mustn't be empty");
        }
    }

    private void checkPassword(String password) {
        if (password.length() < 8 || !password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[A-Za-z0-9]+$")) {
            throw new IllegalArgumentException(
                    "The password must be no shorter than 8 characters, and must also contain numbers, lowercase and uppercase letters of the Latin alphabet"
            );
        }
    }
}