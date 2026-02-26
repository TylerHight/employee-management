package com.fscc.automation.core.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.fscc.automation.core.config.ConfigManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages WebDriver instances for the framework
 * Uses ThreadLocal to support parallel test execution
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(DriverManager.class);

    private DriverManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the WebDriver instance for the current thread
     * 
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    /**
     * Initialize a new WebDriver instance based on browser configuration
     */
    public static void initializeDriver() {
        logger.info("Initializing WebDriver");

        String browser = ConfigManager.getInstance().getBrowser();
        boolean headless = ConfigManager.getInstance().isHeadless();

        if (browser == null) {
            browser = "chrome"; // Default fallback
        }

        browser = browser.toLowerCase();
        logger.info("Initializing WebDriver for browser: {} (headless: {})", browser, headless);

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                if (headless) {
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--headless");
                    driver.set(new FirefoxDriver(firefoxOptions));
                    logger.info("Firefox started in headless mode");
                } else {
                    driver.set(new FirefoxDriver());
                }
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                if (headless) {
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--headless");
                    driver.set(new EdgeDriver(edgeOptions));
                    logger.info("Edge started in headless mode");
                } else {
                    driver.set(new EdgeDriver());
                }
                break;

            default: // Chrome
                WebDriverManager.chromedriver().setup();
                if (headless) {
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--headless");
                    chromeOptions.addArguments("--disable-gpu"); // Recommended for headless
                    chromeOptions.addArguments("--window-size=1920,1080"); // Set resolution
                    driver.set(new ChromeDriver(chromeOptions));
                    logger.info("Chrome started in headless mode");
                } else {
                    driver.set(new ChromeDriver());
                }
        }

        driver.get().manage().window().maximize();
        logger.info("WebDriver initialized successfully");
    }

    /**
     * Quit the WebDriver instance and remove it from ThreadLocal
     */
    public static void quitDriver() {
        logger.info("Quitting WebDriver");
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
            logger.info("WebDriver quit successfully");
        }
    }
}
