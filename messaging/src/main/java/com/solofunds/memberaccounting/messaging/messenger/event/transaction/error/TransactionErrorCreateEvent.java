package com.solofunds.memberaccounting.messaging.messenger.event.transaction.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.model.TransactionErrorCodeDTO;
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
public class TransactionErrorCreateEvent extends Event implements Serializable {

    @JsonIgnore
    public static final String TOPIC= "transactionError.create.requested";

    private TransactionErrorCodeDTO transactionErrorCodeCapture;

    @Override
    public String getDestination() {
        return TOPIC;
    }

}
