package com.fscc.automation.core.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fscc.automation.core.config.ConfigManager;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages ExtentReports for Cucumber test reporting
 * Enhanced for Cucumber scenario context and step logging
 */
public class ReportManager {

    private static final Logger logger = LogManager.getLogger(ReportManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private ReportManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initialize the ExtentReports instance
     */
    public static void initReports() {
        if (extent == null) {
            String reportsPath = ConfigManager.getInstance().getProperty("reportsPath");
            if (reportsPath == null) {
                reportsPath = "reports/extent-reports/test-report.html";
            } else {
                reportsPath = reportsPath + "/test-report.html";
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportsPath);
            sparkReporter.config().setDocumentTitle("UI Playground Automation Report");
            sparkReporter.config().setReportName("Cucumber Test Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Browser", ConfigManager.getInstance().getBrowser());
            extent.setSystemInfo("Base URL", ConfigManager.getInstance().getBaseUrl());

            logger.info("ExtentReports initialized successfully at: {}", reportsPath);
        }
    }

    /**
     * Get the ExtentReports instance
     * 
     * @return ExtentReports instance
     */
    public static ExtentReports getExtentReports() {
        if (extent == null) {
            initReports();
        }
        return extent;
    }

    /**
     * Create a new test in the report (original method for backward compatibility)
     * 
     * @param testName Name of the test
     */
    public static void createTest(String testName) {
        ExtentTest test = getExtentReports().createTest(testName);
        extentTest.set(test);
        logger.info("Created test in report: {}", testName);
    }

    /**
     * Create test with Cucumber scenario context
     * 
     * @param scenarioName Cucumber scenario name
     * @param featureName  Feature file name
     * @param tags         Scenario tags
     */
    public static void createTest(String scenarioName, String featureName, Collection<String> tags) {
        ExtentTest test = getExtentReports().createTest(scenarioName);

        // Add feature context
        if (featureName != null && !featureName.isEmpty()) {
            test.assignCategory(featureName);
        }

        // Add tags
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                test.assignAuthor(tag.replace("@", "")); // Remove @ symbol from tags
            }
        }

        extentTest.set(test);
        logger.info("Created Cucumber test in report: {} from feature: {}", scenarioName, featureName);
    }

    /**
     * Get the current test instance
     * 
     * @return ExtentTest instance for the current thread
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Remove the current test instance
     */
    public static void removeTest() {
        extentTest.remove();
    }

    /**
     * Log Cucumber step execution
     * 
     * @param stepName Step description
     * @param status   Step status
     */
    public static void logStep(String stepName, String status) {
        ExtentTest test = getTest();
        if (test != null) {
            switch (status.toLowerCase()) {
                case "passed":
                    test.pass("Step: " + stepName);
                    break;
                case "failed":
                    test.fail("Step: " + stepName);
                    break;
                case "skipped":
                    test.skip("Step: " + stepName);
                    break;
                default:
                    test.info("Step: " + stepName);
            }
        }
    }

    /**
     * Add info log to current test
     * 
     * @param message Info message
     */
    public static void logInfo(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.info(message);
        }
    }

    /**
     * Add pass log to current test
     * 
     * @param message Pass message
     */
    public static void logPass(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.pass(message);
        }
    }

    /**
     * Add fail log to current test
     * 
     * @param message Fail message
     */
    public static void logFail(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.fail(message);
        }
    }

    /**
     * Flush reports to disk
     */
    public static void flushReports() {
        if (extent != null) {
            extent.flush();
            logger.info("ExtentReports flushed successfully");
        }
    }
}
