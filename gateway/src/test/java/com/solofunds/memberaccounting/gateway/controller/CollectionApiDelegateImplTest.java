package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.collections.CollectionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CollectionsDto;
import com.solofunds.memberaccounting.model.CreateCollectionsDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class CollectionApiDelegateImplTest {

    @Mock
    EventFactory mockEventFactory;

    @Mock
    MessagePublisher mockPublisher;

    @InjectMocks
    private CollectionApiDelegateImpl collectionApiDelegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateCollections_Success() throws Exception {
        UUID loanId = UUID.randomUUID();
        CreateCollectionsDto createCollectionsDto = new CreateCollectionsDto(loanId);

        CollectionCreateEvent collectionCreateEvent = new CollectionCreateEvent();
        collectionCreateEvent.setPublisher(mockPublisher);
        collectionCreateEvent.setCreateCollectionsDTO(createCollectionsDto);

        when(mockEventFactory.buildCollectionCreateEvent(any(CreateCollectionsDto.class))).thenReturn(collectionCreateEvent);
        when(mockPublisher
                .publishAndWait(any(CollectionCreateEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(createCollectionsDto));

        ResponseEntity<CollectionsDto> response = collectionApiDelegate.createCollections(createCollectionsDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(mockPublisher, times(1)).publishAndWait(any(CollectionCreateEvent.class), any(String.class));

        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateCollections_Exception() throws Exception {
        UUID loanId = UUID.randomUUID();
        CreateCollectionsDto createCollectionsDto = new CreateCollectionsDto(loanId);

        CollectionCreateEvent collectionCreateEvent = new CollectionCreateEvent();
        collectionCreateEvent.setPublisher(mockPublisher);
        collectionCreateEvent.setCreateCollectionsDTO(createCollectionsDto);

        when(mockEventFactory.buildCollectionCreateEvent(any(CreateCollectionsDto.class))).thenReturn(collectionCreateEvent);
        when(mockPublisher.publishAndWait(any(CollectionCreateEvent.class), any(String.class))).thenThrow(new IOException("Simulated exception"));

        assertThrows(RuntimeException.class, () -> collectionApiDelegate.createCollections(createCollectionsDto));

        verify(mockPublisher, times(1)).publishAndWait(any(CollectionCreateEvent.class), any(String.class));
    }


    @Test
    public void testGetCollectionsById_Success() throws Exception {
        UUID loanId = UUID.randomUUID();
        CollectionsDto collectionsDto = new CollectionsDto(loanId);

        ResourceByIdGetEvent resourceByIdGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.COLLECTION_GET_TOPIC)
                .id(loanId)
                .build();

        when(mockEventFactory.buildCollectionGetById(any(UUID.class))).thenReturn(resourceByIdGetEvent);

        when(mockPublisher.publishAndWait(any(ResourceByIdGetEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(collectionsDto));

        ResponseEntity<CollectionsDto> response = collectionApiDelegate.getCollectionsById(loanId);

        verify(mockPublisher, times(1)).publishAndWait(any(ResourceByIdGetEvent.class), any(String.class));

        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(collectionsDto.getId(), response.getBody().getId(), "Ids should match");
        assertEquals(collectionsDto, response.getBody(), "CollectionsDto objects should be equal");
    }

    @Test
    public void testGetCollectionsById_Exception() throws Exception {
        UUID id = UUID.randomUUID();

        when(mockEventFactory.buildCollectionGetById(eq(id))).thenThrow(new IllegalArgumentException("Simulated exception"));

        assertThrows(RuntimeException.class, () -> collectionApiDelegate.getCollectionsById(id));

        verify(mockEventFactory, times(1)).buildCollectionGetById(eq(id));
    }
}