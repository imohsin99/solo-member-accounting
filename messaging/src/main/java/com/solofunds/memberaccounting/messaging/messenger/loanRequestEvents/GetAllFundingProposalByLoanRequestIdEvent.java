package com.solofunds.memberaccounting.messaging.messenger.loanRequestEvents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class GetAllFundingProposalByLoanRequestIdEvent extends Event {
    @JsonIgnore
    public static final String TOPIC = "allFundingProposalByLoanRequestId.get.requested";

    UUID id;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
