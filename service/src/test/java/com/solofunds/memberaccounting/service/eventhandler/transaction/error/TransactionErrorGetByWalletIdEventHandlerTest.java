package com.solofunds.memberaccounting.service.eventhandler.transaction.error;

import com.solofunds.memberaccounting.messaging.messenger.event.transaction.error.TransactionErrorGetByWalletIdEvent;
import com.solofunds.memberaccounting.model.TransactionErrorCodeDTO;
import com.solofunds.memberaccounting.service.service.TransactionErrorCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
class TransactionErrorGetByWalletIdEventHandlerTest {

    @Mock
    TransactionErrorCodeService transactionErrorCodeService;

    @Mock
    Message<TransactionErrorGetByWalletIdEvent> eventHandlerMessage;

    @InjectMocks
    TransactionErrorGetByWalletIdEventHandler eventHandler;

    Fixture fixture;

    @BeforeEach
    void setup() {
        fixture = new Fixture();
    }

    @Test
    void consumeGetMessage() {
        fixture.givenTransactionErrorGetEventIsMocked();
        fixture.givenTransactionErrorServiceReturnsTransactionErrorDto();

        fixture.whenConsumeGetMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Nested
    class Fixture {

        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

        UUID TRANSACTION_ERROR_ID = UUID.randomUUID();

        TransactionErrorCodeDTO transactionErrorCodeDTO;

        List<TransactionErrorCodeDTO> response;

        void givenTransactionErrorGetEventIsMocked() {
            when(eventHandlerMessage.getPayload()).thenReturn(
                    TransactionErrorGetByWalletIdEvent.builder()
                            .walletAccountId(WALLET_ACCOUNT_ID)
                            .build()
            );
        }

        void givenTransactionErrorServiceReturnsTransactionErrorDto() {
            transactionErrorCodeDTO = new TransactionErrorCodeDTO();
            transactionErrorCodeDTO.setId(TRANSACTION_ERROR_ID);
            transactionErrorCodeDTO.setWalletAccountId(WALLET_ACCOUNT_ID);
            when(transactionErrorCodeService.getTransactionErrorCodeByWalletId(WALLET_ACCOUNT_ID)).thenReturn(List.of(transactionErrorCodeDTO));
        }

        void whenConsumeGetMessageIsCalled() {
            response = eventHandler.consumeGetMessage(eventHandlerMessage).getPayload();
        }

        void thenVerifyExpectedJsonResponseIsReturned() {
            assertEquals(1, response.size());
            assertEquals(WALLET_ACCOUNT_ID, response.get(0).getWalletAccountId());
        }
    }
}
