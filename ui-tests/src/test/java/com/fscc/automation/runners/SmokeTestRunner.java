// File: src/test/java/com/uiplayground/automation/runners/SmokeTestRunner.java
package com.fscc.automation.runners;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "src/test/resources/features", glue = {
        "com.fscc.automation.steps",
        "com.fscc.automation.hooks"
}, plugin = {
        "pretty",
        "html:target/cucumber-reports/html/smoke",
        "json:target/cucumber-reports/json/smoke.json",
        "junit:target/cucumber-reports/junit/smoke.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
}, tags = "@smoke")
public class SmokeTestRunner extends BaseTestRunner {
}
