package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.InterchangeNodeApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.interchangeNode.InterchangeNodeCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateInterchangeNodeDto;
import com.solofunds.memberaccounting.model.InterchangeNodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class InterchangeNodeApiImpl implements InterchangeNodeApiDelegate {

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<InterchangeNodeDto> createInterchangeNode(CreateInterchangeNodeDto createInterchangeNodeDto) {
        try {
            InterchangeNodeCreateEvent event = eventFactory.buildInterchangeNodeCreateEvent(createInterchangeNodeDto);
            InterchangeNodeDto interchangeNode = event.publishAndReceive(InterchangeNodeDto.class);
            return ResponseEntity.ok(interchangeNode);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<InterchangeNodeDto> getInterchangeNode(UUID id) {
        try {
            ResourceByIdGetEvent event = eventFactory.buildInterchangeNodeGetEvent(id);
            InterchangeNodeDto interchangeNode = event.publishAndReceive(InterchangeNodeDto.class);
            return ResponseEntity.ok(interchangeNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
