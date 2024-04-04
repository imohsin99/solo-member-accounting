package com.solofunds.memberaccounting.messaging.messenger.event.walletAccount;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreateWalletAccountDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class WalletAccountCreateEvent extends Event {

    @JsonIgnore
    public static final String TOPIC= "walletAccount.create.requested";

    private CreateWalletAccountDto createWalletAccountDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
