package ru.practicum.explore.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explore.exception.exceptions.*;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, CategoryNotFoundException.class,
            EventNotFoundException.class, AreaNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        for (final FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", errors.toString());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            ConstraintViolationException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final Exception e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage());
    }

    @ExceptionHandler({UpdateEventImpossibleException.class,
            ParticipationRequestNotCreatedException.class,
            EmailOrNameRegisteredException.class,
            CategoryExistsException.class,
            CannotDeleteCategoryException.class,
            AccessDeniedException.class,
            RequestCannotBeUpdatedException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEventUpdateImpossible(RuntimeException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage());
    }
}
