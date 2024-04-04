package com.solofunds.memberaccounting.messaging.messenger.event.loan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.LoanStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class LoanGetAllByMemberIdAndStatusEvent extends Event {

    @JsonIgnore
    public static final String TOPIC= "loan.memberById.requested";

    @JsonProperty
    private LoanStatus status;

    private UUID id;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
