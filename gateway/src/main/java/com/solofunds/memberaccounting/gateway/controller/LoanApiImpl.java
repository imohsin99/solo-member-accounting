package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.LoanApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanPatchEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateLoanDto;
import com.solofunds.memberaccounting.model.LoanDto;
import com.solofunds.memberaccounting.model.UpdateLoanDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class LoanApiImpl implements LoanApiDelegate {

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<LoanDto> createLoan(CreateLoanDto createLoanDto) {
        try {
            LoanCreateEvent event = eventFactory.buildLoanCreateEvent(createLoanDto);
            LoanDto loan = mapper.readValue(event.publishAndWait(), LoanDto.class);
            return ResponseEntity.ok(loan);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<LoanDto> getLoanById(UUID id) {
        try {
            ResourceByIdGetEvent event = eventFactory.buildGetLoanById(id);
            LoanDto loanDTO = mapper.readValue(event.publishAndWait(), LoanDto.class);
            return ResponseEntity.ok(loanDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<LoanDto> updateLoan(UUID id, UpdateLoanDto updateLoanDTO) {
        try {
            LoanPatchEvent event = eventFactory.buildLoanPatchEvent(id,updateLoanDTO);
            LoanDto updatedLoan = mapper.readValue(event.publishAndWait(),LoanDto.class);
            return ResponseEntity.ok(updatedLoan);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
