package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.loanRequest.GetFilteredAndSortedLoanRequestEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loanRequest.LoanRequestCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.loanRequestEvents.GetAllFundingProposalByLoanRequestIdEvent;
import com.solofunds.memberaccounting.model.CreateLoanRequestDto;
import com.solofunds.memberaccounting.model.FundingProposalDto;
import com.solofunds.memberaccounting.model.LoanRequestDto;
import com.solofunds.memberaccounting.model.LoanRequestStatus;
import com.solofunds.memberaccounting.model.SortBy;
import com.solofunds.memberaccounting.model.SortDirection;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
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
public class LoanRequestDelegateImplTest {

    @Mock
    EventFactory mockEventFactory;

    @Mock
    MessagePublisher mockPublisher;

    @InjectMocks
    private LoanRequestDelegateImpl loanRequestDelegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateLoanRequest_Success() throws Exception {
        UUID borrowerWalletAccountId = UUID.randomUUID();
        BigDecimal principal = BigDecimal.valueOf(1000.0);
        String currencyCode = "USD";
        Long currencyExponent = 2L;
        BigDecimal tipAmount = BigDecimal.valueOf(100.0);
        BigDecimal donationAmount = BigDecimal.valueOf(50.0);
        String description = "Test loan request";
        Long duration = 30L;

        CreateLoanRequestDto createLoanRequestDto = new CreateLoanRequestDto(
                borrowerWalletAccountId, principal, currencyCode, currencyExponent,
                tipAmount, donationAmount, description, duration
        );

        LoanRequestCreateEvent mockEvent = new LoanRequestCreateEvent();
        mockEvent.setPublisher(mockPublisher);
        mockEvent.setCreateLoanRequest(createLoanRequestDto);

        when(mockEventFactory.buildLoanRequestCreateEvent(eq(createLoanRequestDto))).thenReturn(mockEvent);
        when(mockPublisher.publishAndWait(eq(mockEvent), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(createLoanRequestDto));

        ResponseEntity<LoanRequestDto> response = loanRequestDelegate.createLoanRequest(createLoanRequestDto);

        verify(mockPublisher, times(1)).publishAndWait(eq(mockEvent), any(String.class));

        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateLoanRequest_Exception() throws Exception {
        CreateLoanRequestDto createLoanRequestDto = new CreateLoanRequestDto();

        when(mockEventFactory.buildLoanRequestCreateEvent(any(CreateLoanRequestDto.class)))
                .thenThrow(new RuntimeException("test"));

        assertThrows(RuntimeException.class, () -> loanRequestDelegate.createLoanRequest(createLoanRequestDto));
    }

    @Test
    public void testGetLoanRequest_Success() throws Exception {
        UUID loanRequestId = UUID.randomUUID();
        LoanRequestDto loanRequestDto = new LoanRequestDto();

        ResourceByIdGetEvent loanRequestGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.LOAN_REQUEST_GET_TOPIC)
                .id(loanRequestId)
                .build();

        when(mockEventFactory.buildLoanRequestGetEvent(any(UUID.class))).thenReturn(loanRequestGetEvent);
        when(mockPublisher.publishAndReceive(any(ResourceByIdGetEvent.class), any(String.class), any()))
                .thenReturn(loanRequestDto);

        ResponseEntity<LoanRequestDto> response = loanRequestDelegate.getLoanRequest(loanRequestId);

        verify(mockEventFactory, times(1)).buildLoanRequestGetEvent(eq(loanRequestId));
        verify(mockPublisher, times(1)).publishAndReceive(eq(loanRequestGetEvent),
                                                                                eq(ResourceByIdGetEvent.LOAN_REQUEST_GET_TOPIC),
                                                                                eq(LoanRequestDto.class));

        assertEquals(loanRequestDto, response.getBody());
    }

    @Test
    public void testGetLoanRequest_Exception() throws Exception {
        UUID loanRequestId = UUID.randomUUID();

        ResourceByIdGetEvent loanRequestGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.LOAN_REQUEST_GET_TOPIC)
                .id(loanRequestId)
                .build();

        when(mockEventFactory.buildLoanRequestGetEvent(any(UUID.class))).thenReturn(loanRequestGetEvent);
        when(mockPublisher.publishAndReceive(any(ResourceByIdGetEvent.class), any(String.class), any()))
                .thenThrow(new IOException("test"));

        assertThrows(IOException.class, () -> loanRequestDelegate.getLoanRequest(loanRequestId));
    }

    @Test
    public void testGetFilteredAndSortedLoanRequests_Success() throws Exception {
        UUID loanRequestId = UUID.randomUUID();
        LoanRequestStatus status = LoanRequestStatus.ACTIVE;
        SortDirection sort = SortDirection.ASC;
        SortBy sortBy = SortBy.AMOUNT;
        Boolean slpEligible = true;

        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setLoanRequestId(loanRequestId);
        loanRequestDto.setStatus(status);
        List<LoanRequestDto> loanRequestDtoList = List.of(loanRequestDto, loanRequestDto);

        GetFilteredAndSortedLoanRequestEvent getFilteredAndSortedLoanRequestEvent = GetFilteredAndSortedLoanRequestEvent
                .builder()
                .publisher(mockPublisher)
                .sort(sort)
                .sortBy(sortBy)
                .slpEligible(slpEligible)
                .build();

        when(mockEventFactory.buildGetFilteredAndSortedLoanRequestEvent(eq(status), eq(sort), eq(sortBy), eq(slpEligible)))
                .thenReturn(getFilteredAndSortedLoanRequestEvent);
        when(mockPublisher.publishAndWait(any(GetFilteredAndSortedLoanRequestEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(loanRequestDtoList));

        ResponseEntity<List<LoanRequestDto>> response = loanRequestDelegate.getFilteredAndSortedLoanRequests(
                status, sort, sortBy, slpEligible);

        verify(mockPublisher, times(1)).publishAndWait(any(GetFilteredAndSortedLoanRequestEvent.class), any(String.class));

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(LoanRequestStatus.ACTIVE,response.getBody().get(0).getStatus());
    }

    @Test
    public void testGetFilteredAndSortedLoanRequests() throws Exception {
        LoanRequestStatus status = LoanRequestStatus.ACTIVE;
        SortDirection sort = SortDirection.ASC;
        SortBy sortBy = SortBy.AMOUNT;
        Boolean slpEligible = true;

        when(mockEventFactory.buildGetFilteredAndSortedLoanRequestEvent(eq(status), eq(sort), eq(sortBy), eq(slpEligible)))
                .thenThrow(new RuntimeException("test"));

        assertThrows(RuntimeException.class, () -> loanRequestDelegate.getFilteredAndSortedLoanRequests(
                status, sort, sortBy, slpEligible));
    }

    @Test
    public void testGetAllFundingProposalByLoanRequestId_Success() throws Exception {
        UUID loanRequestId = UUID.randomUUID();

        FundingProposalDto fundingProposalDto = new FundingProposalDto();
        fundingProposalDto.setLoanRequestId(loanRequestId);

        List<FundingProposalDto> fundingProposalDtoList = List.of(fundingProposalDto, fundingProposalDto);

        GetAllFundingProposalByLoanRequestIdEvent event = GetAllFundingProposalByLoanRequestIdEvent.builder()
                .publisher(mockPublisher)
                .id(loanRequestId)
                .build();

        when(mockEventFactory.buildGetAllFundingProposalByLoanRequestIdEvent(eq(loanRequestId)))
                .thenReturn(event);
        when(mockPublisher.publishAndWait(eq(event), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(fundingProposalDtoList));

        ResponseEntity<List<FundingProposalDto>> response = loanRequestDelegate.getAllFundingProposalByLoanRequestId(loanRequestId);

        verify(mockPublisher, times(1)).publishAndWait(eq(event), any(String.class));

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetAllFundingProposalByLoanRequestId_Exception() throws Exception {
        UUID loanRequestId = UUID.randomUUID();

        when(mockPublisher.publishAndWait(any(GetAllFundingProposalByLoanRequestIdEvent.class), any(String.class)))
                .thenThrow(new IOException("test"));

        assertThrows(RuntimeException.class, () -> loanRequestDelegate.getAllFundingProposalByLoanRequestId(loanRequestId));
    }
}