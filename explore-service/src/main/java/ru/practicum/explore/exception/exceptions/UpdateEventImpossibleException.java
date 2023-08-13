package ru.practicum.explore.exception.exceptions;

public class UpdateEventImpossibleException extends RuntimeException {
    public UpdateEventImpossibleException() {
    }

    public UpdateEventImpossibleException(String message) {
        super(message);
    }

    public UpdateEventImpossibleException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateEventImpossibleException(Throwable cause) {
        super(cause);
    }
}
