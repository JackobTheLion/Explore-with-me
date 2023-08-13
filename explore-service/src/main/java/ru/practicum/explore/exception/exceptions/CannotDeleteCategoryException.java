package ru.practicum.explore.exception.exceptions;

public class CannotDeleteCategoryException extends RuntimeException {
    public CannotDeleteCategoryException() {
    }

    public CannotDeleteCategoryException(String message) {
        super(message);
    }

    public CannotDeleteCategoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotDeleteCategoryException(Throwable cause) {
        super(cause);
    }
}
