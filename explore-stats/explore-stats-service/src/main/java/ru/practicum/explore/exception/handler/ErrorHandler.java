package ru.practicum.explore.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explore.exception.ErrorResponse;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({IllegalArgumentException.class, DateTimeParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
