package ru.practicum.explore.exception.exceptions;

public class CompilationNotFoundException extends RuntimeException {
    public CompilationNotFoundException() {
    }

    public CompilationNotFoundException(String message) {
        super(message);
    }

    public CompilationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompilationNotFoundException(Throwable cause) {
        super(cause);
    }
}
