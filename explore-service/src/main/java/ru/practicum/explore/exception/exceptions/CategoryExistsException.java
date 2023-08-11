package ru.practicum.explore.exception.exceptions;

public class CategoryExistsException extends RuntimeException {
    public CategoryExistsException() {
    }

    public CategoryExistsException(String message) {
        super(message);
    }

    public CategoryExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryExistsException(Throwable cause) {
        super(cause);
    }
}
