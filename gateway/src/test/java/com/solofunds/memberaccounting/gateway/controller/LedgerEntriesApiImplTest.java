package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerByLoanIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerByMemberIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerEntryCreateEvent;
import com.solofunds.memberaccounting.model.LedgerEntryDto;
import com.solofunds.memberaccounting.model.Status;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LedgerEntriesApiImplTest {

    @Mock
    MessagePublisher mockPublisher;

    @Mock
    EventFactory mockEventFactory;

    @InjectMocks
    private LedgerEntriesApiImpl ledgerEntriesApiImpl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<LedgerEntryDto> getLedgerEntryDtos() {
        LedgerEntryDto ledgerEntryDto1 = new LedgerEntryDto(UUID.randomUUID(),null);
        LedgerEntryDto ledgerEntryDto2 = new LedgerEntryDto(UUID.randomUUID(),null);

        List<LedgerEntryDto> ledgerEntries = new ArrayList<>();
        ledgerEntries.add(ledgerEntryDto1);
        ledgerEntries.add(ledgerEntryDto2);
        return ledgerEntries;
    }

    @Test
    void getLedgerEntriesOfLoan() throws Exception {

        UUID loanId = UUID.randomUUID();
        List<LedgerEntryDto> ledgerEntries = getLedgerEntryDtos();

        LedgerByLoanIdGetEvent event = LedgerByLoanIdGetEvent.builder()
                .publisher(mockPublisher)
                .loan_id(loanId)
                .build();

        when(mockEventFactory.buildLedgerByLoanIdGetEvent(any(UUID.class))).thenReturn(event);

        when(mockPublisher
                .publishAndWait(any(LedgerByLoanIdGetEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(ledgerEntries));
        ResponseEntity<List<LedgerEntryDto>> response = ledgerEntriesApiImpl.getLedgerEntriesOfLoan(loanId);

        verify(mockPublisher, times(1)).publishAndWait(any(LedgerByLoanIdGetEvent.class), any(String.class));

        assertNotNull(response.getBody());
        assertEquals(ledgerEntries.size(), response.getBody().size());
    }

    @Test
    void getLedgerEntriesOfLoan_nullLoanId() {
        assertThrows(RuntimeException.class,
                () -> ledgerEntriesApiImpl.getLedgerEntriesOfLoan(null)
        );
    }

    @Test
    void getLedgerEntriesOfMember() throws Exception {

        UUID memberId = UUID.randomUUID();
        List<LedgerEntryDto> ledgerEntries = getLedgerEntryDtos();

        LedgerByMemberIdGetEvent event = LedgerByMemberIdGetEvent.builder()
                .publisher(mockPublisher)
                .memberId(memberId)
                .build();

        when(mockEventFactory.buildLedgerByMemberIdGetEvent(any(UUID.class))).thenReturn(event);

        when(mockPublisher
                .publishAndWait(any(LedgerByMemberIdGetEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(ledgerEntries));
        ResponseEntity<List<LedgerEntryDto>> response = ledgerEntriesApiImpl.getLedgerEntriesOfMember(memberId);

        verify(mockPublisher, times(1)).publishAndWait(any(LedgerByMemberIdGetEvent.class), any(String.class));

        assertNotNull(response.getBody());
        assertEquals(ledgerEntries.size(), response.getBody().size());
    }

    @Test
    void getLedgerEntriesOfMember_nullMemberId() {
        assertThrows(RuntimeException.class,
                () -> ledgerEntriesApiImpl.getLedgerEntriesOfMember(null)
        );
    }



    @Test
    void createLedgerEntryOfLoan() throws Exception {

        UUID loanId = UUID.randomUUID();
        UUID LOAN_LEDGER_ID = UUID.randomUUID();
        UUID SOLO_MEMBER_ID = UUID.randomUUID();
        UUID MEMBER_TRANSACTION_ID = UUID.randomUUID();

        LedgerEntryDto ledgerEntryDto = new LedgerEntryDto(UUID.randomUUID(),null);

        ledgerEntryDto.setLoanLedgerId(LOAN_LEDGER_ID);
        ledgerEntryDto.soloMemberGuid(SOLO_MEMBER_ID);
        ledgerEntryDto.setMemberTransactionIds(Arrays.asList(new UUID[]{MEMBER_TRANSACTION_ID}));
        ledgerEntryDto.setStatus(Status.PENDING);



        LedgerEntryCreateEvent event = LedgerEntryCreateEvent.builder()
                .publisher(mockPublisher)
                .loan_id(loanId)
                .ledgerEntryToCreate(ledgerEntryDto)
                .build();

        when(mockEventFactory.buildLedgerEntryCreateEvent(any(UUID.class),any(LedgerEntryDto.class))).thenReturn(event);

        when(mockPublisher.publishAndWait(any(LedgerEntryCreateEvent.class), anyString())).thenReturn(objectMapper.writeValueAsString(ledgerEntryDto));


        ResponseEntity<LedgerEntryDto> response = ledgerEntriesApiImpl.createLedgerEntryOfLoan(loanId,ledgerEntryDto);

        verify(mockPublisher, times(1)).publishAndWait(any(LedgerEntryCreateEvent.class), any(String.class));

        assertNotNull(response.getBody());




    }

    @Test
    void createLedgerEntryOfLoan_nullLoanId() throws RuntimeException {
        assertThrows(RuntimeException.class,
                () -> ledgerEntriesApiImpl.createLedgerEntryOfLoan(null,new LedgerEntryDto(UUID.randomUUID(),null))
        );
    }

}
