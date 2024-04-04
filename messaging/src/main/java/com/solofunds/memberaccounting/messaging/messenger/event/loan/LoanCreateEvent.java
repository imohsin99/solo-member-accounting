package com.solofunds.memberaccounting.messaging.messenger.event.loan;

import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreateLoanDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class LoanCreateEvent extends Event {

    public static final String TOPIC="loan.create.requested";

    private CreateLoanDto createLoanDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
