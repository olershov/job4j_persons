package ru.job4j.domain;

import lombok.Data;
import ru.job4j.marker.Operation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Модель данных пользователя
 */
@Data
@Entity
public class Person {

    /**
     * Идентификатор пользователя. Аннотация NotNull говорит о том, что поле будет проходить валидацию
     * на равенство null
     */
    @NotNull(message = "Id must be non null", groups = {
            Operation.OnUpdate.class
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Логин пользователя. Аннотация NotBlank говорит о том, что поле будет проходить валидацию,
     * не является ли строка пустой
     */
    @NotBlank(message = "Login must be not empty")
    private String login;

    /**
     * Пароль пользователя. Аннотация NotBlank говорит о том, что поле будет проходить валидацию,
     * не является ли строка пустой
     */
    @NotBlank(message = "Password must be not empty")
    private String password;
}
