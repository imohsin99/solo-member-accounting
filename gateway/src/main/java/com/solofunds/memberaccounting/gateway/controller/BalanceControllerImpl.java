package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.BalanceApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.BalanceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class BalanceControllerImpl implements BalanceApiDelegate {

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<BalanceDto> getBalance(UUID walletAccountId) {
        try {
            ResourceByIdGetEvent balanceByWalletIdGetEvent = eventFactory.buildBalanceByWalletAccountIdGetEvent(walletAccountId);
            BalanceDto balanceRes = mapper.readValue(balanceByWalletIdGetEvent.publishAndWait(), BalanceDto.class);
            return ResponseEntity.ok(balanceRes);
        } catch (Exception e) {
            log.error("Exception encountered during getBalance", e);
            throw new RuntimeException(e);
        }
    }

}
