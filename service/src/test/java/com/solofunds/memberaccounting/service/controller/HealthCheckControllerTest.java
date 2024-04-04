package com.solofunds.memberaccounting.service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class HealthCheckControllerTest {

    @Autowired
    HealthCheckController healthCheckController;

    @Test
    public void healthCheckTest() {
        ResponseEntity<String> res = healthCheckController.healthCheck();
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals("Healthcheck successful", res.getBody());
    }
}
