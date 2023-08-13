package ru.practicum.explore.exception.exceptions;

public class HitNotAddedException extends RuntimeException {
    public HitNotAddedException() {
    }

    public HitNotAddedException(String message) {
        super(message);
    }

    public HitNotAddedException(String message, Throwable cause) {
        super(message, cause);
    }

    public HitNotAddedException(Throwable cause) {
        super(cause);
    }

}
