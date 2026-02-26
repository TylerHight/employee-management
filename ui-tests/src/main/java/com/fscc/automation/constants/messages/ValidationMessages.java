package com.fscc.automation.constants.messages;

public final class ValidationMessages {

    private ValidationMessages() {
    }

    // Field Validation Messages
    public static final String FIELD_NOT_NULL = "%s should not be null";
    public static final String FIELD_NOT_EMPTY = "%s should not be empty";
    public static final String FIELD_CONTAINS_TEXT = "%s should contain '%s'";
    public static final String FIELD_EQUALS_TEXT = "%s should equal '%s'";

    // Title Validation
    public static final String TITLE_VERIFICATION_FAILED = "Page title verification failed: expected '%s' but got '%s'";

    // Count Validation
    public static final String COUNT_MISMATCH = "Expected %d %s but found %d";
}
