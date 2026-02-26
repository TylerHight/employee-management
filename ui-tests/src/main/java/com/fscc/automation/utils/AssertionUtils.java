package com.fscc.automation.utils;

import org.testng.Assert;
import com.fscc.automation.core.reporting.ReportManager;
import com.fscc.automation.constants.messages.ValidationMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Assertion utilities with enhanced logging and reporting.
 * Provides centralized assertion methods with consistent error handling,
 * logging, and reporting integration.
 */
public final class AssertionUtils {

    private static final Logger logger = LogManager.getLogger(AssertionUtils.class);

    private AssertionUtils() {
        // Utility class
    }

    /**
     * Asserts that two strings are equal with normalized whitespace
     * 
     * @param actual   Actual string value
     * @param expected Expected string value
     * @param message  Error message template if assertion fails
     */
    public static void assertTitle(String actual, String expected, String message) {
        logAssertion("Title", expected, actual);

        // Normalize strings before comparison (trim and normalize whitespace)
        String normalizedActual = actual != null ? actual.replaceAll("\\s+", " ").trim() : null;
        String normalizedExpected = expected != null ? expected.replaceAll("\\s+", " ").trim() : null;

        try {
            Assert.assertEquals(normalizedActual, normalizedExpected, message);
            reportAssertionPassed("Title", expected, actual);
        } catch (AssertionError e) {
            reportAssertionFailed("Title", expected, actual, e.getMessage());
            throw e;
        }
    }

    /**
     * Asserts that a string is not null or empty
     * 
     * @param actual    Actual string value
     * @param fieldName Name of the field being validated
     */
    public static void assertNotNullOrEmpty(String actual, String fieldName) {
        logAssertion(fieldName, "not null or empty", actual != null ? actual : "null");

        try {
            Assert.assertNotNull(actual, String.format(ValidationMessages.FIELD_NOT_NULL, fieldName));
            Assert.assertFalse(actual.trim().isEmpty(), String.format(ValidationMessages.FIELD_NOT_EMPTY, fieldName));
            reportAssertionPassed(fieldName, "not null or empty", actual);
        } catch (AssertionError e) {
            reportAssertionFailed(fieldName, "not null or empty", actual != null ? actual : "null", e.getMessage());
            throw e;
        }
    }

    /**
     * Asserts that a condition is true
     * 
     * @param condition Boolean condition to verify
     * @param message   Error message if assertion fails
     */
    public static void assertTrue(boolean condition, String message) {
        logAssertion("Condition", "true", String.valueOf(condition));

        try {
            Assert.assertTrue(condition, message);
            reportAssertionPassed("Condition", "true", String.valueOf(condition));
        } catch (AssertionError e) {
            reportAssertionFailed("Condition", "true", String.valueOf(condition), e.getMessage());
            throw e;
        }
    }

    /**
     * Asserts that an element count matches the expected value
     * 
     * @param actual      Actual count
     * @param expected    Expected count
     * @param elementName Name of elements being counted
     */
    public static void assertElementCount(int actual, int expected, String elementName) {
        logAssertion(elementName + " count", String.valueOf(expected), String.valueOf(actual));

        try {
            Assert.assertEquals(actual, expected,
                    String.format(ValidationMessages.COUNT_MISMATCH, expected, elementName, actual));

            reportAssertionPassed(elementName + " count", String.valueOf(expected), String.valueOf(actual));
        } catch (AssertionError e) {
            reportAssertionFailed(elementName + " count", String.valueOf(expected),
                    String.valueOf(actual), e.getMessage());
            throw e;
        }
    }

    /**
     * Asserts that a string contains another string
     * 
     * @param actual   The string to search within
     * @param expected The substring expected to be found
     * @param message  Error message if assertion fails
     */
    public static void assertContains(String actual, String expected, String message) {
        logAssertion("Text contains", expected, actual);

        try {
            Assert.assertTrue(actual.contains(expected), message);
            reportAssertionPassed("Text contains", expected, actual);
        } catch (AssertionError e) {
            reportAssertionFailed("Text contains", expected, actual, e.getMessage());
            throw e;
        }
    }

    /**
     * Asserts that an element exists and is displayed
     * 
     * @param isDisplayed Result of element.isDisplayed() check
     * @param elementName Name of the element being checked
     */
    public static void assertElementDisplayed(boolean isDisplayed, String elementName) {
        logAssertion(elementName, "displayed", String.valueOf(isDisplayed));

        try {
            Assert.assertTrue(isDisplayed,
                    String.format("Element '%s' should be displayed but was not visible", elementName));
            reportAssertionPassed(elementName, "displayed", "true");
        } catch (AssertionError e) {
            reportAssertionFailed(elementName, "displayed", "false", e.getMessage());
            throw e;
        }
    }

    // Private helper methods for consistent logging

    private static final String ASSERT_PASS_FORMAT = "✅ %s assertion passed - Expected: '%s', Actual: '%s'";
    private static final String ASSERT_FAIL_FORMAT = "❌ %s assertion failed - Expected: '%s', Actual: '%s' - %s";

    private static void logAssertion(String type, String expected, String actual) {
        logger.info("Asserting {} - Expected: '{}', Actual: '{}'", type, expected, actual);
    }

    private static void reportAssertionPassed(String type, String expected, String actual) {
        String message = String.format(ASSERT_PASS_FORMAT, type, expected, actual);
        logger.debug(message);
        ReportManager.logPass(message);
    }

    private static void reportAssertionFailed(String type, String expected, String actual, String errorMessage) {
        String message = String.format(ASSERT_FAIL_FORMAT, type, expected, actual, errorMessage);
        logger.error(message);
        ReportManager.logFail(message);
    }
}
