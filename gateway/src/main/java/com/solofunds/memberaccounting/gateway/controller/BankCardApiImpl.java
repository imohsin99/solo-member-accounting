package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.BankCardApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.bankCard.BankCardCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.BankCardDto;
import com.solofunds.memberaccounting.model.CreateBankCardDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class BankCardApiImpl implements BankCardApiDelegate {

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<BankCardDto> createBankCard(CreateBankCardDto createBankCardDto) {
        try {
            BankCardCreateEvent event = eventFactory.buildBankCardCreateEvent(createBankCardDto);
            BankCardDto bankCardDto = event.publishAndReceive(BankCardDto.class);
            return ResponseEntity.ok(bankCardDto);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<BankCardDto> getBankCard(UUID id) {
        try {
            ResourceByIdGetEvent event = eventFactory.buildBankCardGetEvent(id);
            BankCardDto interchangeNode = event.publishAndReceive(BankCardDto.class);
            return ResponseEntity.ok(interchangeNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
