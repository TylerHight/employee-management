package com.ibm.fscc.systemtests.config;

public class TestConfig {
    public static final String BASE_URL = "http://localhost:8080";

    // --- LOGIN AND EMPLOYEE SERVICES ---
    public static final String LOGIN_PATH = "/api/login/authenticate";
    public static final String LOGIN_HEALTH_PATH = "/api/login/status/check";
    public static final String EMPLOYEES_PATH = "/api/employees";
    public static final String EMPLOYEES_STATUS_PATH = EMPLOYEES_PATH + "/status/check";
    public static final String EMPLOYEE_BY_ID_PATH = EMPLOYEES_PATH + "/%d";
    public static final String EMPLOYEE_FIELD_PATH = EMPLOYEES_PATH + "/%d/%s";

    // Test credentials
    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "password123";

    // --- REGISTRATION SERVICE ---
    public static final String REGISTRATION_PATH = "/api/registration";
    public static final String REGISTRATION_HEALTH_PATH = "/actuator/health";
    public static final String REGISTRATION_STATUS_PATH = "/api/registration/status/check";
}

