package ru.job4j.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Person;
import ru.job4j.domain.PersonDTO;
import ru.job4j.repository.PersonRepository;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса PersonService
 */
@AllArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;

    @Override
    public List<Person> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Person> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Person> save(Person person) {
        Optional<Person> result = Optional.empty();
        try {
            result = Optional.of(repository.save(person));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Optional<Person> delete(Person person) {
        Optional<Person> personInDB = repository.findById(person.getId());
        if (personInDB.isPresent()) {
            repository.delete(person);
        }
       return personInDB;
    }

    @Override
    public Optional<Person> update(Person person) {
        Optional<Person> personInDB = repository.findById(person.getId());
        if (personInDB.isPresent()) {
            if (person.getLogin() == null) {
                person.setLogin(personInDB.get().getLogin());
            }
            save(person);
        }
        return personInDB;
    }
}
