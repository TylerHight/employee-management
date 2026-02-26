# CODE_DEVELOPMENT_GUIDE.md

## Project Overview
This guide provides essential information for developing tests with this framework, a Selenium WebDriver test automation solution using Cucumber BDD and TestNG.

## Core Project Structure
```
src/
├── main/java/com/fscc/automation/
│   ├── constants/          # Page constants, selectors, messages
│   ├── core/               # Framework foundation
│   ├── models/             # Data models
│   ├── services/           # Service layer for API interactions
│   └── pages/              # Page Objects
└── test/
    ├── java/com/fscc/automation/
    │   ├── runners/        # TestNG Cucumber runners
    │   ├── steps/          # BDD step definitions 
    │   ├── utils/          # Test utilities
    │   └── hooks/          # Test lifecycle hooks
    └── resources/
        ├── features/       # Cucumber feature files
        ├── extent.properties # ExtentReports config
        ├── log4j2.xml      # Logging config
        └── testng.xml      # TestNG config
```

## Key Design Patterns & Components

### Page Object Pattern
```java
public class HomePage extends BasePage {
    @ElementName("Page Title")
    private By pageTitle = By.xpath(HomePageSelectors.PAGE_TITLE);
    
    public HomePage() {
        super();
    }
    
    public String getPageTitleText() {
        logger.info("Retrieving page title");
        return getText(pageTitle);
    }
    
    public HomePage open() {
        navigateTo(UrlConstants.HOME_PAGE_URL);
        return this;
    }
}
```

### Step Definitions Pattern
```java
public class HomePageSteps {
    private HomePage homePage;
    private String actualTitle;
    
    // Constructor injection using PicoContainer
    public HomePageSteps(HomePage homePage) {
        this.homePage = homePage;
    }
    
    @Given("I navigate to the homepage")
    public void i_navigate_to_the_homepage() {
        logger.info("Navigating to homepage");
        homePage.open();
    }
    
    @When("I view the page title")
    public void i_view_the_page_title() {
        logger.info("Retrieving page title");
        actualTitle = homePage.getPageTitleText();
    }
    
    @Then("the page title should be displayed correctly")
    public void the_page_title_should_be_displayed_correctly() {
        AssertionUtils.assertTitle(
            actualTitle,
            HomePageConstants.HOME_PAGE_TITLE,
            String.format(ValidationMessages.TITLE_VERIFICATION_FAILED, 
                          HomePageConstants.HOME_PAGE_TITLE, actualTitle)
        );
    }
}
```

### Test Runner Configuration
```java
@CucumberOptions(
    features = "src/test/resources/features/ui",
    glue = {
        "com.fscc.automation.steps.ui",
        "com.fscc.automation.hooks"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/html",
        "json:target/cucumber-reports/json/cucumber.json",
        "junit:target/cucumber-reports/junit/cucumber.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    tags = "@smoke or @homepage"
)
public class HomePageTestRunner extends AbstractTestNGCucumberTests {
}
```

## Common Utils & Methods Reference

### BasePage Methods
- `click(By locator)`: Click with logging and reporting
- `sendKeys(By locator, String text)`: Type text
- `getText(By locator)`: Get element text
- `isElementDisplayed(By locator)`: Check visibility
- `waitForElementVisible(By locator)`: Explicit wait

### AssertionUtils Methods
- `assertEquals(actual, expected, message)`
- `assertNotNullOrEmpty(text, elementName)`
- `assertTrue(condition, message)`
- `assertTitle(actual, expected, message)`

### Configuration Access
- `ConfigManager.getInstance().getProperty(key)`
- `ConfigManager.getInstance().getBrowser()`
- `ConfigManager.getInstance().isHeadless()`
- `DriverManager.getDriver()`

### Reporting Methods
- `ReportManager.logInfo(message)`
- `ReportManager.logPass(message)`
- `ReportManager.logFail(message)`

## Creating New Tests

### UI Tests
1. Add selectors to appropriate Selectors class
2. Add constants to Constants class if needed
3. Create/update Page Object with element interactions
4. Write feature file in features/ui directory
5. Implement step definitions in steps/ui package
6. Add to appropriate UI test runner with relevant tags

### API Tests
1. Create model classes for request/response
2. Create service classes for API interactions
3. Write feature file in features/api directory
4. Implement API step definitions in steps/api package
5. Add to API test runner with relevant tags

## Configuration Options

### Core Settings
```properties
# config.properties
baseUrl=http://www.uitestingplayground.com
browser=chrome
headless=false
explicitWait=10
implicitWait=5
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test runner
mvn test -Dtest=HomePageTestRunner

# Run tests in headless mode
mvn test -Dheadless=true
```

## Best Practices

### Code Organization
- One page object per page/component
- Group related step definitions
- Use constants for locators and messages
- Keep feature files focused on user scenarios
- Follow naming conventions consistently

### Test Maintenance
- Use robust selectors (IDs, unique attributes)
- Implement proper wait strategies
- Avoid hardcoded values
- Write descriptive assertion messages
- Use tags to organize test execution

### Logging
- Use appropriate log levels
- Log before and after critical actions
- Include context in log messages
- Avoid excessive logging

---

**Last Updated**: October 2025  
**Version**: 0.2.0

