package com.ibm.fscc.loginservice.exception;

import com.ibm.fscc.common.exception.ApiError;
import com.ibm.fscc.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class LoginGlobalExceptionHandler {

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidPassword(InvalidPasswordException ex) {
        ApiError error = new ApiError("INVALID_PASSWORD", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<ApiError> handleInvalidResetToken(InvalidResetTokenException ex) {
        ApiError error = new ApiError("INVALID_RESET_TOKEN", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError error = new ApiError("USER_NOT_FOUND", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
