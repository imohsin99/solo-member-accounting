package com.solofunds.memberaccounting.messaging.messenger.event.balance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CalculateStartingBalanceDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class CalculateStartingBalanceEvent extends Event {
    @JsonIgnore
    public static final String TOPIC = "calculateStartingBalance.requested";

    private CalculateStartingBalanceDto calculateStartingBalanceDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
