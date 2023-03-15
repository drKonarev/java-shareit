package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.persistence.PersistenceException;
import javax.validation.ValidationException;
import java.lang.reflect.InvocationTargetException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, HttpClientErrorException.NotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistException.class, PostAlreadyExistException.class,
            ConstraintViolationException.class,})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse alreadyExist(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({ValidationException.class, NumberFormatException.class, TypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationError(RuntimeException ex) {
        if (ex.getMessage().length() >= 25)
            return new ErrorResponse("Ошибка валидации данных!");
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MyValidationException.class,
            IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationSpringError(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({DuplicateKeyException.class, NullPointerException.class,
            PersistenceException.class, InvocationTargetException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse duplicateException(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler(AccessOrAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse accessOrAvailableException(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalError(Throwable ex) {
        return new ErrorResponse(ex.getMessage());
    }


}
