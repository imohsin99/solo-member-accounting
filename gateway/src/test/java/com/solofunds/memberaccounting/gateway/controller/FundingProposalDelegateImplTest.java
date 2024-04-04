package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal.FundingProposalCreatedEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal.FundingProposalUpdateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateFundingProposalDto;
import com.solofunds.memberaccounting.model.FundingProposalDto;
import com.solofunds.memberaccounting.model.UpdateFundingProposalDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
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
public class FundingProposalDelegateImplTest {

    @Mock
    EventFactory mockEventFactory;

    @Mock
    MessagePublisher mockPublisher;

    @InjectMocks
    private FundingProposalDelegateImpl fundingProposalDelegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateFundingProposal_Success() throws Exception {
        UUID lenderWalletAccountId = UUID.randomUUID();
        BigDecimal proposedTipAmount = BigDecimal.valueOf(100.0);
        BigDecimal amount = BigDecimal.valueOf(1000.0);
        String currencyCode = "USD";
        Long currencyExponent = 2L;

        CreateFundingProposalDto createFundingProposalDto = new CreateFundingProposalDto(lenderWalletAccountId
                , proposedTipAmount, amount, currencyCode, currencyExponent
        );

        FundingProposalCreatedEvent mockEvent = new FundingProposalCreatedEvent();
        mockEvent.setPublisher(mockPublisher);
        mockEvent.setCreateFundingProposalDto(createFundingProposalDto);

        when(mockEventFactory.buildFundingProposalCreatedEvent(any(CreateFundingProposalDto.class))).thenReturn(mockEvent);
        when(mockPublisher
                .publishAndWait(any(FundingProposalCreatedEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(createFundingProposalDto));

        ResponseEntity<FundingProposalDto> response = fundingProposalDelegate.createFundingProposal(createFundingProposalDto);

        verify(mockPublisher, times(1)).publishAndWait(any(FundingProposalCreatedEvent.class), any(String.class));

        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateFundingProposal_Exception() throws Exception {
        UUID lenderWalletAccountId = UUID.randomUUID();
        BigDecimal proposedTipAmount = BigDecimal.valueOf(100.0);
        BigDecimal amount = BigDecimal.valueOf(1000.0);
        String currencyCode = "USD";
        Long currencyExponent = 2L;

        CreateFundingProposalDto createFundingProposalDto = new CreateFundingProposalDto(lenderWalletAccountId
                , proposedTipAmount, amount, currencyCode, currencyExponent
        );

        FundingProposalCreatedEvent mockEvent = new FundingProposalCreatedEvent();
        mockEvent.setPublisher(mockPublisher);
        mockEvent.setCreateFundingProposalDto(createFundingProposalDto);

        when(mockEventFactory.buildFundingProposalCreatedEvent(any(CreateFundingProposalDto.class))).thenReturn(mockEvent);
        when(mockPublisher.publishAndWait(any(FundingProposalCreatedEvent.class), any(String.class)))
                .thenThrow(new IOException("test"));

        assertThrows(RuntimeException.class, () -> fundingProposalDelegate.createFundingProposal(createFundingProposalDto));

        verify(mockPublisher, times(1)).publishAndWait(any(FundingProposalCreatedEvent.class), any(String.class));
    }

    @Test
    public void testGetFundingProposal_Success() throws Exception {
        UUID fundingProposalId = UUID.randomUUID();
        FundingProposalDto fundingProposalDto = new FundingProposalDto();
        fundingProposalDto.setFundingProposalId(fundingProposalId);

        ResourceByIdGetEvent fundingProposalGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.FUNDING_PROPOSAL_GET_TOPIC)
                .id(fundingProposalId)
                .build();

        when(mockEventFactory.buildFundingProposalGetEvent(any(UUID.class))).thenReturn(fundingProposalGetEvent);
        when(mockPublisher.publishAndWait(eq(fundingProposalGetEvent), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(fundingProposalDto));

        ResponseEntity<FundingProposalDto> response = fundingProposalDelegate.getFundingProposal(fundingProposalId);

        verify(mockPublisher, times(1)).publishAndWait(eq(fundingProposalGetEvent), any(String.class));

        assertEquals(fundingProposalDto, response.getBody());
    }

    @Test
    public void testGetFundingProposal_Exception() throws Exception {
        UUID fundingProposalId = UUID.randomUUID();

        ResourceByIdGetEvent fundingProposalGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.FUNDING_PROPOSAL_GET_TOPIC)
                .id(fundingProposalId)
                .build();

        when(mockEventFactory.buildFundingProposalGetEvent(any(UUID.class))).thenReturn(fundingProposalGetEvent);
        when(mockPublisher.publishAndWait(eq(fundingProposalGetEvent), any(String.class)))
                .thenThrow(new IOException("test"));

        assertThrows(RuntimeException.class, () -> fundingProposalDelegate.getFundingProposal(fundingProposalId));

        verify(mockPublisher, times(1)).publishAndWait(eq(fundingProposalGetEvent), any(String.class));
    }

    @Test
    public void testUpdateFundingProposal_Success() throws Exception {
        UUID fundingProposalId = UUID.randomUUID();
        UpdateFundingProposalDto updateFundingProposalDto = new UpdateFundingProposalDto();

        FundingProposalUpdateEvent mockEvent = new FundingProposalUpdateEvent();
        mockEvent.setPublisher(mockPublisher);
        mockEvent.setId(fundingProposalId);
        mockEvent.setFundingProposalDto(updateFundingProposalDto);

        when(mockEventFactory.buildFundingProposalUpdateEvent(any(UUID.class), any(UpdateFundingProposalDto.class)))
                .thenReturn(mockEvent);
        when(mockPublisher.publishAndWait(any(FundingProposalUpdateEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(updateFundingProposalDto));

        ResponseEntity<FundingProposalDto> response = fundingProposalDelegate.updateFundingProposal(fundingProposalId, updateFundingProposalDto);

        verify(mockPublisher, times(1)).publishAndWait(any(FundingProposalUpdateEvent.class), any(String.class));

        assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateFundingProposal_Exception() {
        UUID fundingProposalId = UUID.randomUUID();
        UpdateFundingProposalDto updateFundingProposalDto = new UpdateFundingProposalDto();

        when(mockEventFactory.buildFundingProposalUpdateEvent(any(UUID.class), any(UpdateFundingProposalDto.class)))
                .thenThrow(new RuntimeException(new IOException("test")));

        assertThrows(RuntimeException.class, () -> fundingProposalDelegate.updateFundingProposal(fundingProposalId, updateFundingProposalDto));
    }
}