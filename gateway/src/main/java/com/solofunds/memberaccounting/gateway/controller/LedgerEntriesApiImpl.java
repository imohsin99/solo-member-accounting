package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.LedgerEntriesApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerByLoanIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerByMemberIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerEntryCreateEvent;
import com.solofunds.memberaccounting.model.LedgerEntryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class LedgerEntriesApiImpl implements LedgerEntriesApiDelegate {

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<List<LedgerEntryDto>> getLedgerEntriesOfLoan(UUID loanId) {
        try {
            LedgerByLoanIdGetEvent event = eventFactory.buildLedgerByLoanIdGetEvent(loanId);
            List<LedgerEntryDto> ledgerEntries = mapper.readValue(event.publishAndWait(), new TypeReference<>() {});
            return ResponseEntity.ok(ledgerEntries);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<LedgerEntryDto>> getLedgerEntriesOfMember(UUID memberGuid) {
        try {
            LedgerByMemberIdGetEvent event = eventFactory.buildLedgerByMemberIdGetEvent(memberGuid);
            List<LedgerEntryDto> ledgerEntries = mapper.readValue(event.publishAndWait(), new TypeReference<>() {});
            return ResponseEntity.ok(ledgerEntries);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ResponseEntity<LedgerEntryDto> createLedgerEntryOfLoan(UUID loanId, LedgerEntryDto ledgerEntryDto) {
        try {
            LedgerEntryCreateEvent event = eventFactory.buildLedgerEntryCreateEvent(loanId, ledgerEntryDto);
            LedgerEntryDto ledgerEntry = mapper.readValue(event.publishAndWait(), LedgerEntryDto.class);
            return ResponseEntity.ok(ledgerEntry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
