package com.fscc.automation.constants.messages;

public final class ErrorMessages {

    private ErrorMessages() {
    }

    // General Error Messages
    public static final String ELEMENT_NOT_FOUND = "Element not found on the page";
    public static final String PAGE_LOAD_TIMEOUT = "Page failed to load within expected time";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";

    // Home Page Specific
    public static final String HOME_PAGE_TITLE_MISMATCH = "Home page title does not match expected value";
    public static final String TEST_LINK_NOT_FOUND = "Test scenario link not found: %s";
    public static final String UNEXPECTED_LINK_COUNT = "Expected %d links, but found %d";
}
