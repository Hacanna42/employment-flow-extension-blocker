package io.github.hacanna42.extensionblocker.common.api;

import io.github.hacanna42.extensionblocker.extension.exception.CustomExtensionLimitExceededException;
import io.github.hacanna42.extensionblocker.extension.exception.DuplicateExtensionException;
import io.github.hacanna42.extensionblocker.extension.exception.ExtensionNotFoundException;
import io.github.hacanna42.extensionblocker.extension.exception.InvalidExtensionNameException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExtensionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ExtensionNotFoundException exception) {
        return respond(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateExtensionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateExtensionException exception) {
        return respond(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(CustomExtensionLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleLimitExceeded(CustomExtensionLimitExceededException exception) {
        return respond(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidExtensionNameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidName(InvalidExtensionNameException exception) {
        return respond(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        return respond(HttpStatus.CONFLICT, "이미 추가된 확장자예요.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        return respond(HttpStatus.BAD_REQUEST, firstValidationMessage(exception));
    }

    private String firstValidationMessage(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("입력값을 확인해 주세요.");
    }

    private ResponseEntity<ErrorResponse> respond(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(message));
    }
}
