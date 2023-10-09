package ru.job4j.domain;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Модель данных UserDTO. Создана для частичного обновления модели Person.
 * Аннотация NotNull говорит о том, что поле будет проходить валидацию на равенство null
 */
@Data
public class PersonDTO {

    /**
     * Идентификатор пользователя
     */
    @NotNull(message = "Id must be non null")
    private Integer id;

    /**
     * Пароль пользователя
     */
    @NotBlank(message = "Password must be not empty")
    private String password;
}
