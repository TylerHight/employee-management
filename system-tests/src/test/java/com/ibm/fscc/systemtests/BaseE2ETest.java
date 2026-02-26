package com.ibm.fscc.systemtests;

import com.ibm.fscc.systemtests.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public abstract class BaseE2ETest {

    protected static String jwtToken;

    @BeforeAll
    static void setupBase() {
        RestAssured.baseURI = TestConfig.BASE_URL;
    }

    protected void waitForServiceReady(String path, int expectedStatus, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < timeoutSeconds * 1000L) {
            try {
                int statusCode = given()
                        .when()
                        .get(path)
                        .then()
                        .extract()
                        .statusCode();
                if (statusCode == expectedStatus) {
                    return; // service ready
                }
            } catch (Exception ignored) {
                // ignore until timeout
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new RuntimeException("Service not ready: " + path);
    }

    protected String loginAndGetToken(String email, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}")
                .when()
                .post(TestConfig.LOGIN_PATH)
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
