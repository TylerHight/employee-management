# Test Reports Guide

This guide explains how to run tests and generate test reports for the Employee Service.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Running Tests

### Basic Test Execution

Run all tests:
```bash
mvn test
```

Run tests with clean build:
```bash
mvn clean test
```

### Run Specific Test Class

```bash
# Run entire test class
mvn test -Dtest=EmployeeServiceSpec
```

### Run Specific Test Method

```bash
# Run a single test method (Spock uses feature names)
mvn test -Dtest=EmployeeServiceSpec -Dtest.method="addEmployeeFromClient should generate UUID and save employee"

# Or use wildcards
mvn test -Dtest=EmployeeServiceSpec -Dtest.method="*should generate UUID*"
```

### Run Multiple Test Classes

```bash
mvn test -Dtest=EmployeeServiceSpec,AnotherTestSpec
```

### Run Tests Matching Pattern

```bash
# Run all tests with "Employee" in the name
mvn test -Dtest=*Employee*
```

## Test Reports

The Employee Service generates multiple types of test reports:

### 1. Surefire Reports (Standard Maven)

**Location:** `target/surefire-reports/`

**Generated automatically** when running `mvn test`

**Contents:**
- `TEST-*.xml` - JUnit XML format reports
- `*.txt` - Plain text test results

### 2. Surefire HTML Report

**Location:** `target/site/surefire-report.html`

**Generate:**
```bash
mvn surefire-report:report
```

**Generate with tests:**
```bash
mvn clean test surefire-report:report
```

### 3. Allure Reports (Interactive HTML)

**Location:** `target/site/allure-maven-plugin/`

#### Generate Allure Report

```bash
# Run tests and generate report
mvn clean test allure:report
```

#### View Allure Report

**Option 1: Serve with Auto-Refresh (Recommended for Development)**
```bash
# Starts local server and opens report in browser
mvn allure:serve
```

**How `allure:serve` works:**
- Creates a temporary report from `target/allure-results/`
- Starts a local web server (usually on port 45000+)
- Automatically opens the report in your default browser
- **Important:** Press `Ctrl+C` to stop the server when done

**Do you need to restart it after re-running tests?**
- **Yes**, `allure:serve` creates a temporary report
- After running new tests, stop the server (`Ctrl+C`) and run `mvn allure:serve` again
- The new report will include the latest test results

**Development Workflow:**
```bash
# 1. Run tests
mvn test

# 2. View report (opens browser)
mvn allure:serve

# 3. Make code changes and re-run tests
# Press Ctrl+C to stop the server first
mvn test

# 4. View updated report
mvn allure:serve
```

**Option 2: Generate Static Report (For Sharing/CI)**
```bash
# Generate report once
mvn allure:report

# Open manually in browser
# Location: target/site/allure-maven-plugin/index.html
```

**When to use each option:**
- Use `allure:serve` during development (quick, auto-opens browser)
- Use `allure:report` for CI/CD or when you need a permanent report file

**Option 3: Manual**
Open `target/site/allure-maven-plugin/index.html` in your browser

## Complete Workflow

### Development Workflow (Single Feature)

When working on a specific feature:

```bash
# 1. Run only the test you're working on
mvn test -Dtest=EmployeeServiceSpec -Dtest.method="*should generate UUID*"

# 2. View results quickly
mvn allure:serve

# 3. Make changes, re-run test
# Press Ctrl+C to stop allure:serve first
mvn test -Dtest=EmployeeServiceSpec -Dtest.method="*should generate UUID*"

# 4. View updated results
mvn allure:serve
```

### Full Test Suite Workflow

```bash
# Clean, test, and generate all reports
mvn clean test surefire-report:report allure:report
```

### View Reports

1. **Surefire HTML Report:**
   - Open: `target/site/surefire-report.html`

2. **Allure Interactive Report:**
   - Run: `mvn allure:serve` (development)
   - Or open: `target/site/allure-maven-plugin/index.html` (static)

## Report Locations Summary

| Report Type | Location | Command |
|------------|----------|---------|
| Surefire XML/TXT | `target/surefire-reports/` | `mvn test` |
| Surefire HTML | `target/site/surefire-report.html` | `mvn surefire-report:report` |
| Allure Results | `target/allure-results/` | `mvn test` (auto-generated) |
| Allure HTML | `target/site/allure-maven-plugin/` | `mvn allure:report` |

## Cleaning Up

Remove all generated files including test reports:
```bash
mvn clean
```

This removes the entire `target/` directory, including all test reports.

## Continuous Integration

For CI/CD pipelines, use:
```bash
mvn clean test allure:report
```

Then publish the reports from:
- `target/surefire-reports/` (for CI test result parsing)
- `target/site/allure-maven-plugin/` (for interactive HTML reports)

## Troubleshooting

### Tests Not Running

1. Ensure test files match the pattern: `**/*Spec.class` or `**/*Test.class`
2. Check that tests are in `src/test/groovy/` or `src/test/java/`
3. Verify Groovy files are being compiled: `mvn clean compile test-compile`

### Specific Test Not Running

```bash
# Check test name exactly matches
mvn test -Dtest=EmployeeServiceSpec -X

# For Spock tests, use the exact feature name in quotes
mvn test -Dtest=EmployeeServiceSpec -Dtest.method="exact feature name here"
```

### Allure Report Not Generated

1. Ensure tests ran successfully: `mvn test`
2. Check that `target/allure-results/` contains JSON files
3. Regenerate: `mvn allure:report`

### Allure Serve Port Already in Use

If you see "Address already in use" error:
```bash
# Find and kill the process using the port (Windows)
netstat -ano | findstr :45000
taskkill /PID <process_id> /F

# Or just use a different port
mvn allure:serve -Dallure.serve.port=45001
```

### Report Files Missing

Run a clean build:
```bash
mvn clean test allure:report
```

## Additional Resources

- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [Allure Framework](https://docs.qameta.io/allure/)
- [Spock Framework](https://spockframework.org/)