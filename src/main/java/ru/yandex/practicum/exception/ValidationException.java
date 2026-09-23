package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    public final String error;

    public ValidationException(String error, String message) {
        super(message);
        this.error = error;
    }
}
