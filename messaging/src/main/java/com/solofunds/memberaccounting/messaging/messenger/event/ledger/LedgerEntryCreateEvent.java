package com.solofunds.memberaccounting.messaging.messenger.event.ledger;

import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.LedgerEntryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class LedgerEntryCreateEvent extends Event {

    public static  final String TOPIC="ledger.create.requested";

    private UUID loan_id;

    private LedgerEntryDto ledgerEntryToCreate;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
