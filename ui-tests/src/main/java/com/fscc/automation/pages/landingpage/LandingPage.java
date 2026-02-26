package com.fscc.automation.pages.landingpage;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.fscc.automation.constants.selectors.Landing;
import com.fscc.automation.core.annotations.ElementName;
import com.fscc.automation.core.config.ConfigManager;
import com.fscc.automation.pages.base.BasePage;

/**
 * Page Object for the Landing Page
 * Contains all elements and methods to interact with the landing page
 */
public class LandingPage extends BasePage {

    // Body Elements
    @ElementName("Landing Title")
    @FindBy(css = "[data-test='" + Landing.Body.TITLE + "']")
    private WebElement landingTitle;

    @ElementName("Landing Subtitle")
    @FindBy(css = "[data-test='" + Landing.Body.SUBTITLE + "']")
    private WebElement landingSubtitle;

    @ElementName("Landing Register Button")
    @FindBy(css = "[data-test='" + Landing.Body.REGISTER_BUTTON + "']")
    private WebElement landingRegisterButton;

    @ElementName("Landing Sign In Button")
    @FindBy(css = "[data-test='" + Landing.Body.SIGN_IN_BUTTON + "']")
    private WebElement landingSignInButton;

    @ElementName("Feature Registration")
    @FindBy(css = "[data-test='" + Landing.Body.FEATURE_REGISTRATION + "']")
    private WebElement featureRegistration;

    @ElementName("Feature Login")
    @FindBy(css = "[data-test='" + Landing.Body.FEATURE_LOGIN + "']")
    private WebElement featureLogin;

    @ElementName("Feature Admin Tools")
    @FindBy(css = "[data-test='" + Landing.Body.FEATURE_ADMIN_TOOLS + "']")
    private WebElement featureAdminTools;
    
    // Dialog Elements
    @ElementName("Registration Dialog")
    @FindBy(css = "." + Landing.Body.REGISTRATION_DIALOG)
    private WebElement registrationDialog;
    
    @ElementName("Employee Form Card")
    @FindBy(css = "." + Landing.Body.EMPLOYEE_FORM_CARD + "." + Landing.Body.REGISTRATION_MODE)
    private WebElement registrationForm;

    public LandingPage() {
        super();
    }

    /**
     * Opens the landing page
     * 
     * @return LandingPage instance for method chaining
     */
    public LandingPage open() {
        navigateTo(ConfigManager.getInstance().getBaseUrl());
        return this;
    }

    /**
     * Gets the landing page title text
     * 
     * @return The title text
     */
    public String getTitle() {
        return getText(landingTitle);
    }

    /**
     * Gets the landing page subtitle text
     * 
     * @return The subtitle text
     */
    public String getSubtitle() {
        return getText(landingSubtitle);
    }

    /**
     * Clicks the register button in the page body
     */
    public void clickRegisterButton() {
        click(landingRegisterButton);
    }

    /**
     * Clicks the sign in button in the page body
     */
    public void clickSignInButton() {
        click(landingSignInButton);
    }

    /**
     * Checks if the landing page title is displayed
     * 
     * @return true if the title is displayed
     */
    public boolean isTitleDisplayed() {
        return isElementDisplayed(landingTitle);
    }

    /**
     * Checks if the landing page subtitle is displayed
     * 
     * @return true if the subtitle is displayed
     */
    public boolean isSubtitleDisplayed() {
        return isElementDisplayed(landingSubtitle);
    }

    /**
     * Checks if all feature sections are displayed
     * 
     * @return true if all feature sections are displayed
     */
    public boolean areFeatureSectionsDisplayed() {
        return isElementDisplayed(featureRegistration) &&
                isElementDisplayed(featureLogin) &&
                isElementDisplayed(featureAdminTools);
    }

    /**
     * Checks if the registration dialog is displayed
     * 
     * @return true if the registration dialog is open
     */
    public boolean isRegistrationDialogDisplayed() {
        try {
            return isElementDisplayed(registrationDialog) && isElementDisplayed(registrationForm);
        } catch (Exception e) {
            logger.debug("Registration dialog not found", e);
            return false;
        }
    }

    /**
     * Checks if the current page is the login page
     * 
     * @return true if on the login page
     */
    public boolean isOnLoginPage() {
        // Wait briefly for navigation to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check if URL contains "/login"
        String currentUrl = driver.getCurrentUrl();
        logger.debug("Current URL: {}", currentUrl);
        return currentUrl.contains("/login");
    }
}
