package com.solofunds.memberaccounting.gateway.controller;

import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.bankCard.BankCardCreateEvent;
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
public class BankCardApiDelegateImplTest {

    @Mock
    EventFactory mockEventFactory;

    @Mock
    MessagePublisher mockPublisher;

    @InjectMocks
    private BankCardApiImpl bankCardApi;

    @Test
    public void testCreateBankCard_Success() throws Exception {
        UUID walletAccountId = UUID.randomUUID();
        CreateBankCardDto createBankCardDto = new CreateBankCardDto(walletAccountId, CardType.DEBIT, false, "test bank institution name");
        BankCardDto bankCardDto = new BankCardDto();
        BankCardCreateEvent bankCardCreateEvent = BankCardCreateEvent.builder().createBankCardDto(createBankCardDto).publisher(mockPublisher).build();

        when(mockEventFactory.buildBankCardCreateEvent(any(CreateBankCardDto.class))).thenReturn(bankCardCreateEvent);
        when(mockPublisher.publishAndReceive(any(BankCardCreateEvent.class), any(String.class), any())).thenReturn(bankCardDto);

        ResponseEntity<BankCardDto> response = bankCardApi.createBankCard(createBankCardDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockPublisher, times(1)).publishAndReceive(any(BankCardCreateEvent.class), any(String.class), any());
        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateBankCard_Exception() throws Exception {
        UUID walletAccountId = UUID.randomUUID();
        CreateBankCardDto createBankCardDto = new CreateBankCardDto(walletAccountId, CardType.DEBIT, false, "test bank institution name");
        BankCardCreateEvent bankCardCreateEvent = BankCardCreateEvent.builder().createBankCardDto(createBankCardDto).publisher(mockPublisher).build();

        when(mockEventFactory.buildBankCardCreateEvent(any(CreateBankCardDto.class))).thenReturn(bankCardCreateEvent);
        when(mockPublisher.publishAndReceive(any(BankCardCreateEvent.class), any(String.class), any())).thenThrow(new IOException("Simulated Exception"));

        assertThrows(RuntimeException.class, () -> bankCardApi.createBankCard(createBankCardDto));
        verify(mockPublisher, times(1)).publishAndReceive(any(BankCardCreateEvent.class), any(String.class), any());
    }

    @Test
    public void testGetBankCard_Success() throws Exception {
        UUID walletAccountId = UUID.randomUUID();
        UUID bankCardId = UUID.randomUUID();
        BankCardDto bankCardDto = new BankCardDto(walletAccountId, CardType.DEBIT, false, "test");
        bankCardDto.setId(bankCardId);

        ResourceByIdGetEvent resourceByIdGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.BANK_CARD_GET_EVENT)
                .id(bankCardId)
                .build();

        when(mockEventFactory.buildBankCardGetEvent(any(UUID.class))).thenReturn(resourceByIdGetEvent);
        when(mockPublisher.publishAndReceive(any(ResourceByIdGetEvent.class), anyString(), any()))
                .thenReturn(bankCardDto);

        ResponseEntity<BankCardDto> response = bankCardApi.getBankCard(bankCardId);

        verify(mockPublisher, times(1)).publishAndReceive(any(ResourceByIdGetEvent.class), anyString(), any());

        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(bankCardDto.getId(), response.getBody().getId(), "Ids should match");
        assertEquals(bankCardDto, response.getBody(), "CollectionsDto objects should be equal");
    }

    @Test
    public void testGetCollectionsById_Exception() {
        UUID id = UUID.randomUUID();

        when(mockEventFactory.buildBankCardGetEvent(eq(id))).thenThrow(new IllegalArgumentException("Simulated exception"));

        assertThrows(RuntimeException.class, () -> bankCardApi.getBankCard(id));

        verify(mockEventFactory, times(1)).buildBankCardGetEvent(eq(id));
    }
}