package com.solofunds.memberaccounting.messaging.messenger.event.accountStatement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class AccountStatementGetAllEvent extends Event implements Serializable {

    @JsonIgnore
    public static final String TOPIC = "accountStatement.get.all.requested";

    @Override
    public String getDestination() {
        return TOPIC;
    }

}
