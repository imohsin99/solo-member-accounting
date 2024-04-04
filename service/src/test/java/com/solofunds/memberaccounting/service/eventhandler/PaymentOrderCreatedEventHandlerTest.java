package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.paymentOrder.PaymentOrderCreateEvent;
import com.solofunds.memberaccounting.model.CreatePaymentOrderDto;
import com.solofunds.memberaccounting.model.PaymentOrderDto;
import com.solofunds.memberaccounting.service.service.PaymentOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentOrderCreatedEventHandlerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // Allows the objectMapper to ignore the "destination" field
            .build();

    @Mock
    PaymentOrderService mockPaymentOrderService;

    @InjectMocks
    PaymentOrderCreatedEventHandler paymentOrderCreatedEventHandler;

    @Test
    void paymentOrderCreatedEventHandlerSuccess() throws Exception {
        fixture.givenCreatePaymentOrderEventIsProvided();
        fixture.givenMessageStringIsProvided();
        fixture.givenPaymentOrderServiceReturnsDto();
        fixture.whenConsumeMessage();
        fixture.thenVerifyExpectedJsonIsReturned();
    }

    @Test
    void paymentOrderCreatedEventHandlerRuntimeException() throws Exception {
        fixture.givenCreatePaymentOrderEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();
        fixture.whenConsumeMessageVerifyExceptionIsThrown();
    }

    Fixture fixture;

    @BeforeEach
    void setUp() {
        paymentOrderCreatedEventHandler = new PaymentOrderCreatedEventHandler(objectMapper, mockPaymentOrderService);
        fixture = new Fixture();
    }

    @Nested
    private class Fixture {
        UUID paymentOrderDtoId = UUID.randomUUID();
        PaymentOrderCreateEvent paymentOrderCreateEvent;
        Message<String> createPaymentOrderRequest;
        PaymentOrderDto paymentOrderDto;

        String jsonRes;

        void givenCreatePaymentOrderEventIsProvided() {
            CreatePaymentOrderDto createPaymentOrderDto = new CreatePaymentOrderDto();
            paymentOrderCreateEvent = new PaymentOrderCreateEvent();
            paymentOrderCreateEvent.setCreatePaymentOrderDto(createPaymentOrderDto);
        }

        void givenMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String paymentOrderCreateEventJson = objectMapper.writeValueAsString(paymentOrderCreateEvent);
            createPaymentOrderRequest = (Message<String>) simpleMessageConverter.toMessage(paymentOrderCreateEventJson, null);
        }

        void givenMalformedMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String paymentOrderCreateEventJson = objectMapper.writeValueAsString("test");
            createPaymentOrderRequest = (Message<String>) simpleMessageConverter.toMessage(paymentOrderCreateEventJson, null);
        }

        void givenPaymentOrderServiceReturnsDto() throws Exception{
            paymentOrderDto = new PaymentOrderDto();
            paymentOrderDto.setId(paymentOrderDtoId);
            when(mockPaymentOrderService.createPaymentOrder(any(CreatePaymentOrderDto.class))).thenReturn(paymentOrderDto);
        }

        void whenConsumeMessage() {
            jsonRes = paymentOrderCreatedEventHandler.consumeMessage(createPaymentOrderRequest);
        }

        void thenVerifyExpectedJsonIsReturned() throws JsonProcessingException {
            assertNotNull(jsonRes);
            PaymentOrderDto paymentOrderDtoRes = objectMapper.readValue(jsonRes, PaymentOrderDto.class);
            assertEquals(paymentOrderDtoId, paymentOrderDtoRes.getId());
        }

        void whenConsumeMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> paymentOrderCreatedEventHandler.consumeMessage(createPaymentOrderRequest));
        }

    }
}
