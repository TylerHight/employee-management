package com.ibm.fscc.registrationservice.exception;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String status) {
        super("Invalid registration status: " + status);
    }
}
