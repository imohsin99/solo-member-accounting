package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.MemberTransactionApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.memberTransaction.MemberTransactionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateMemberTransactionDto;
import com.solofunds.memberaccounting.model.MemberTransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class MemberTransactionDelegateImpl implements MemberTransactionApiDelegate {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<MemberTransactionDto> getTransactionByTransactionId(UUID id) {
        try {
            ResourceByIdGetEvent event = eventFactory.buildMemberTransactionGetEvent(id);
            MemberTransactionDto memberTransactionDTO = objectMapper.readValue(event.publishAndWait(), MemberTransactionDto.class);
            return ResponseEntity.ok(memberTransactionDTO);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public ResponseEntity<MemberTransactionDto> createMemberTransaction(CreateMemberTransactionDto createMemberTransactionDto) {
        try {
            MemberTransactionCreateEvent event = eventFactory.buildMemberTransactionCreateEvent(createMemberTransactionDto);
            MemberTransactionDto memberTransactionDTO = objectMapper.readValue(event.publishAndWait(), MemberTransactionDto.class);
            return ResponseEntity.ok(memberTransactionDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
