package com.ibm.fscc.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceType, fieldName, fieldValue));
    }
}
