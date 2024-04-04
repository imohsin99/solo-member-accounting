package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.FundingProposalApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal.FundingProposalCreatedEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal.FundingProposalUpdateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateFundingProposalDto;
import com.solofunds.memberaccounting.model.FundingProposalDto;
import com.solofunds.memberaccounting.model.UpdateFundingProposalDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class FundingProposalDelegateImpl implements FundingProposalApiDelegate {
    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<FundingProposalDto> createFundingProposal(CreateFundingProposalDto createFundingProposalDto) {
        try {
            FundingProposalCreatedEvent event = eventFactory.buildFundingProposalCreatedEvent(createFundingProposalDto);
            FundingProposalDto newFundingProposal = mapper.readValue(event.publishAndWait(), FundingProposalDto.class);
            return ResponseEntity.ok(newFundingProposal);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<FundingProposalDto> getFundingProposal(UUID id) {
        try {
            ResourceByIdGetEvent event = eventFactory.buildFundingProposalGetEvent(id);
            FundingProposalDto newFundingProposal = mapper.readValue(event.publishAndWait(), FundingProposalDto.class);
            return ResponseEntity.ok(newFundingProposal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<FundingProposalDto> updateFundingProposal(UUID id, UpdateFundingProposalDto fundingProposalDto) {
        try {
            FundingProposalUpdateEvent event = eventFactory.buildFundingProposalUpdateEvent(id, fundingProposalDto);
            FundingProposalDto updatedFundingProposal = mapper.readValue(event.publishAndWait(), FundingProposalDto.class);
            return ResponseEntity.ok(updatedFundingProposal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
