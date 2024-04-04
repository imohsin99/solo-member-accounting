//package com.solofunds.memberaccounting.gateway.controller;
//
//import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
//import com.solofunds.memberaccounting.messaging.messenger.event.Event;
//import com.solofunds.memberaccounting.model.HealthCheck;
//import jakarta.jms.JMSException;
//import jakarta.validation.constraints.NotNull;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class HealthcheckControllerTest {
//
//    @Mock
//    MessagePublisher<Event> mockPublisher;
//
//    @InjectMocks
//    HealthCheckControllerImpl healthcheckController;
//
//    public HealthcheckControllerTest() {
//    }
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testHealthcheckSuccess() throws Exception {
//
//        when(mockPublisher.publishAndWait(any(Event.class), any(String.class))).thenReturn("healthcheck test response");
//        ResponseEntity<HealthCheck> res = healthcheckController.healthCheck();
//        Assertions.assertTrue(res.getStatusCode().is2xxSuccessful());
//        @NotNull
//        HealthCheck body = res.getBody();
//        Assertions.assertTrue(body.getHealthCheckValue().contains("healthcheck test response"));
//    }
//
//    @Test
//    public void testHealthcheckFailure() throws Exception {
//        when(mockPublisher.publishAndWait(any(Event.class), any(String.class))).thenThrow(new JMSException("test error"));
//        ResponseEntity<HealthCheck> res = healthcheckController.healthCheck();
//        Assertions.assertTrue(res.getStatusCode().is4xxClientError());
//        Assertions.assertTrue(res.getBody().getHealthCheckValue().contains("test error"));
//    }
//
//}