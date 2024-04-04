package com.solofunds.memberaccounting.service.eventhandler.accountstatement;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.accountStatement.AccountStatementCreateEvent;
import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.model.CreateAccountStatementDto;
import com.solofunds.memberaccounting.service.eventhandler.accountStatement.AccountStatementCreateEventHandler;
import com.solofunds.memberaccounting.service.service.impl.AccountStatementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountStatementCreateEventHandlerTest {
    private Fixture fixture;

    @Mock
    CreateAccountStatementDto statementDTO;

    @Mock
    AccountStatementServiceImpl accountStatementService;

    @Mock
    Message<AccountStatementCreateEvent> accountStatementCreateEventMessage;

    @InjectMocks
    AccountStatementCreateEventHandler accountStatementCreateEventHandler;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Test
    void testConsumeCreateMessage() {
        fixture.givenCreateAccountStatementEventIsMocked();
        fixture.givenAccountStatementCreateEventIsProvided();
        fixture.givenAccountStatementServiceReturnsCreateAccountStatement();

        fixture.whenConsumeCreateMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturnedForCreateEvent();
    }

    class Fixture {
        final LocalDate START_TIME = LocalDate.now();
        final LocalDate END_TIME = LocalDate.now();
        final UUID WALLET_ACCOUNT_ID = UUID.randomUUID();
        final UUID ACCOUNT_STATEMENT_ID = UUID.randomUUID();
        AccountStatementDto accountStatementDTO;
        Message<AccountStatementDto> response;

        public void givenCreateAccountStatementEventIsMocked() {
            when(statementDTO.getEndTime()).thenReturn(END_TIME);
            when(statementDTO.getStartTime()).thenReturn(START_TIME);
        }

        public void givenAccountStatementCreateEventIsProvided(){
            when(accountStatementCreateEventMessage.getPayload()).thenReturn(AccountStatementCreateEvent
                    .builder()
                    .walletAccountId(WALLET_ACCOUNT_ID)
                    .createAccountStatementDto(statementDTO)
                    .build());
        }

        public void givenAccountStatementServiceReturnsCreateAccountStatement() {
            accountStatementDTO = new AccountStatementDto();
            accountStatementDTO.setId(ACCOUNT_STATEMENT_ID);
            when(accountStatementService.createAccountStatement(any(UUID.class), any(LocalDate.class),any(LocalDate.class))).thenReturn(accountStatementDTO);
        }

        public void whenConsumeCreateMessageIsCalled() {
            response = accountStatementCreateEventHandler.consumeEvent(accountStatementCreateEventMessage);
        }

        public void thenVerifyExpectedJsonResponseIsReturnedForCreateEvent() {
            assertEquals(ACCOUNT_STATEMENT_ID, response.getPayload().getId());
        }
    }
}
