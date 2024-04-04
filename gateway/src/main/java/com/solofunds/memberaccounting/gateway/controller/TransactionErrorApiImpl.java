package com.solofunds.memberaccounting.gateway.controller;

import com.solofunds.memberaccounting.api.TransactionErrorsApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.transaction.error.TransactionErrorCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.transaction.error.TransactionErrorGetByWalletIdEvent;
import com.solofunds.memberaccounting.model.TransactionErrorCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class TransactionErrorApiImpl implements TransactionErrorsApiDelegate {

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<TransactionErrorCodeDTO> addTransactionError(TransactionErrorCodeDTO transactionErrorCodeDTO) {
        try {
            TransactionErrorCreateEvent event = eventFactory.buildTransactionErrorCreateEvent(transactionErrorCodeDTO);
            TransactionErrorCodeDTO errorCodeDTO = event.publishAndReceive(TransactionErrorCodeDTO.class);
            return ResponseEntity.ok(errorCodeDTO);
        }
        catch (Exception e) {
            log.error("Exception encountered during addTransactionError", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<TransactionErrorCodeDTO>> getTransactionErrorsByWalletAccount(UUID walletId) {
        try {
            TransactionErrorGetByWalletIdEvent event = eventFactory.buildTransactionErrorGetByWalletIdEvent(walletId);
            Class<List<TransactionErrorCodeDTO>> clazz = (Class) List.class;
            List<TransactionErrorCodeDTO> transactionErrorCodeDTO = event.publishAndReceive(clazz);
            return ResponseEntity.ok(transactionErrorCodeDTO);
        }
        catch (Exception e) {
            log.error("Exception encountered during getTransactionError", e);
            throw new RuntimeException(e);
        }
    }
}
