package com.solofunds.memberaccounting.messaging.messenger.event.walletAccount;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class WalletAccountGetAllByMemberIdEvent extends Event {

    @JsonIgnore
    public static final String TOPIC= "walletAccount.getAllByMemberId.requested";

    private UUID memberId;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
