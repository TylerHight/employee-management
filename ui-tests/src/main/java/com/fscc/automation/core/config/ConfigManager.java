package com.fscc.automation.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages configuration properties for the framework
 * Follows Singleton pattern to ensure only one instance exists
 */
public class ConfigManager {

    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "/config.properties";
    private static ConfigManager instance;

    private ConfigManager() {
        try (InputStream is = getClass().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                properties.load(is);
                logger.info("Configuration loaded successfully from classpath: " + CONFIG_FILE);
            } else {
                logger.error("Configuration file not found in classpath: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file: " + CONFIG_FILE, e);
        }
    }

    /**
     * Get the singleton instance of ConfigManager
     * 
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Get a property with default value if not found
     * 
     * @param key          Property key
     * @param defaultValue Default value if property is not found
     * @return Property value or default
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get a property value by key
     * 
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get the base URL of the application
     * 
     * @return Base URL
     */
    public String getBaseUrl() {
        return getProperty("baseUrl");
    }

    /**
     * Check if headless browser mode is enabled
     * 
     * @return true if headless mode is enabled
     */
    public boolean isHeadless() {
        // Allow override via system property
        String sysProp = System.getProperty("headless");
        if (sysProp != null) {
            return Boolean.parseBoolean(sysProp);
        }
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    /**
     * Get the implicit wait time in seconds
     * 
     * @return Implicit wait time
     */
    public int getImplicitWait() {
        return Integer.parseInt(getProperty("implicitWait"));
    }

    /**
     * Get the explicit wait time in seconds
     * 
     * @return Explicit wait time
     */
    public int getExplicitWait() {
        return Integer.parseInt(getProperty("explicitWait"));
    }

    /**
     * Get the path for saving screenshots
     * 
     * @return Screenshots path
     */
    public String getScreenshotsPath() {
        return getProperty("screenshotsPath");
    }

    /**
     * Get the browser type
     * 
     * @return Browser type (chrome, firefox, edge)
     */
    public String getBrowser() {
        return getProperty("browser");
    }

    /**
     * Get Cucumber-specific configuration
     */
    public String getCucumberReportsPath() {
        return getProperty("cucumber.reports.path");
    }

    public boolean isCucumberStepLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("cucumber.step.logging.enabled"));
    }

    /**
     * Check if screenshots should be organized by feature
     * 
     * @return true if organize by feature enabled
     */
    public boolean isOrganizeScreenshotsByFeature() {
        return Boolean.parseBoolean(getProperty("cucumber.screenshots.organize.by.feature"));
    }

    /**
     * Check if Cucumber parallel execution is enabled
     * 
     * @return true if parallel execution enabled
     */
    public boolean isCucumberParallelExecution() {
        return Boolean.parseBoolean(getProperty("cucumber.parallel.execution"));
    }

}
