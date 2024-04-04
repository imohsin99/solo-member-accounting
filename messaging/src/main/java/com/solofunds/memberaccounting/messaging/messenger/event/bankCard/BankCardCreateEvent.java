package com.solofunds.memberaccounting.messaging.messenger.event.bankCard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreateBankCardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class BankCardCreateEvent extends Event implements Serializable {

    @JsonIgnore
    public static final String TOPIC = "bankCard.create.requested";

    private CreateBankCardDto createBankCardDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
