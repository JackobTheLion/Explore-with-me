package ru.practicum.explore.exception.exceptions;

public class ParticipationRequestNotCreatedException extends RuntimeException {
    public ParticipationRequestNotCreatedException() {
    }

    public ParticipationRequestNotCreatedException(String message) {
        super(message);
    }

    public ParticipationRequestNotCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParticipationRequestNotCreatedException(Throwable cause) {
        super(cause);
    }
}
