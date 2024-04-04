package com.solofunds.memberaccounting.messaging.messenger.event.accountStatement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.Format;
import com.solofunds.memberaccounting.model.MemberDataDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class AccountStatementGetEvent extends Event implements Serializable {

    @JsonIgnore
    public static final String TOPIC = "accountStatement.get.requested";

    UUID id;

    MemberDataDto memberDataDto;

    Format format;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
