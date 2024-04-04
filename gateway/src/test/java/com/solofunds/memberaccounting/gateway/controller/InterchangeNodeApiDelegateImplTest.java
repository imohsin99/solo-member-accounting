package com.solofunds.memberaccounting.gateway.controller;

import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.interchangeNode.InterchangeNodeCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class InterchangeNodeApiDelegateImplTest {

    @Mock
    EventFactory mockEventFactory;

    @Mock
    MessagePublisher mockPublisher;

    @InjectMocks
    private InterchangeNodeApiImpl interchangeNodeApi;

    @Test
    public void testCreateInterchangeNode_Success() throws Exception {
        UUID walletAccountId = UUID.randomUUID();
        UUID bankCardId = UUID.randomUUID();
        UUID externalBankAccountId = UUID.randomUUID();
        CreateInterchangeNodeDto createInterchangeNodeDto = new CreateInterchangeNodeDto(bankCardId, externalBankAccountId, walletAccountId, "testCardHash", BankNodeType.INTERCHANGE, CardType.DEBIT, Network.AMEX);
        InterchangeNodeDto interchangeNodeDto = new InterchangeNodeDto();
        InterchangeNodeCreateEvent interchangeNodeCreateEvent = InterchangeNodeCreateEvent.builder().createInterchangeNodeDto(createInterchangeNodeDto).publisher(mockPublisher).build();

        when(mockEventFactory.buildInterchangeNodeCreateEvent(any(CreateInterchangeNodeDto.class))).thenReturn(interchangeNodeCreateEvent);
        when(mockPublisher.publishAndReceive(any(InterchangeNodeCreateEvent.class), any(String.class), any())).thenReturn(interchangeNodeDto);

        ResponseEntity<InterchangeNodeDto> response = interchangeNodeApi.createInterchangeNode(createInterchangeNodeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockPublisher, times(1)).publishAndReceive(any(InterchangeNodeCreateEvent.class), any(String.class), any());
        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateInterchangeNode_Exception() throws Exception {
        UUID walletAccountId = UUID.randomUUID();
        UUID bankCardId = UUID.randomUUID();
        UUID externalBankAccountId = UUID.randomUUID();
        CreateInterchangeNodeDto createInterchangeNodeDto = new CreateInterchangeNodeDto(bankCardId, externalBankAccountId, walletAccountId, "testCardHash", BankNodeType.INTERCHANGE, CardType.DEBIT, Network.AMEX);
        InterchangeNodeCreateEvent bankCardCreateEvent = InterchangeNodeCreateEvent.builder().createInterchangeNodeDto(createInterchangeNodeDto).publisher(mockPublisher).build();

        when(mockEventFactory.buildInterchangeNodeCreateEvent(any(CreateInterchangeNodeDto.class))).thenReturn(bankCardCreateEvent);
        when(mockPublisher.publishAndReceive(any(InterchangeNodeCreateEvent.class), any(String.class), any())).thenThrow(new IOException("Simulated Exception"));

        assertThrows(RuntimeException.class, () -> interchangeNodeApi.createInterchangeNode(createInterchangeNodeDto));
        verify(mockPublisher, times(1)).publishAndReceive(any(InterchangeNodeCreateEvent.class), any(String.class), any());
    }

    @Test
    public void testGetInterchangeNode_Success() throws Exception {
        UUID walletAccountId = UUID.randomUUID();
        UUID bankCardId = UUID.randomUUID();
        UUID externalBankAccountId = UUID.randomUUID();
        UUID interchangeNodeId = UUID.randomUUID();
        InterchangeNodeDto interchangeNodeDto = new InterchangeNodeDto(bankCardId, externalBankAccountId, walletAccountId, "hash", BankNodeType.INTERCHANGE, CardType.DEBIT, Network.AMEX);
        interchangeNodeDto.setId(interchangeNodeId);

        ResourceByIdGetEvent resourceByIdGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.INTERCHANGE_NODE_GET_EVENT)
                .id(interchangeNodeId)
                .build();

        when(mockEventFactory.buildInterchangeNodeGetEvent(any(UUID.class))).thenReturn(resourceByIdGetEvent);
        when(mockPublisher.publishAndReceive(any(ResourceByIdGetEvent.class), anyString(), any()))
                .thenReturn(interchangeNodeDto);

        ResponseEntity<InterchangeNodeDto> response = interchangeNodeApi.getInterchangeNode(interchangeNodeId);

        verify(mockPublisher, times(1)).publishAndReceive(any(ResourceByIdGetEvent.class), anyString(), any());

        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(interchangeNodeDto.getId(), response.getBody().getId(), "Ids should match");
        assertEquals(interchangeNodeDto, response.getBody(), "CollectionsDto objects should be equal");
    }

    @Test
    public void testGetCollectionsById_Exception() {
        UUID id = UUID.randomUUID();

        when(mockEventFactory.buildInterchangeNodeGetEvent(eq(id))).thenThrow(new IllegalArgumentException("Simulated exception"));

        assertThrows(RuntimeException.class, () -> interchangeNodeApi.getInterchangeNode(id));

        verify(mockEventFactory, times(1)).buildInterchangeNodeGetEvent(eq(id));
    }
}