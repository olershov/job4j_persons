package ru.job4j.service;

import ru.job4j.domain.Person;
import ru.job4j.domain.PersonDTO;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, описывающий слой сервисов с CRUD-операциями
 */
public interface PersonService {

    /**
     * Получить список всеъ пользователей
     */
    List<Person> findAll();

    /**
     * Поиск пользователя по id
     */
    Optional<Person> findById(int id);

    /**
     * Сохранение нового пользователя
     */
    Optional<Person> save(Person person);

    /**
     * Удаление пользователя
     */
    Optional<Person> delete(Person person);

    /**
     * Обновление пользователя
     */
    Optional<Person> update(Person person);
}
