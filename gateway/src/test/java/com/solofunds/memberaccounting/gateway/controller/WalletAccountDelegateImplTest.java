package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.walletAccount.WalletAccountCreateEvent;
import com.solofunds.memberaccounting.model.CreateWalletAccountDto;
import com.solofunds.memberaccounting.model.WalletAccountDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class WalletAccountDelegateImplTest {

    @Mock
    MessagePublisher mockPublisher;

    @Mock
    EventFactory mockEventFactory;

    @InjectMocks
    private WalletAccountDelegateImpl walletAccountDelegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateWalletAccount_Success() throws Exception {
        UUID soloMemberGUID = UUID.randomUUID();
        UUID bankId = UUID.randomUUID();
        CreateWalletAccountDto createWalletAccountDto = new CreateWalletAccountDto(soloMemberGUID, bankId);

        WalletAccountCreateEvent mockEvent = new WalletAccountCreateEvent();
        mockEvent.setPublisher(mockPublisher);
        when(mockEventFactory.buildWalletAccountCreateEvent(eq(createWalletAccountDto))).thenReturn(mockEvent);
        when(mockPublisher.publishAndWait(eq(mockEvent), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(createWalletAccountDto));

        ResponseEntity<WalletAccountDto> response = walletAccountDelegate.createWalletAccount(createWalletAccountDto);

        verify(mockPublisher, times(1)).publishAndWait(eq(mockEvent), any(String.class));

        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateWalletAccount_Exception() throws Exception {
        CreateWalletAccountDto createWalletAccountDto = new CreateWalletAccountDto();

        when(mockEventFactory.buildWalletAccountCreateEvent(eq(createWalletAccountDto)))
                .thenThrow(new RuntimeException(new IOException("Simulated exception")));

        assertThrows(RuntimeException.class, () -> walletAccountDelegate.createWalletAccount(createWalletAccountDto));
    }

    @Test
    public void testGetWalletAccount_Success() throws Exception {
        UUID walletAccountId = UUID.randomUUID();
        WalletAccountDto walletAccountDto = new WalletAccountDto();
        walletAccountDto.setWalletAccountId(walletAccountId);

        ResourceByIdGetEvent walletAccountGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.WALLET_ACCOUNT_GET_TOPIC)
                .id(walletAccountId)
                .build();

        when(mockEventFactory.buildWalletAccountGetEvent(any(UUID.class))).thenReturn(walletAccountGetEvent);
        when(mockPublisher.publishAndWait(eq(walletAccountGetEvent), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(walletAccountDto));

        ResponseEntity<WalletAccountDto> response = walletAccountDelegate.getWalletAccount(walletAccountId);

        verify(mockPublisher, times(1)).publishAndWait(eq(walletAccountGetEvent), any(String.class));

        assertEquals(walletAccountDto, response.getBody());
    }

    @Test
    public void testGetWalletAccount_Exception() throws Exception {
        UUID walletAccountId = UUID.randomUUID();

        ResourceByIdGetEvent walletAccountGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.WALLET_ACCOUNT_GET_TOPIC)
                .id(walletAccountId)
                .build();

        when(mockEventFactory.buildWalletAccountGetEvent(any(UUID.class))).thenReturn(walletAccountGetEvent);
        when(mockPublisher.publishAndWait(eq(walletAccountGetEvent), any(String.class)))
                .thenThrow(new IOException("test"));

        assertThrows(RuntimeException.class, () -> walletAccountDelegate.getWalletAccount(walletAccountId));

        verify(mockPublisher, times(1)).publishAndWait(eq(walletAccountGetEvent), any(String.class));
    }
}