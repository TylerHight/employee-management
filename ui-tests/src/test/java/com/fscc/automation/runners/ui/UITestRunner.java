package com.fscc.automation.runners.ui;

import com.fscc.automation.runners.BaseTestRunner;
import io.cucumber.testng.CucumberOptions;

/**
 * Test runner for all UI tests
 * Runs all UI tests in the project
 */
@CucumberOptions(
    features = "src/test/resources/features/ui",
    glue = {"com.fscc.automation.steps", "com.fscc.automation.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/html",
        "json:target/cucumber-reports/json/cucumber.json",
        "junit:target/cucumber-reports/junit/cucumber.xml"
    }
)
public class UITestRunner extends BaseTestRunner {
    // Configuration is inherited from BaseTestRunner
}
