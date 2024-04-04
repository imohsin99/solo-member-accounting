package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.balance.CalculateStartingBalanceEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.BalanceDto;
import com.solofunds.memberaccounting.model.CalculateStartingBalanceDto;
import com.solofunds.memberaccounting.service.repositories.StartingBalanceRepository;
import com.solofunds.memberaccounting.service.service.BalanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class BalanceEventHandlerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    @Autowired
    EventFactory eventFactory;

    @Mock
    StartingBalanceRepository startingBalanceRepository;

    @Mock
    BalanceService balanceService;

    BalanceEventHandler balanceEventHandler;

    @Test
    void testGetBalanceEventHandlerSuccess() throws Exception {
        fixture.givenGetBalanceEventIsProvided();
        fixture.givenGetBalanceMessageStringIsProvided();
        fixture.givenBalanceServiceReturnsBalanceRequestDto();
        fixture.whenConsumeGetBalanceMessageIsCalled();
        fixture.thenVerifyExpectedBalanceRequestDtoJsonResponseIsReturned();
    }

    @Test
    void testGetBalanceEventHandlerRuntimeException() throws Exception {
        fixture.givenGetBalanceEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();
        fixture.whenConsumeGetBalanceMessageVerifyExceptionIsThrown();
    }

    @Test
    void testCalculateStartingBalanceEventHandlerSuccess() throws Exception {
        fixture.givenCalculateStartingBalanceEventIsProvided();
        fixture.givenCalculateStartingBalanceMessageStringIsProvided();
        fixture.whenConsumeCalculateStartingBalanceMessageIsCalled();
        fixture.thenVerifyExpectedCalculateStartingBalanceJsonResponseIsReturned();
    }

    @Test
    void testCalculateStartingBalanceEventHandlerRuntimeException() throws Exception {
        fixture.givenCalculateStartingBalanceEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();
        fixture.whenConsumeCalculateStartingBalanceMessageVerifyExceptionIsThrown();
    }

    Fixture fixture;

    @BeforeEach
    void setUp() {
        balanceEventHandler = new BalanceEventHandler(startingBalanceRepository, balanceService, objectMapper);
        fixture = new Fixture();
    }

    @Nested
    private class Fixture {
        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();
        ResourceByIdGetEvent getBalanceEvent;
        CalculateStartingBalanceEvent calculateStartingBalanceEvent;
        Message<String> eventRequest;
        BalanceDto balanceDto;
        BigDecimal nineNine = new BigDecimal("99.99");
        String currencyCode = "USD";
        OffsetDateTime today = OffsetDateTime.now();
        OffsetDateTime tomorrow = OffsetDateTime.now().plusDays(1);

        String response;

        void givenGetBalanceEventIsProvided() {
            getBalanceEvent = eventFactory.buildBalanceByWalletAccountIdGetEvent(WALLET_ACCOUNT_ID);
        }

        void givenCalculateStartingBalanceEventIsProvided() {
            CalculateStartingBalanceDto calculateStartingBalanceDTO = new CalculateStartingBalanceDto();
            calculateStartingBalanceDTO.setStartTime(today);
            calculateStartingBalanceDTO.setEndTime(tomorrow);
            calculateStartingBalanceEvent = new CalculateStartingBalanceEvent();
            calculateStartingBalanceEvent.setCalculateStartingBalanceDto(calculateStartingBalanceDTO);
        }

        void givenGetBalanceMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getBalanceEventJson = objectMapper.writeValueAsString(getBalanceEvent);
            eventRequest = (Message<String>) simpleMessageConverter.toMessage(getBalanceEventJson, null);
        }

        void givenCalculateStartingBalanceMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String calculateStartingBalanceEventJson = objectMapper.writeValueAsString(calculateStartingBalanceEvent);
            eventRequest = (Message<String>) simpleMessageConverter.toMessage(calculateStartingBalanceEventJson, null);
        }

        void givenBalanceServiceReturnsBalanceRequestDto() throws Exception {
            balanceDto = new BalanceDto();
            balanceDto.setAvailableBalance(nineNine);
            balanceDto.setPendingBalance(nineNine);
            balanceDto.setSoloCreditBalance(nineNine);
            balanceDto.setCurrencyCode(currencyCode);
            when(balanceService.getBalanceByWalletAccountId(WALLET_ACCOUNT_ID)).thenReturn(balanceDto);
        }

        void whenConsumeGetBalanceMessageIsCalled() {
            response = balanceEventHandler.consumeGetBalanceMessage(eventRequest);
        }

        void whenConsumeCalculateStartingBalanceMessageIsCalled() throws Exception {
            response = balanceEventHandler.consumeCalculateStartingBalanceMessage(eventRequest);
        }

        void thenVerifyExpectedBalanceRequestDtoJsonResponseIsReturned() throws JsonProcessingException {
            assertNotNull(response);
            BalanceDto balanceDtoResponse = objectMapper.readValue(response, BalanceDto.class);
            assertEquals(nineNine, balanceDtoResponse.getAvailableBalance());
            assertEquals(nineNine, balanceDtoResponse.getPendingBalance());
            assertEquals(nineNine, balanceDtoResponse.getSoloCreditBalance());
            assertEquals(currencyCode, balanceDtoResponse.getCurrencyCode());
        }

        void thenVerifyExpectedCalculateStartingBalanceJsonResponseIsReturned() {
            assertNotNull(response);
            assertTrue(response.contains("calculate_starting_balance executed successfully"));
        }

        void givenMalformedMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String fundingProposalUpdateEventJson = objectMapper.writeValueAsString("test");
            eventRequest = (Message<String>) simpleMessageConverter.toMessage(fundingProposalUpdateEventJson, null);
        }

        void whenConsumeGetBalanceMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> balanceEventHandler.consumeGetBalanceMessage(eventRequest));
        }

        void whenConsumeCalculateStartingBalanceMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> balanceEventHandler.consumeCalculateStartingBalanceMessage(eventRequest));
        }
    }
}
