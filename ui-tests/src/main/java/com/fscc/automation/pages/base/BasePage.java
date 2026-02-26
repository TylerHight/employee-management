package com.fscc.automation.pages.base;

import com.fscc.automation.core.annotations.ElementName;
import com.fscc.automation.core.config.ConfigManager;
import com.fscc.automation.core.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all Page Objects
 * Provides common functionality for interacting with web elements
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final Logger logger = LogManager.getLogger(this.getClass());
    private final Map<WebElement, String> elementNameCache = new HashMap<>();

    /**
     * Constructor initializes WebDriver, WebDriverWait, and PageFactory
     */
    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigManager.getInstance().getExplicitWait()));
        PageFactory.initElements(driver, this);
        cacheElementNames(); // Cache element names once
        logger.debug("Initialized page: " + this.getClass().getSimpleName());
    }

    /**
     * Cache element names once during page initialization
     */
    private void cacheElementNames() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);

                // Handle single WebElement
                if (field.getType() == WebElement.class) {
                    WebElement element = (WebElement) field.get(this);
                    if (element != null) {
                        cacheElementName(element, field);
                    }
                }
                // Handle List<WebElement>
                else if (List.class.isAssignableFrom(field.getType())) {
                    Object fieldValue = field.get(this);
                    if (fieldValue instanceof List) {
                        List<?> list = (List<?>) fieldValue;
                        if (!list.isEmpty() && list.get(0) instanceof WebElement) {
                            // For lists, we'll cache a generic name since individual elements
                            // in the list will be handled differently
                            ElementName annotation = field.getAnnotation(ElementName.class);
                            String baseName = annotation != null ? annotation.value() : field.getName();
                            // Individual list elements will use this base name + index if needed
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Could not cache element name for field: " + field.getName(), e);
            }
        }
    }

    /**
     * Cache individual element name
     */
    private void cacheElementName(WebElement element, Field field) {
        ElementName annotation = field.getAnnotation(ElementName.class);
        String name = annotation != null ? annotation.value() : field.getName();
        elementNameCache.put(element, name);
    }

    /**
     * Get cached element name
     */
    protected String getElementName(WebElement element) {
        return elementNameCache.getOrDefault(element, "Unknown Element");
    }

    /**
     * Navigate to a URL
     */
    protected void navigateTo(String url) {
        driver.get(url);
        logger.info("Navigated to URL: " + url);
    }

    /**
     * Click on an element with automatic name resolution
     */
    protected void click(WebElement element) {
        String elementName = getElementName(element);
        try {
            waitForElementClickable(element);
            highlightElement(element);
            element.click();
            logger.info("Clicked on element: " + elementName);
        } catch (Exception e) {
            logger.error("Failed to click on element: " + elementName, e);
            throw e;
        }
    }

    /**
     * Enter text into an input field
     */
    protected void sendText(WebElement element, String text) {
        String elementName = getElementName(element);
        try {
            waitForElementVisible(element);
            highlightElement(element);
            element.clear();
            element.sendKeys(text);
            logger.info("Entered text in " + elementName + ": " + text);
        } catch (Exception e) {
            logger.error("Failed to enter text in element: " + elementName, e);
            throw e;
        }
    }

    /**
     * Get text from an element
     */
    protected String getText(WebElement element) {
        String elementName = getElementName(element);
        try {
            waitForElementVisible(element);
            highlightElement(element);
            String text = element.getText();
            logger.info("Got text from " + elementName + ": " + text);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element: " + elementName, e);
            throw e;
        }
    }

    /**
     * Wait for an element to be visible
     */
    protected void waitForElementVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for an element to be clickable
     */
    protected void waitForElementClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Highlight an element by changing its border style
     */
    protected void highlightElement(WebElement element) {
        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.border='2px solid red'", element);
        }
    }

    /**
     * Check if an element is displayed
     */
    protected boolean isElementDisplayed(WebElement element) {
        String elementName = getElementName(element);
        try {
            boolean isDisplayed = element.isDisplayed();
            logger.info(elementName + " is displayed: " + isDisplayed);
            return isDisplayed;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.info(elementName + " is not displayed");
            return false;
        }
    }

    /**
     * Execute JavaScript
     */
    protected Object executeJavaScript(String script, Object... args) {
        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return js.executeScript(script, args);
        }
        return null;
    }
}