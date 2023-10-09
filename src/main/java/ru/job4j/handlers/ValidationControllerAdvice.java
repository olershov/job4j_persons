package ru.job4j.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс с аннотацией ControllerAdvice совместно с аннотацией ExceptionHandler у метода
 * служит для отслеживания и обработки исключений у всех классов-контроллеров
 */
@ControllerAdvice
public class ValidationControllerAdvice {

    /**
     * Метод позволяет отслеживать и обрабатывать исключения MethodArgumentNotValidException во всех контроллерах
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handle(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(
                e.getFieldErrors().stream()
                        .map(f -> Map.of(
                                f.getField(),
                                String.format("%s. Actual value: %s", f.getDefaultMessage(), f.getRejectedValue())
                        ))
                        .collect(Collectors.toList())
        );
    }

}