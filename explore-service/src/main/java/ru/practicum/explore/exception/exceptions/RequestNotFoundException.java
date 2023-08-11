package ru.practicum.explore.exception.exceptions;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException() {
    }

    public RequestNotFoundException(String message) {
        super(message);
    }

    public RequestNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestNotFoundException(Throwable cause) {
        super(cause);
    }
}
