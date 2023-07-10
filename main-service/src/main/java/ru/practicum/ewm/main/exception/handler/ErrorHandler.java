package ru.practicum.ewm.main.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.main.exception.ConflictException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ApiError handleNotFoundException(EntityNotFoundException e) {
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ApiError handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("409 data violation constraint exception reason:{}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class,
            ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiError handleWrongRequestParameters(RuntimeException e) {
        log.error("400 bad request exception reason:{}", e.getMessage(), e);
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Incorrectly made request")
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("400 bad request exception reason:{}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(String.format("Field: %s. Error: %s. Value: %s",
                        Objects.requireNonNull(e.getFieldError()).getField(),
                        e.getFieldError().getDefaultMessage(),
                        e.getFieldError().getRejectedValue()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ApiError handleConflictException(final ConflictException e) {
        log.error("409 conflict exception reason:{}", e.getMessage(), e);
        return ApiError.builder()
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .status(HttpStatus.CONFLICT)
                .build();
    }

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ApiError {
        StackTraceElement[] errors;
        String message;
        String reason;
        HttpStatus status;
        final LocalDateTime timestamp = LocalDateTime.now();
    }
}