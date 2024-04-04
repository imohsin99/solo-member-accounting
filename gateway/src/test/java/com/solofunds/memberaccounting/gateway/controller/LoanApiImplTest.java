package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateLoanDto;
import com.solofunds.memberaccounting.model.LoanDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoanApiImplTest {

    @Mock
    MessagePublisher mockPublisher;

    @Mock
    EventFactory mockEventFactory;

    @InjectMocks
    private LoanApiImpl loanApi;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createLoan() throws Exception {
        UUID loanRequestId = UUID.randomUUID();
        UUID fundingProposalId = UUID.randomUUID();
        UUID borrowerWalletAccountId = UUID.randomUUID();
        UUID lenderWalletAccountId = UUID.randomUUID();
        CreateLoanDto createRequest = new CreateLoanDto(loanRequestId, fundingProposalId, borrowerWalletAccountId, lenderWalletAccountId);

        LoanCreateEvent loanCreateEvent = new LoanCreateEvent();
        loanCreateEvent.setCreateLoanDto(createRequest);
        loanCreateEvent.setPublisher(mockPublisher);

        LoanDto savedLoan = new LoanDto();
        savedLoan.setLoanRequestId(loanRequestId);
        savedLoan.setAcceptedLoanProposalId(fundingProposalId);

        when(mockEventFactory.buildLoanCreateEvent(any(CreateLoanDto.class))).thenReturn(loanCreateEvent);

        when(mockPublisher
                .publishAndWait(any(LoanCreateEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(savedLoan));

        ResponseEntity<LoanDto> response = loanApi.createLoan(createRequest);

        verify(mockPublisher, times(1)).publishAndWait(any(LoanCreateEvent.class), any(String.class));

        assertNotNull(response.getBody());
        assertEquals(savedLoan.getLoanRequestId(), response.getBody().getLoanRequestId());
    }

    @Test
    void createLoan_invalidLoanCreateRequest(){
        CreateLoanDto loanCreateRequest = new CreateLoanDto(null, null, null, null);
        assertThrows(RuntimeException.class,
                () -> loanApi.createLoan(loanCreateRequest)
        );
    }

    @Test
    void getLoanById() throws Exception {
        UUID loanId = UUID.randomUUID();
        LoanDto loanDTO = new LoanDto();
        loanDTO.setId(loanId);

        ResourceByIdGetEvent loanGetEvent = ResourceByIdGetEvent.builder()
                        .publisher(mockPublisher)
                        .topic(ResourceByIdGetEvent.LOAN_GET_TOPIC)
                        .id(loanId)
                        .build();

        when(mockEventFactory.buildGetLoanById(any(UUID.class))).thenReturn(loanGetEvent);

        when(mockPublisher
                .publishAndWait(any(ResourceByIdGetEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(loanDTO));

        ResponseEntity<LoanDto> response = loanApi.getLoanById(loanId);

        verify(mockPublisher, times(1)).publishAndWait(any(ResourceByIdGetEvent.class), any(String.class));

        assertEquals(loanDTO, response.getBody());
        assertEquals(loanId, response.getBody().getId());
    }

    @Test
    void createLoan_invalidLoanId(){
        assertThrows(RuntimeException.class,
                () -> loanApi.getLoanById(null)
        );
    }
}