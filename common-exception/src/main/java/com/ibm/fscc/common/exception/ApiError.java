package com.ibm.fscc.common.exception;

import java.time.Instant;
import java.util.Map;

public class ApiError {
    private String code;
    private String message;
    private Instant timestamp;
    private Map<String, String> fieldErrors; // Optional for validation errors

    public ApiError(String code, String message, Instant timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ApiError(String code, String message, Instant timestamp, Map<String, String> fieldErrors) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.fieldErrors = fieldErrors;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
