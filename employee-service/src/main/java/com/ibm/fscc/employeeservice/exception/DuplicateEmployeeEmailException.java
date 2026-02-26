package com.ibm.fscc.employeeservice.exception;

public class DuplicateEmployeeEmailException extends RuntimeException {
    public DuplicateEmployeeEmailException(String email) {
        super("Employee with email '%s' already exists".formatted(email));
    }
}
