package com.solofunds.memberaccounting.messaging.messenger.event.ledger;

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
public class LedgerByLoanIdGetEvent extends Event {

    public static  final String TOPIC="ledger.getByLoanId.requested";

    private UUID loan_id;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}