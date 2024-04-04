package com.solofunds.memberaccounting.messaging.messenger.event.accountStatement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.MemberDataDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class AccountStatementDeleteEvent extends Event {

    @JsonIgnore
    public static final String TOPIC = "accountStatement.delete.requested";

    UUID id;

    MemberDataDto memberDataDTO;

    String format;

    @Override
    public String getDestination() {
        return TOPIC;
    }

}
