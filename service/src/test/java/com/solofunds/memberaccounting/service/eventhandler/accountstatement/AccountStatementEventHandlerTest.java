package com.solofunds.memberaccounting.service.eventhandler.accountstatement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.accountStatement.AccountStatementDeleteEvent;
import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.service.eventhandler.accountStatement.AccountStatementEventHandler;
import com.solofunds.memberaccounting.service.service.impl.AccountStatementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AccountStatementEventHandlerTest {
    @Mock
    AccountStatementServiceImpl accountStatementService;

    @InjectMocks
    AccountStatementEventHandler accountStatementEventHandler;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    Fixture fixture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fixture = new Fixture();
        accountStatementEventHandler = new AccountStatementEventHandler(accountStatementService, objectMapper);
    }

    @Test
    void testConsumeDeleteMessage() throws JsonProcessingException {
        fixture.givenAccountStatementDeleteEventIsProvided();
        fixture.givenDeleteMessageStringIsProvided();
        fixture.givenAccountStatementServiceReturnsDeleteAccountStatement();

        fixture.whenConsumeDeleteMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturnedForDeleteEvent();
    }

    @Test
    void testConsumeDeleteMessage_Exception() throws Exception {
        fixture.givenAccountStatementDeleteEventIsProvided();
        fixture.givenMalformedDeleteMessageStringIsProvided();

        fixture.whenConsumeDeleteMessageVerifyExceptionIsThrown();
    }


    private class Fixture{
        final UUID ACCOUNT_STATEMENT_ID = UUID.randomUUID();

        AccountStatementDeleteEvent accountStatementDeleteEvent;

        AccountStatementDto accountStatementDTO;

        Message<String> deleteRequest;

        Message<String> getRequest;

        String response;

        public void givenAccountStatementDeleteEventIsProvided(){
            accountStatementDeleteEvent=AccountStatementDeleteEvent
                    .builder()
                    .id(ACCOUNT_STATEMENT_ID)
                    .build();
        }
        public void givenDeleteMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter=new SimpleMessageConverter();
            String json=objectMapper.writeValueAsString(accountStatementDeleteEvent);
            deleteRequest=(Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void givenAccountStatementServiceReturnsDeleteAccountStatement() {
            accountStatementDTO=new AccountStatementDto();
            accountStatementDTO.setId(ACCOUNT_STATEMENT_ID);
            when(accountStatementService.deleteAccountStatementById(ACCOUNT_STATEMENT_ID)).thenReturn(accountStatementDTO);
        }

        public void whenConsumeDeleteMessageIsCalled() {
            response=accountStatementEventHandler.consumeDeleteMessage(deleteRequest);
        }

        public void thenVerifyExpectedJsonResponseIsReturnedForDeleteEvent() throws JsonProcessingException {
            assertNotNull(response);
            AccountStatementDto accountStatementDTO = objectMapper.readValue(response, AccountStatementDto.class);
            assertEquals(ACCOUNT_STATEMENT_ID, accountStatementDTO.getId());
        }

        public void givenMalformedDeleteMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter=new SimpleMessageConverter();
            String json=objectMapper.writeValueAsString("test");
            deleteRequest=(Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void whenConsumeDeleteMessageVerifyExceptionIsThrown() {
            assertThrows(Exception.class, () -> accountStatementEventHandler.consumeDeleteMessage(deleteRequest));
        }


    }
}