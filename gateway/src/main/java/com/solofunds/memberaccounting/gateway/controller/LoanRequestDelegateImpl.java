package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.LoanRequestApiDelegate;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class LoanRequestDelegateImpl implements LoanRequestApiDelegate {
    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<LoanRequestDto> createLoanRequest(CreateLoanRequestDto createLoanRequestDto) {
        try {
            LoanRequestCreateEvent event = eventFactory.buildLoanRequestCreateEvent(createLoanRequestDto);
            LoanRequestDto newLoanRequest = mapper.readValue(event.publishAndWait(), LoanRequestDto.class);
            return ResponseEntity.ok(newLoanRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<LoanRequestDto> getLoanRequest(UUID id) {
        ResourceByIdGetEvent event = eventFactory.buildLoanRequestGetEvent(id);
        LoanRequestDto newLoanRequest = event.publishAndReceive(LoanRequestDto.class);
        return ResponseEntity.ok(newLoanRequest);
    }

    @Override
    public ResponseEntity<List<LoanRequestDto>> getFilteredAndSortedLoanRequests(LoanRequestStatus status,
                                                                                 SortDirection sort,
                                                                                 SortBy sortBy,
                                                                                 Boolean slpEligible) {
        try {
            GetFilteredAndSortedLoanRequestEvent event = eventFactory.
                    buildGetFilteredAndSortedLoanRequestEvent(status,sort,sortBy,slpEligible);

            List<LoanRequestDto> loanRequestDtoList = mapper.readValue(event.publishAndWait(), new TypeReference<>() {});
            return ResponseEntity.ok(loanRequestDtoList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<FundingProposalDto>> getAllFundingProposalByLoanRequestId(UUID id) {
        try {
            GetAllFundingProposalByLoanRequestIdEvent event = eventFactory.buildGetAllFundingProposalByLoanRequestIdEvent(id);
            List<FundingProposalDto> fundingProposalDtoList = mapper.readValue(event.publishAndWait(), new TypeReference<>() {});
            return ResponseEntity.ok(fundingProposalDtoList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
