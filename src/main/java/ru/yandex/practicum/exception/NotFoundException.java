package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    public final String error;

    public NotFoundException(String error, String message) {
        super(message);
        this.error = error;
    }
}
