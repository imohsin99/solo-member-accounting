package com.solofunds.memberaccounting.service.eventhandler.accountstatement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solofunds.memberaccounting.messaging.messenger.event.accountStatement.AccountStatementGetAllEvent;
import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.service.eventhandler.accountStatement.AccountStatementGetAllEventHandler;
import com.solofunds.memberaccounting.service.service.impl.AccountStatementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountStatementGetAllEventHandlerTest {
    private Fixture fixture;

    @Mock
    AccountStatementServiceImpl accountStatementService;
    @Mock
    Message<AccountStatementGetAllEvent> accountStatementGetAllEventMessage;

    @InjectMocks
    AccountStatementGetAllEventHandler accountStatementGetAllEventHandler;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Test
    void testConsumeGetAllMessage() throws JsonProcessingException {
        fixture.givenAccountStatementGetAllEventIsProvided();
        fixture.givenAccountStatementServiceReturnsGetAllAccountStatement();

        fixture.whenConsumeGetAllMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturnedForGetAllEvent();
    }

    class Fixture {
        final UUID ACCOUNT_STATEMENT_ID = UUID.randomUUID();

        AccountStatementDto accountStatementDTO;

        List<AccountStatementDto> response;

        public void givenAccountStatementGetAllEventIsProvided() {
            when(accountStatementGetAllEventMessage.getPayload()).thenReturn(AccountStatementGetAllEvent
                    .builder()
                    .build());
        }

        public void givenAccountStatementServiceReturnsGetAllAccountStatement() {
            accountStatementDTO = new AccountStatementDto();
            accountStatementDTO.setId(ACCOUNT_STATEMENT_ID);
            when(accountStatementService.getAllAccountStatements()).thenReturn(List.of(accountStatementDTO));
        }

        public void whenConsumeGetAllMessageIsCalled() {
            response = accountStatementGetAllEventHandler.consumeEvent(accountStatementGetAllEventMessage).getPayload();
        }

        public void thenVerifyExpectedJsonResponseIsReturnedForGetAllEvent() {
            assertEquals(ACCOUNT_STATEMENT_ID, response.get(0).getId());
        }

    }
}
