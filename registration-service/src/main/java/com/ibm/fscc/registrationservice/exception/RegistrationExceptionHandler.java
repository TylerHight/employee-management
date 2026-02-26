package com.ibm.fscc.registrationservice.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.ibm.fscc.common.exception.ApiError;
import com.ibm.fscc.common.exception.BaseGlobalExceptionHandler;

@ControllerAdvice
public class RegistrationExceptionHandler extends BaseGlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiError> handleDuplicateEmail(DuplicateEmailException ex) {
        ApiError error = new ApiError("DUPLICATE_EMAIL", ex.getMessage(), Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ApiError> handleInvalidStatus(InvalidStatusException ex) {
        ApiError error = new ApiError("INVALID_STATUS", ex.getMessage(), Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ApiError> handleInvalidStateTransition(InvalidStateTransitionException ex) {
        ApiError error = new ApiError("INVALID_STATE_TRANSITION", ex.getMessage(), Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(KafkaPublishException.class)
    public ResponseEntity<ApiError> handleKafkaPublishException(KafkaPublishException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        if (ex.getCause() != null) {
            fieldErrors.put("cause", ex.getCause().getMessage());
        }
        
        ApiError error = new ApiError(
            "KAFKA_PUBLISH_ERROR",
            "Failed to publish message to Kafka: " + ex.getMessage(),
            Instant.now(),
            fieldErrors
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDataAccessException(DataAccessException ex) {
        String errorCode = "DATABASE_ERROR";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        // More specific error handling for MongoDB errors
        if (ex instanceof DuplicateKeyException) {
            errorCode = "DUPLICATE_KEY";
            status = HttpStatus.CONFLICT;
        }
        
        ApiError error = new ApiError(
            errorCode,
            "Database operation failed: " + ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.status(status).body(error);
    }
    
    @ExceptionHandler(SyncException.class)
    public ResponseEntity<ApiError> handleSyncException(SyncException ex) {
        ApiError error = new ApiError(
            "SYNC_ERROR",
            "Synchronization error between Kafka and MongoDB: " + ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
