package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.paymentOrder.PaymentOrderCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreatePaymentOrderDto;
import com.solofunds.memberaccounting.model.PaymentOrderDto;
import com.solofunds.memberaccounting.service.service.PaymentOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentOrderEventHandlerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // Allows the objectMapper to ignore the "destination" field
            .build();

    @Autowired
    EventFactory eventFactory;

    @Mock
    PaymentOrderService mockPaymentOrderService;

    @InjectMocks
    PaymentOrderEventHandler paymentOrderdEventHandler;

    @Test
    void paymentOrderCreatedEventHandlerSuccess() throws Exception {
        fixture.givenPaymentOrderEventIsProvided();
        fixture.givenCreateMessageStringIsProvided();
        fixture.givenPaymentOrderServiceReturnsDto();
        fixture.whenConsumeCreateMessage();
        fixture.thenVerifyExpectedCreatedJsonIsReturned();
    }

    @Test
    void paymentOrderCreatedEventHandlerRuntimeException() throws Exception {
        fixture.givenPaymentOrderEventIsProvided();
        fixture.givenMalformedCreateMessageStringIsProvided();
        fixture.whenConsumeCreateMessageVerifyExceptionIsThrown();
    }

    @Test
    void paymentOrderGetEventHandlerSuccess() throws Exception {
        fixture.givenPaymentOrderGetEventIsProvided();
        fixture.givenMessageStringIsProvided();
        fixture.givenPaymentOrderServiceReturnsDtos();
        fixture.whenConsumeMessage();
        fixture.thenVerifyExpectedJsonIsReturned();
    }

    @Test
    void paymentOrderGetEventHandlerRuntimeException() throws Exception {
        fixture.givenPaymentOrderGetEventIsProvided();
        fixture.givenMalformedGetMessageStringIsProvided();
        fixture.whenConsumeMessageVerifyExceptionIsThrown();
    }

    Fixture fixture;

    @BeforeEach
    void setUp() {
        paymentOrderdEventHandler = new PaymentOrderEventHandler(objectMapper, mockPaymentOrderService);
        fixture = new Fixture();
    }

    @Nested
    private class Fixture {
        UUID paymentOrderDtoId = UUID.randomUUID();
        UUID walletAccountId = UUID.randomUUID();
        PaymentOrderCreateEvent paymentOrderCreateEvent;
        Message<String> createPaymentOrderRequest;
        PaymentOrderDto paymentOrderDto;
        ResourceByIdGetEvent paymentOrderGetEvent;
        Message<String> getPaymentOrderRequest;
        List<PaymentOrderDto> paymentOrderDtos = new ArrayList<>();
        String jsonRes;

        void givenPaymentOrderEventIsProvided() {
            CreatePaymentOrderDto createPaymentOrderDto = new CreatePaymentOrderDto();
            paymentOrderCreateEvent = new PaymentOrderCreateEvent();
            paymentOrderCreateEvent.setCreatePaymentOrderDto(createPaymentOrderDto);
        }

        void givenCreateMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String paymentOrderCreateEventJson = objectMapper.writeValueAsString(paymentOrderCreateEvent);
            createPaymentOrderRequest = (Message<String>) simpleMessageConverter.toMessage(paymentOrderCreateEventJson, null);
        }

        void givenMalformedCreateMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String paymentOrderCreateEventJson = objectMapper.writeValueAsString("test");
            createPaymentOrderRequest = (Message<String>) simpleMessageConverter.toMessage(paymentOrderCreateEventJson, null);
        }

        void givenPaymentOrderServiceReturnsDto() throws Exception{
            paymentOrderDto = new PaymentOrderDto();
            paymentOrderDto.setId(paymentOrderDtoId);
            when(mockPaymentOrderService.createPaymentOrder(any(CreatePaymentOrderDto.class))).thenReturn(paymentOrderDto);
        }

        void whenConsumeCreateMessage() {
            jsonRes = paymentOrderdEventHandler.consumeCreateMessage(createPaymentOrderRequest);
        }

        void thenVerifyExpectedCreatedJsonIsReturned() throws JsonProcessingException {
            assertNotNull(jsonRes);
            PaymentOrderDto paymentOrderDtoRes = objectMapper.readValue(jsonRes, PaymentOrderDto.class);
            assertEquals(paymentOrderDtoId, paymentOrderDtoRes.getId());
        }

        void whenConsumeCreateMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> paymentOrderdEventHandler.consumeCreateMessage(createPaymentOrderRequest));
        }

        void givenPaymentOrderGetEventIsProvided() {
            paymentOrderGetEvent = eventFactory.buildPaymentOrderByWalletAccountIdGetEvent(walletAccountId);
        }

        void givenMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String paymentOrderGetEventJson = objectMapper.writeValueAsString(paymentOrderGetEvent);
            getPaymentOrderRequest = (Message<String>) simpleMessageConverter.toMessage(paymentOrderGetEventJson, null);
        }

        void givenMalformedGetMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String paymentOrderCreateEventJson = objectMapper.writeValueAsString("test");
            getPaymentOrderRequest = (Message<String>) simpleMessageConverter.toMessage(paymentOrderCreateEventJson, null);
        }

        void givenPaymentOrderServiceReturnsDtos() throws Exception{
            paymentOrderDto = new PaymentOrderDto();
            paymentOrderDto.setId(paymentOrderDtoId);
            paymentOrderDtos.add(paymentOrderDto);
            when(mockPaymentOrderService.getPaymentOrdersByWalletAccountId(any(UUID.class))).thenReturn(paymentOrderDtos);
        }

        void whenConsumeMessage() {
            jsonRes = paymentOrderdEventHandler.consumeGetMessage(getPaymentOrderRequest);
        }

        void thenVerifyExpectedJsonIsReturned() throws JsonProcessingException {
            assertNotNull(jsonRes);
            List<PaymentOrderDto> paymentOrderDtoRes = objectMapper.readValue(jsonRes, new TypeReference<>() {});
            assertEquals(paymentOrderDtoId, paymentOrderDtoRes.get(0).getId());
        }

        void whenConsumeMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> paymentOrderdEventHandler.consumeGetMessage(getPaymentOrderRequest));
        }

    }
}
