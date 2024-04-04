package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.CallCalculateStartingBalanceApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.balance.CalculateStartingBalanceEvent;
import com.solofunds.memberaccounting.model.CalculateStartingBalanceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CallCalculateStartingBalanceImpl implements CallCalculateStartingBalanceApiDelegate {

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<String> callCalculateStartingBalanceFunction(CalculateStartingBalanceDto calculateStartingBalanceDto) {
        try {
            CalculateStartingBalanceEvent event = eventFactory.buildCalculateStartingBalanceEvent(calculateStartingBalanceDto);
            String res = mapper.readValue(event.publishAndWait(), String.class);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
