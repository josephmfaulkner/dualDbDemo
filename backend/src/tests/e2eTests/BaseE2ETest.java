package com.josephmfaulkner.dualDBDemo.e2eTests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseE2ETest {

    private static final String TARGET_URL = System.getProperty("target.url");

    @LocalServerPort
    private int port;

    @BeforeAll
    void setupConfig() {
        if (TARGET_URL != null && !TARGET_URL.isBlank()) {
            RestAssured.baseURI = TARGET_URL;
        } else {
            RestAssured.baseURI = "http://localhost";
            RestAssured.port = this.port;
        }
    }
}