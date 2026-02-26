package com.ibm.fscc.systemtests;

import com.ibm.fscc.systemtests.config.TestConfig;

import static com.ibm.fscc.systemtests.util.TestDataFactory.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistrationAccessIT extends BaseE2ETest {

    private static String testEmail;

    @BeforeAll
    static void setup() {
        RegistrationAccessIT testInstance = new RegistrationAccessIT();

        // Wait for login-service to be ready
        testInstance.waitForServiceReady(TestConfig.LOGIN_HEALTH_PATH, 200, 60);
        // Login and get JWT token
        jwtToken = testInstance.loginAndGetToken(TestConfig.TEST_EMAIL, TestConfig.TEST_PASSWORD);

        // Wait for registration-service to be ready
        testInstance.waitForServiceReady(TestConfig.REGISTRATION_HEALTH_PATH, 200, 60);

        // Unique email for the test run
        testEmail = "testuser+" + System.currentTimeMillis() + "@example.com";
    }

    @Test
    @Order(1)
    void shouldRegisterEmployee() {
        given()
            .contentType(ContentType.JSON)
            .body(buildRegistrationRequest("John", "Doe", testEmail))
        .when()
            .post(TestConfig.REGISTRATION_PATH)
        .then()
            .statusCode(201)
            .body("email", equalTo(testEmail))
            .body("status", equalTo("PENDING"));
    }

    @Test
    @Order(2)
    void shouldGetEmployeeByEmail() {
        given()
            .header("Authorization", "Bearer " + jwtToken)
            .accept(ContentType.JSON)
        .when()
            .get(TestConfig.REGISTRATION_PATH + "/" + testEmail)
        .then()
            .statusCode(200)
            .body("email", equalTo(testEmail))
            .body("status", equalTo("PENDING"));
    }

    @Test
    @Order(3)
    void shouldApproveRegistration() {
        given()
            .header("Authorization", "Bearer " + jwtToken)
        .when()
            .post(TestConfig.REGISTRATION_PATH + "/" + testEmail + "/approve")
        .then()
            .statusCode(200)
            .body("status", equalTo("APPROVED"));
    }

    @Test
    @Order(4)
    void shouldCancelRegistration() {
        String cancelEmail = "cancel+" + System.currentTimeMillis() + "@example.com";

        // First, register (public)
        given()
            .contentType(ContentType.JSON)
            .body(buildRegistrationRequest("Jane", "Doe", cancelEmail))
        .when()
            .post(TestConfig.REGISTRATION_PATH)
        .then()
            .statusCode(201);

        // Then cancel
        given()
            .header("Authorization", "Bearer " + jwtToken)
        .when()
            .delete(TestConfig.REGISTRATION_PATH + "/" + cancelEmail)
        .then()
            .statusCode(204);
    }
}
