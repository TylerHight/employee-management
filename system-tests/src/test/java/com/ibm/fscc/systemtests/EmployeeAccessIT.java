package com.ibm.fscc.systemtests;

import com.ibm.fscc.systemtests.config.TestConfig;
import com.ibm.fscc.systemtests.dto.EmployeeUpdateDto;

import static com.ibm.fscc.systemtests.util.TestDataFactory.*;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeAccessIT extends BaseE2ETest {

    private static Long createdEmployeeId;

    @BeforeAll
    static void initTokenAndEmployee() {
        EmployeeAccessIT testInstance = new EmployeeAccessIT();
        // Wait for login service
        testInstance.waitForServiceReady(TestConfig.LOGIN_HEALTH_PATH, 200, 60);
        jwtToken = testInstance.loginAndGetToken(TestConfig.TEST_EMAIL, TestConfig.TEST_PASSWORD);

        // Create initial employee with unique email
        String uniqueEmail = "cowfield+" + System.currentTimeMillis() + "@example.com";
        createdEmployeeId = given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(buildEmployeeDto("Tyler", "Cows", uniqueEmail))
                .when()
                .post(TestConfig.EMPLOYEES_PATH)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo("Tyler"))
                .extract()
                .jsonPath()
                .getLong("id");
    }

    @Test
    void shouldLoginAndAccessEmployees() {
        given()
                .header("Authorization", "Bearer " + jwtToken)
                .accept(ContentType.JSON)
                .when()
                .get(TestConfig.EMPLOYEES_PATH)
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    @Test
    void shouldRejectAccessWithoutLogin() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get(TestConfig.EMPLOYEES_PATH)
                .then()
                .statusCode(401);
    }

    @Test
    void shouldAddEmployee() {
        String uniqueEmail = "supercows+" + System.currentTimeMillis() + "@example.com";
        given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(buildEmployeeDto("Alice", "Smith", uniqueEmail))
                .when()
                .post(TestConfig.EMPLOYEES_PATH)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo("Alice"));
    }

    @Test
    @Order(1)
    void shouldGetEmployeeField() {
        given()
                .header("Authorization", "Bearer " + jwtToken)
                .accept(ContentType.JSON)
                .when()
                .get(String.format(TestConfig.EMPLOYEE_FIELD_PATH, createdEmployeeId, "firstName"))
                .then()
                .statusCode(200)
                .body(equalTo("Tyler")); // Controller returns plain string
    }

    @Test
    @Order(2)
    void shouldUpdateEmployee() {
        EmployeeUpdateDto updateDto = buildEmployeeUpdateDto(null, "Updated");

        given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(updateDto)
                .when()
                .patch(String.format(TestConfig.EMPLOYEE_BY_ID_PATH, createdEmployeeId))
                .then()
                .statusCode(200)
                .body("lastName", equalTo("Updated"));
    }

    @Test
    @Order(3)
    void shouldDeleteEmployee() {
        given()
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(String.format(TestConfig.EMPLOYEE_BY_ID_PATH, createdEmployeeId))
                .then()
                .statusCode(204);
    }

    @Test
    void shouldReturnStatusCheckMessage() {
        given()
                .header("Authorization", "Bearer " + jwtToken)
                .accept(ContentType.TEXT)
                .when()
                .get(TestConfig.EMPLOYEES_STATUS_PATH)
                .then()
                .statusCode(200)
                .body(containsString("Working on port"));
    }
}
