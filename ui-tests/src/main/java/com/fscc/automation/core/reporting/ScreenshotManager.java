package com.fscc.automation.core.reporting;

import com.fscc.automation.core.config.ConfigManager;
import com.fscc.automation.core.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages taking screenshots during test execution
 */
public class ScreenshotManager {

    private static final Logger logger = LogManager.getLogger(ScreenshotManager.class);

    private ScreenshotManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Enhanced screenshot method for Cucumber scenarios
     * 
     * @param scenarioName Cucumber scenario name
     * @param featureName  Feature name (optional)
     * @return Path to saved screenshot
     */
    public static String takeScreenshot(String scenarioName, String featureName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            logger.error("Driver is null, cannot take screenshot");
            return null;
        }

        if (driver instanceof TakesScreenshot) {
            try {
                // Create timestamp for unique filename
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                // Enhanced filename with feature context
                String fileName = (featureName != null ? featureName + "_" : "") +
                        scenarioName.replaceAll("[^a-zA-Z0-9]", "_") +
                        "_" + timestamp + ".png";

                // Create feature-specific directory
                String screenshotDir = ConfigManager.getInstance().getScreenshotsPath();
                if (featureName != null) {
                    screenshotDir = screenshotDir + "/" + featureName;
                }

                Path dirPath = Paths.get(screenshotDir);

                // Create directory if it doesn't exist
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                // Take screenshot
                File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path targetPath = Paths.get(screenshotDir, fileName);
                Files.copy(scrFile.toPath(), targetPath);

                logger.info("Screenshot saved to: " + targetPath);
                return targetPath.toString();

            } catch (IOException e) {
                logger.error("Failed to take screenshot", e);
            }
        } else {
            logger.warn("WebDriver doesn't support screenshots");
        }

        return null;
    }

    // Backward compatibility method
    public static String takeScreenshot(String testName) {
        return takeScreenshot(testName, null);
    }

}