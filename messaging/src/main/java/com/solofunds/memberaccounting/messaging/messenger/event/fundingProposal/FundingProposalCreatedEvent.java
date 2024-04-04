package com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreateFundingProposalDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class FundingProposalCreatedEvent extends Event {
    @JsonIgnore
    public static final String TOPIC = "fundingProposal.create.requested";

    private CreateFundingProposalDto createFundingProposalDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
