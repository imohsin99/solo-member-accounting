package com.solofunds.memberaccounting.messaging.messenger.event.loan;

import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.UpdateLoanDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class LoanPatchEvent extends Event {

    public static final String TOPIC = "loan.update.requested";

    private UUID id;

    private UpdateLoanDto updateLoanDTO;

    @Override
    public String getDestination() {
        return TOPIC;
    }

}
