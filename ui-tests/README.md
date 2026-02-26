# FSCC UI Test Automation

A test automation framework built with Selenium WebDriver, Cucumber BDD, and TestNG for testing the FSCC employee management website. This project demonstrates modern test automation practices with clean architecture and maintainable design.

[![Selenium](https://img.shields.io/badge/Selenium-4.15.0-43B02A)](https://www.selenium.dev/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.14.0-23D96C)](https://cucumber.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8.0-FFC20E)](https://testng.org/)

## Technology Stack

- **Java 11** - Core programming language
- **Selenium WebDriver 4.15.0** - Browser automation
- **Cucumber 7.14.0** - BDD test framework
- **TestNG 7.8.0** - Test execution and management
- **ExtentReports 5.1.1** - Enhanced test reporting
- **Rest Assured 5.3.2** - API testing capabilities
- **Log4j2 2.21.1** - Logging framework
- **PicoContainer** - Dependency injection

## Key Features

### Core Features
- BDD test authoring with Gherkin syntax
- Page Object Model implementation
- Headless browser support (Chrome, Firefox, Edge)
- ExtentReports integration with HTML and PDF reports
- API testing with Rest Assured
- Screenshot capture on test failures

### Technical Capabilities
- Dependency injection with PicoContainer
- Robust text comparison with whitespace handling
- Custom assertion utilities
- Element name annotations for enhanced debugging
- Comprehensive logging

## Quick Start

```bash
# Clone the repository
git clone [repository-url]

# Navigate to project directory
cd qa-automation-playground

# Run tests
mvn clean test

# Run in headless mode
mvn test -Dheadless=true

# Run specific test runner
mvn test -Dtest=HomePageTestRunner
```

## Configuration

Basic configuration options (see [Configuration Guide](CODE_DEVELOPMENT_GUIDE.md#configuration) for details):

```properties
# Main configuration properties
baseUrl=http://www.uitestingplayground.com
browser=chrome
headless=false
```

## Test Coverage

Currently implemented test scenarios:

- ✅ Homepage title verification
- ✅ Homepage navigation elements
- ✅ Test scenario links verification
- ✅ Dynamic ID page interactions

## Documentation

- [**Code Development Guide**](CODE_DEVELOPMENT_GUIDE.md) - Detailed development standards, patterns and examples
- [**Project Structure**](CODE_DEVELOPMENT_GUIDE.md#core-project-structure) - Directory organization and architecture
- [**Configuration Guide**](CODE_DEVELOPMENT_GUIDE.md#configuration) - All available configuration options
- [**Development Guide**](CODE_DEVELOPMENT_GUIDE.md#creating-new-tests) - How to create new tests

## Reporting

Test execution reports are available at:

- **ExtentReports HTML**: `test-output/SparkReport/Spark.html`
- **ExtentReports PDF**: `test-output/PdfReport/ExtentPdf.pdf`
- **Cucumber Reports**: `target/cucumber-reports/html`
- **TestNG Reports**: `target/surefire-reports/`

## Project Status

**Current Version**: 0.2.0 - ExtentReports and Headless Mode Integration  
**Next Milestone**: Multi-browser and environment support  
**Target**: Enterprise-ready test automation framework

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- Chrome browser (default, others configurable)
- Internet connection for accessing UI Testing Playground

## Contributing

Please read our [Code Development Guide](CODE_DEVELOPMENT_GUIDE.md) for details on coding standards, patterns, and best practices.

- Follow established coding standards
- Write clear commit messages using conventional commits
- Include appropriate test coverage
- Update documentation for changes

## Roadmap

### Coming Soon
- Multi-browser support for Firefox, Edge, and Safari
- Environment-specific configurations
- Complete UI Testing Playground coverage
- Enhanced API testing scenarios

### Future Plans
- Parallel test execution
- Docker containerization
- CI/CD integration
- Selenium Grid support
- Database validation capabilities

---

*This framework demonstrates modern test automation practices with clean architecture, maintainable code, and professional development standards suitable for enterprise environments.*
