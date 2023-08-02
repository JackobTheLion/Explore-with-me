package ru.practicum.explore.exceptions;

public class FailToSaveHitException extends RuntimeException {
    public FailToSaveHitException(String message) {
        super(message);
    }
}
