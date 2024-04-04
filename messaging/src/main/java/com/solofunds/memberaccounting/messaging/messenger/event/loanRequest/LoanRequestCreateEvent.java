package com.solofunds.memberaccounting.messaging.messenger.event.loanRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreateLoanRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class LoanRequestCreateEvent extends Event {
    @JsonIgnore
    public static final String TOPIC = "loanRequest.create.requested";

    private CreateLoanRequestDto createLoanRequest;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}