Feature: Landing Page functionality
    As a user
    I want to access the landing page
    So that I can view the application information and navigate to other pages

  @smoke @landing
  Scenario: Verify landing page loads with correct title
    Given I navigate to the landing page
    When I view the page title
    Then the page title should be displayed correctly
    And the page title should not be empty

  @smoke @landing
  Scenario: Verify landing page subtitle is displayed
    Given I navigate to the landing page
    When I view the page subtitle
    Then the page subtitle should be displayed correctly

  @regression @landing
  Scenario: Verify feature sections are displayed
    Given I navigate to the landing page
    Then all feature sections should be displayed

  @regression @landing
  Scenario: Verify registration button opens registration dialog
    Given I navigate to the landing page
    When I click the register button
    Then the registration dialog should be displayed

  @regression @landing
  Scenario: Verify sign in button navigates to login page
    Given I navigate to the landing page
    When I click the sign in button
    Then I should be redirected to the login page