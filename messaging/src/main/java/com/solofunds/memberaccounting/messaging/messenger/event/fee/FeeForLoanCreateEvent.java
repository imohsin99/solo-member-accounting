package com.solofunds.memberaccounting.messaging.messenger.event.fee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreateFeeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class FeeForLoanCreateEvent extends Event {

    @JsonIgnore
    public static  final String  TOPIC="feesForLoan.create.requested";

    private UUID id;

    private CreateFeeDto createFeeDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
