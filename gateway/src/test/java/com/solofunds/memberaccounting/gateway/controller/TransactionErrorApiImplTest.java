package com.solofunds.memberaccounting.gateway.controller;

import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.transaction.error.TransactionErrorCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.transaction.error.TransactionErrorGetByWalletIdEvent;
import com.solofunds.memberaccounting.model.TransactionErrorCodeDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionErrorApiImplTest {

    @Mock
    EventFactory mockEventFactory;

    @Mock
    TransactionErrorCodeDTO createTransactionErrorCode;

    @Mock
    TransactionErrorCreateEvent transactionErrorCreateEvent;

    @Mock
    TransactionErrorGetByWalletIdEvent transactionErrorGetByWalletIdEvent;

    @InjectMocks
    TransactionErrorApiImpl transactionErrorApi;

    @Test
    void addTransactionError() {
        UUID TRANSACTION_ERROR_ID = UUID.randomUUID();
        TransactionErrorCodeDTO expectedTransactionErrorCode = new TransactionErrorCodeDTO();
        expectedTransactionErrorCode.setId(TRANSACTION_ERROR_ID);

        when(mockEventFactory.buildTransactionErrorCreateEvent(any(TransactionErrorCodeDTO.class))).thenReturn(transactionErrorCreateEvent);
        when(transactionErrorCreateEvent.publishAndReceive(any())).thenReturn(expectedTransactionErrorCode);

        ResponseEntity<TransactionErrorCodeDTO> response = transactionErrorApi.addTransactionError(createTransactionErrorCode);

        assertNotNull(response.getBody());
        assertEquals(TRANSACTION_ERROR_ID, response.getBody().getId());
    }

    @Test
    void addTransactionError_RuntimeException() {
        assertThrows(RuntimeException.class, () -> transactionErrorApi.addTransactionError(null));
    }

    @Test
    void getTransactionErrorsByWalletAccount() {
        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();
        TransactionErrorCodeDTO transactionErrorCodeDTO = new TransactionErrorCodeDTO();
        transactionErrorCodeDTO.setWalletAccountId(WALLET_ACCOUNT_ID);

        List<TransactionErrorCodeDTO> expectedList = List.of(transactionErrorCodeDTO);

        when(mockEventFactory.buildTransactionErrorGetByWalletIdEvent(any(UUID.class))).thenReturn(transactionErrorGetByWalletIdEvent);
        when(transactionErrorGetByWalletIdEvent.publishAndReceive(any())).thenReturn(expectedList);

        ResponseEntity<List<TransactionErrorCodeDTO>> response = transactionErrorApi.getTransactionErrorsByWalletAccount(WALLET_ACCOUNT_ID);

        assertEquals(1, response.getBody().size());
        assertEquals(WALLET_ACCOUNT_ID, response.getBody().get(0).getWalletAccountId());
    }

    @Test
    void getTransactionErrorsByWalletAccount_RuntimeException() {
        assertThrows(RuntimeException.class, () -> transactionErrorApi.getTransactionErrorsByWalletAccount(null));
    }
}
