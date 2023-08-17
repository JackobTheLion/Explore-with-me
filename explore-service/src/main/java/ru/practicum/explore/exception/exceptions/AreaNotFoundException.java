package ru.practicum.explore.exception.exceptions;

public class AreaNotFoundException extends RuntimeException {
    public AreaNotFoundException() {
    }

    public AreaNotFoundException(String message) {
        super(message);
    }

    public AreaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AreaNotFoundException(Throwable cause) {
        super(cause);
    }
}
