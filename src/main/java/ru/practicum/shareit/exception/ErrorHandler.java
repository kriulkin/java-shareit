package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final MethodArgumentNotValidException e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(final AlreadyExistsException e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = NoSuchEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final NoSuchEntityException e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus
    public ErrorResponse handle(final Throwable e) {
        log.error(String.format("Error: %s", e.getMessage()));
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
