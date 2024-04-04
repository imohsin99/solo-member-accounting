package com.solofunds.memberaccounting.messaging.messenger.event.memberTransaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreateMemberTransactionDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class MemberTransactionCreateEvent extends Event {

    @JsonIgnore
    public static final String TOPIC= "memberTransaction.create.requested";

    private CreateMemberTransactionDto createMemberTransactionDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
