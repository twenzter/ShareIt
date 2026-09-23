package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class DuplicatedDataException extends RuntimeException {
    public final String error;

    public DuplicatedDataException(String error, String message) {
        super(message);
        this.error = error;
    }
}
