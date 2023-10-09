package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.domain.PersonDTO;
import ru.job4j.marker.Operation;
import ru.job4j.service.PersonServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Контроллер описывает CRUD-операции с пользователем
 * и построен согласно схеме Rest-архитектуры
 */
@AllArgsConstructor
@RestController
@RequestMapping("/person")
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final ObjectMapper objectMapper;
    private final PersonServiceImpl persons;
    private BCryptPasswordEncoder encoder;

    /**
     * Получить список всех пользователей
     */
    @GetMapping("/")
    public List<Person> findAll() {
        return persons.findAll();
    }

    /**
     * Поиск пользователя по id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = persons.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not found. Please, check id.");
        }
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }

    /**
     * Регистрация нового пользователя. Перед созданием происходит проверка, заполнены ли
     * поля логин и пароль, а так же на соответствие сложности пароля требуемым параметрам
     */
    @PostMapping("/sign-up")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Person> signUp(@Valid @RequestBody Person person) {
        String password = person.getPassword();
        checkPassword(password);
        person.setPassword(encoder.encode(password));
        var result = persons.save(person);
        return new ResponseEntity<>(
                result.orElse(new Person()),
                result.isPresent() ? HttpStatus.CREATED : HttpStatus.CONFLICT
        );
    }

    /**
     * Метод обновления пользователя. Перед созданием происходит проверка, заполнены ли
     * поля логин и пароль, а так же на соответствие сложности пароля требуемым параметрам
     */
    @PutMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        String password = person.getPassword();
        checkPassword(password);
        person.setPassword(encoder.encode(person.getPassword()));
        return new ResponseEntity<>(
                persons.update(person).isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    /**
     * Метод частичного обновления пользователя. Обновляет только пароль пользователя
     * с использованием модели PersonDTO
     */
    @PutMapping("/patchUpdate")
    public ResponseEntity<Void> patchUpdate(@Valid @RequestBody PersonDTO personDto) {
        String password = personDto.getPassword();
        checkPassword(password);
        Person person = new Person();
        person.setId(personDto.getId());
        person.setPassword(encoder.encode(personDto.getPassword()));
        return new ResponseEntity<>(
                persons.update(person).isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    /**
     * Метод удаления пользователя
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        if (persons.delete(person).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not found. Please, check id.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Метод с аннотацией ExceptionHandler позволяет отслеживать и обрабатывать исключения на уровне
     * данного контроллера. Данный метод создан для отслеживания IllegalArgumentException
     */
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

    /**
     * Метод для проверки сложности пароля. Пароль обязательно должен быть не менее 8 символов,
     * а также должен содержать цифры, заглавные и строчные буквы латинского алфавита.
     */
    private void checkPassword(String password) {
        if (password.length() < 8 || !password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[A-Za-z0-9]+$")) {
            throw new IllegalArgumentException(
                    "The password must be no shorter than 8 characters, and must also contain numbers, lowercase and uppercase letters of the Latin alphabet"
            );
        }
    }
}