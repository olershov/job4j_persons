package ru.job4j.domain;

import lombok.Data;

/**
 * Модель данных UserDTO. Создана для частичного обновления модели Person.
 */
@Data
public class PersonDTO {

    /**
     * Идентификатор пользователя
     */
    private int id;

    /**
     * Пароль пользователя
     */
    private String password;
}
