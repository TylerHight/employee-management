package com.fscc.automation.hooks;

import com.fscc.automation.core.reporting.ReportManager;
import com.fscc.automation.core.reporting.ScreenshotManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportingHooks {

    private static final Logger logger = LogManager.getLogger(ReportingHooks.class);

    @BeforeAll
    public static void beforeAllScenarios() {
        logger.info("Initializing test suite reports");
        ReportManager.initReports();
    }

    @AfterAll
    public static void afterAllScenarios() {
        logger.info("Finalizing test suite reports");
        ReportManager.flushReports();
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        logger.info("Starting Cucumber scenario: {}", scenario.getName());
        ReportManager.createTest(scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                logger.error("Cucumber scenario failed: {}", scenario.getName());
                String screenshotPath = ScreenshotManager.takeScreenshot(scenario.getName());
                if (screenshotPath != null) {
                    ReportManager.getTest()
                            .fail("Scenario failed: " + scenario.getName())
                            .addScreenCaptureFromPath(screenshotPath);
                } else {
                    ReportManager.getTest().fail("Scenario failed: " + scenario.getName());
                }
            } else {
                logger.info("Cucumber scenario passed: {}", scenario.getName());
                ReportManager.getTest().pass("Scenario passed: " + scenario.getName());
            }
        } finally {
            ReportManager.removeTest();
        }
    }
}
