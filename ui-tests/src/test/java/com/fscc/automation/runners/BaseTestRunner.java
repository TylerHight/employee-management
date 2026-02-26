package com.fscc.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Base class for all test runners
 * Contains common configurations and settings
 */
public abstract class BaseTestRunner extends AbstractTestNGCucumberTests {
    // Common configurations or overrides can go here
}

// Command to run dev tests (tests that are currently being developed):
// mvn test "-Dcucumber.filter.tags=@dev" "-Dtest=<TestRunnerName>"
