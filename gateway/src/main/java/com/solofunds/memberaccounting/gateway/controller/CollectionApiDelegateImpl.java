package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.CollectionsApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.collections.CollectionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CollectionsDto;
import com.solofunds.memberaccounting.model.CreateCollectionsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class CollectionApiDelegateImpl implements CollectionsApiDelegate {

    private final ObjectMapper mapper= JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<CollectionsDto> createCollections(CreateCollectionsDto createCollectionsDto) {
        try {
            CollectionCreateEvent event = eventFactory.buildCollectionCreateEvent(createCollectionsDto);
            CollectionsDto collections = mapper.readValue(event.publishAndWait(), CollectionsDto.class);
            return ResponseEntity.ok(collections);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<CollectionsDto> getCollectionsById(UUID id) {
        try{
            ResourceByIdGetEvent event = eventFactory.buildCollectionGetById(id);
            CollectionsDto collectionsDTO = mapper.readValue(event.publishAndWait(),CollectionsDto.class);
            return ResponseEntity.ok(collectionsDTO);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
