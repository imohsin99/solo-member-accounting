package com.solofunds.memberaccounting.service.eventhandler.accountstatement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solofunds.memberaccounting.messaging.messenger.event.accountStatement.AccountStatementGetByWalletIdEvent;
import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.service.eventhandler.accountStatement.AccountStatementGetByWalletIdEventHandler;
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
public class AccountStatementGetByWalletIdEventHandlerTest {
    private Fixture fixture;

    @Mock
    AccountStatementServiceImpl accountStatementService;

    @Mock
    Message<AccountStatementGetByWalletIdEvent> accountStatementByWalletIdGetEventMessage;

    @InjectMocks
    AccountStatementGetByWalletIdEventHandler accountStatementGetByWalletIdEventHandler;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Test
    void testConsumeGetByWalletIdMessage() throws Exception {
        fixture.givenAccountStatementGetByWalletIdEventProvided();
        fixture.givenAccountStatementServiceReturnsAccountStatementByWalletId();

        fixture.whenConsumeGetByWalletIdMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturnedForGetByWalletIdEvent();
    }

    class Fixture {
        final UUID WALLET_ACCOUNT_ID = UUID.randomUUID();
        final UUID ACCOUNT_STATEMENT_ID = UUID.randomUUID();
        AccountStatementDto accountStatementDTO;

        Message<List<AccountStatementDto>> response;

        public void givenAccountStatementGetByWalletIdEventProvided() {
            when(accountStatementByWalletIdGetEventMessage.getPayload()).thenReturn(AccountStatementGetByWalletIdEvent
                    .builder()
                    .walletAccountId(WALLET_ACCOUNT_ID)
                    .build());
        }

        public void givenAccountStatementServiceReturnsAccountStatementByWalletId() {
            accountStatementDTO = new AccountStatementDto();
            accountStatementDTO.setId(ACCOUNT_STATEMENT_ID);
            accountStatementDTO.setWalletAccountId(WALLET_ACCOUNT_ID);
            when(accountStatementService.getAllAccountStatementsByWalletId(WALLET_ACCOUNT_ID)).thenReturn(List.of(accountStatementDTO));
        }

        public void whenConsumeGetByWalletIdMessageIsCalled() {
            response = accountStatementGetByWalletIdEventHandler.consumeEvent(accountStatementByWalletIdGetEventMessage);
        }

        public void thenVerifyExpectedJsonResponseIsReturnedForGetByWalletIdEvent() {
            assertEquals(WALLET_ACCOUNT_ID, response.getPayload().get(0).getWalletAccountId());
        }
    }
}
