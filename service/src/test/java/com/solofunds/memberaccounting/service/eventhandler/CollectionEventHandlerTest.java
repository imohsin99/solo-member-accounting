package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.collections.CollectionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CollectionsDto;
import com.solofunds.memberaccounting.model.CreateCollectionsDto;
import com.solofunds.memberaccounting.service.service.CollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class CollectionEventHandlerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Mock
    private CollectionService collectionService;

    @InjectMocks
    private CollectionEventHandler collectionEventHandler;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        collectionEventHandler = new CollectionEventHandler(objectMapper, collectionService);
        fixture = new Fixture();
    }

    @Test
    void consumeCreateMessage() throws Exception {
        fixture.givenCreateCollectionCreateEventIsProvided();
        fixture.givenCreateMessageStringIsProvided();
        fixture.givenCollectionServiceReturnsCollectionDto();

        fixture.whenConsumeCreateMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Test
    void consumeCreateMessage_Exception() throws Exception {
        fixture.givenCreateCollectionCreateEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();

        fixture.whenConsumeMessageVerifyExceptionIsThrown();
    }

    @Test
    void consumeGetMessage() throws Exception {
        fixture.givenCollectionGetEventIsProvided();
        fixture.givenGetMessageStringIsProvided();
        fixture.givenCollectionServiceReturnsGetCollectionDto();

        fixture.whenConsumeGetMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Test
    void consumeGetMessage_Exception() throws Exception {
        fixture.givenCollectionGetEventIsProvided();
        fixture.givenMalformedGetMessageStringIsProvided();

        fixture.whenConsumeGetMessageVerifyExceptionIsThrown();
    }

    @Nested
    public class Fixture {

        UUID COLLECTION_ID = UUID.randomUUID();

        CollectionCreateEvent collectionCreateEvent;

        ResourceByIdGetEvent resourceByIdGetEvent;

        Message<String> createRequest;

        Message<String> getRequest;

        CollectionsDto collectionsDTO;

        String response;

        void givenCreateCollectionCreateEventIsProvided(){
            CreateCollectionsDto createCollectionsDto = new CreateCollectionsDto();
            collectionCreateEvent = eventFactory.buildCollectionCreateEvent(createCollectionsDto);
        }

        void givenCollectionGetEventIsProvided(){
            resourceByIdGetEvent = ResourceByIdGetEvent
                    .builder()
                    .id(COLLECTION_ID)
                    .build();
        }

        void givenCreateMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String createJson = objectMapper.writeValueAsString(collectionCreateEvent);
            createRequest = (Message<String>) simpleMessageConverter.toMessage(createJson, null);
        }

        void givenGetMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString(resourceByIdGetEvent);
            getRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }

        void givenCollectionServiceReturnsCollectionDto() {
            collectionsDTO = new CollectionsDto();
            collectionsDTO.setId(COLLECTION_ID);
            when(collectionService.createCollections(any(CreateCollectionsDto.class))).thenReturn(collectionsDTO);
        }

        void givenCollectionServiceReturnsGetCollectionDto() {
            collectionsDTO = new CollectionsDto();
            collectionsDTO.setId(COLLECTION_ID);
            when(collectionService.getCollectionsById(eq(COLLECTION_ID))).thenReturn(collectionsDTO);
        }

        void whenConsumeCreateMessageIsCalled() {
            response = collectionEventHandler.consumeCreateMessage(createRequest);
        }

        void whenConsumeGetMessageIsCalled() {
            response = collectionEventHandler.consumeGetMessage(getRequest);
        }

        void thenVerifyExpectedJsonResponseIsReturned() throws JsonProcessingException {
            assertNotNull(response);
            CollectionsDto collectionsDTOResponse = objectMapper.readValue(response, CollectionsDto.class);
            assertEquals(COLLECTION_ID, collectionsDTOResponse.getId());
        }

        void givenMalformedMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String createJson = objectMapper.writeValueAsString("test");
            createRequest = (Message<String>) simpleMessageConverter.toMessage(createJson, null);
        }

        void givenMalformedGetMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString("test");
            getRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }

        void whenConsumeMessageVerifyExceptionIsThrown() {
            assertThrows(Exception.class, () -> collectionEventHandler.consumeCreateMessage(createRequest));
        }

        void whenConsumeGetMessageVerifyExceptionIsThrown() {
            assertThrows(Exception.class, () -> collectionEventHandler.consumeGetMessage(getRequest));
        }
    }
}