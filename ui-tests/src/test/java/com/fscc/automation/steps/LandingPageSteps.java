package com.fscc.automation.steps;

import com.fscc.automation.pages.landingpage.LandingPage;
import com.fscc.automation.utils.AssertionUtils;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Step definitions for the Landing Page feature
 */
public class LandingPageSteps {

    private static final Logger logger = LogManager.getLogger(LandingPageSteps.class);

    private LandingPage landingPage;
    private String actualTitle;
    private String actualSubtitle;

    public LandingPageSteps(LandingPage landingPage) {
        this.landingPage = landingPage;
    }

    @Given("I navigate to the landing page")
    public void i_navigate_to_the_landing_page() {
        logger.info("Navigating to landing page");
        landingPage.open();
    }

    @When("I view the page title")
    public void i_view_the_page_title() {
        logger.info("Retrieving page title");
        actualTitle = landingPage.getTitle();
        logger.debug("Actual title: {}", actualTitle);
    }

    @Then("the page title should be displayed correctly")
    public void the_page_title_should_be_displayed_correctly() {
        logger.info("Validating the page title is displayed");
        AssertionUtils.assertTrue(landingPage.isTitleDisplayed(), "Page title should be displayed");
    }

    @Then("the page title should not be empty")
    public void the_page_title_should_not_be_empty() {
        logger.info("Validating that the page title is not empty");
        AssertionUtils.assertNotNullOrEmpty(actualTitle, "Page Title");
    }

    @When("I view the page subtitle")
    public void i_view_the_page_subtitle() {
        logger.info("Retrieving page subtitle");
        actualSubtitle = landingPage.getSubtitle();
        logger.debug("Actual subtitle: {}", actualSubtitle);
    }

    @Then("the page subtitle should be displayed correctly")
    public void the_page_subtitle_should_be_displayed_correctly() {
        logger.info("Validating the page subtitle is displayed");
        AssertionUtils.assertTrue(landingPage.isSubtitleDisplayed(), "Page subtitle should be displayed");
    }

    @Then("all feature sections should be displayed")
    public void all_feature_sections_should_be_displayed() {
        logger.info("Validating all feature sections are displayed");
        AssertionUtils.assertTrue(landingPage.areFeatureSectionsDisplayed(),
                "All feature sections should be displayed");
    }

    @When("I click the register button")
    public void i_click_the_register_button() {
        logger.info("Clicking the register button");
        landingPage.clickRegisterButton();
    }

    @Then("the registration dialog should be displayed")
    public void the_registration_dialog_should_be_displayed() {
        logger.info("Verifying registration dialog is displayed");
        // You'll need to implement this method in LandingPage.java
        AssertionUtils.assertTrue(landingPage.isRegistrationDialogDisplayed(),
                "Registration dialog should be displayed");
    }

    @When("I click the sign in button")
    public void i_click_the_sign_in_button() {
        logger.info("Clicking the sign in button");
        landingPage.clickSignInButton();
    }

    @Then("I should be redirected to the login page")
    public void i_should_be_redirected_to_the_login_page() {
        logger.info("Verifying redirection to login page");
        // You'll need to implement this method in LandingPage.java
        AssertionUtils.assertTrue(landingPage.isOnLoginPage(),
                "Should be redirected to login page");
    }

}