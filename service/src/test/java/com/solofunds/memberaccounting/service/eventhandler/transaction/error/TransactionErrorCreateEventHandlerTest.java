package com.solofunds.memberaccounting.service.eventhandler.transaction.error;

import com.solofunds.memberaccounting.messaging.messenger.event.transaction.error.TransactionErrorCreateEvent;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionErrorCreateEventHandlerTest {

    @Mock
    TransactionErrorCodeService transactionErrorCodeService;

    @Mock
    TransactionErrorCodeDTO errorCodeDTO;

    @Mock
    Message<TransactionErrorCreateEvent> createEventMessage;

    @InjectMocks
    TransactionErrorCreateEventHandler eventHandler;

    Fixture fixture;

    @BeforeEach
    void setup() {
        fixture = new Fixture();
    }


    @Test
    void consumeCreateMessage() {
        fixture.givenTransactionErrorCreateEventIsMocked();
        fixture.givenTransactionErrorServiceGivesCreatedTransactionError();

        fixture.whenConsumeGetMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Nested
    class Fixture {

        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

        UUID TRANSACTION_ERROR_ID = UUID.randomUUID();

        TransactionErrorCodeDTO transactionErrorCodeDTO;

        Message<TransactionErrorCodeDTO> response;

        void givenTransactionErrorCreateEventIsMocked() {
            when(createEventMessage.getPayload()).thenReturn(
                    TransactionErrorCreateEvent.builder()
                            .transactionErrorCodeCapture(errorCodeDTO)
                            .build()
            );
        }

        void givenTransactionErrorServiceGivesCreatedTransactionError() {
            transactionErrorCodeDTO = new TransactionErrorCodeDTO();
            transactionErrorCodeDTO.setId(TRANSACTION_ERROR_ID);
            transactionErrorCodeDTO.setWalletAccountId(WALLET_ACCOUNT_ID);
            when(transactionErrorCodeService.addTransactionErrorCode(any(TransactionErrorCodeDTO.class))).thenReturn(transactionErrorCodeDTO);
        }

        void whenConsumeGetMessageIsCalled() {
            response = eventHandler.consumeCreateMessage(createEventMessage);
        }

        void thenVerifyExpectedJsonResponseIsReturned() {
            assertEquals(TRANSACTION_ERROR_ID, response.getPayload().getId());
        }
    }
}
