package com.ibm.fscc.employeeservice.exception;

import com.ibm.fscc.common.exception.ApiError;
import com.ibm.fscc.common.exception.BaseGlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class EmployeeExceptionHandler extends BaseGlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmployeeEmailException.class)
    public ResponseEntity<ApiError> handleDuplicateEmail(DuplicateEmployeeEmailException ex) {
        ApiError error = new ApiError(
                "DUPLICATE_EMAIL",
                ex.getMessage(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidEmployeeFieldException.class)
    public ResponseEntity<ApiError> handleInvalidField(InvalidEmployeeFieldException ex) {
        ApiError error = new ApiError(
                "INVALID_EMPLOYEE_FIELD",
                ex.getMessage(),
                Instant.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
