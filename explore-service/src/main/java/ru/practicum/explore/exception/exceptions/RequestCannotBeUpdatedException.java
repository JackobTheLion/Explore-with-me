package ru.practicum.explore.exception.exceptions;

public class RequestCannotBeUpdatedException extends RuntimeException {
    public RequestCannotBeUpdatedException() {
    }

    public RequestCannotBeUpdatedException(String message) {
        super(message);
    }

    public RequestCannotBeUpdatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestCannotBeUpdatedException(Throwable cause) {
        super(cause);
    }
}
