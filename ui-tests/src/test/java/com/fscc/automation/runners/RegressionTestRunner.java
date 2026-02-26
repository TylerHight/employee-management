package com.fscc.automation.runners;

import io.cucumber.testng.CucumberOptions;

/**
 * Test runner for regression tests
 * Runs all tests with the @regression tag
 */
@CucumberOptions(features = "src/test/resources/features", glue = {
        "com.fscc.automation.steps",
        "com.fscc.automation.hooks"
}, plugin = {
        "pretty",
        "html:target/cucumber-reports/html/regression",
        "json:target/cucumber-reports/json/regression.json",
        "junit:target/cucumber-reports/junit/regression.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
}, tags = "@regression")
public class RegressionTestRunner extends BaseTestRunner {
    // Configuration is inherited from BaseTestRunner
}
