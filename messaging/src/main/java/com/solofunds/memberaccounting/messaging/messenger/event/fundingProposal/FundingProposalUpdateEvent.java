package com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.UpdateFundingProposalDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class FundingProposalUpdateEvent extends Event {
    @JsonIgnore
    public static final String TOPIC = "fundingProposal.update.requested";

    private UUID id;
    private UpdateFundingProposalDto fundingProposalDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
