package com.fscc.automation.runners.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Test runner for Landing Page tests
 */
@CucumberOptions(
    features = "src/test/resources/features/ui/landing-page.feature",
    glue = {"com.fscc.automation.steps", "com.fscc.automation.hooks"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/html",
        "json:target/cucumber-reports/json/cucumber.json",
        "junit:target/cucumber-reports/junit/cucumber.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    tags = "@landing"
)
public class LandingPageTestRunner extends AbstractTestNGCucumberTests {
    // Configuration is inherited from BaseTestRunner
}