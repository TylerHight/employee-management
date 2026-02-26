package com.fscc.automation.hooks;

import com.fscc.automation.core.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cucumber hooks for test setup and teardown
 * Handles WebDriver lifecycle for each scenario
 */
public class TestSetupHooks {

    private static final Logger logger = LogManager.getLogger(TestSetupHooks.class);

    /**
     * Runs before each Cucumber scenario
     */
    @Before(order = 1)
    public void setUp() {
        logger.info("Setting up test scenario - initializing WebDriver");
        DriverManager.initializeDriver();
    }

    /**
     * Runs after each Cucumber scenario
     */
    @After(order = 1)
    public void tearDown() {
        logger.info("Tearing down test scenario - quitting WebDriver");
        DriverManager.quitDriver();
    }
}
