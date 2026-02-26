package com.ibm.fscc.employeeservice.exception;

public class InvalidEmployeeFieldException extends RuntimeException {
    public InvalidEmployeeFieldException (String fieldName) {
        super("Invalid or read-only field: %s".formatted(fieldName));
    }
    
}
