package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.WalletAccountApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.walletAccount.WalletAccountCreateEvent;
import com.solofunds.memberaccounting.model.CreateWalletAccountDto;
import com.solofunds.memberaccounting.model.WalletAccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class WalletAccountDelegateImpl implements WalletAccountApiDelegate{

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<WalletAccountDto> createWalletAccount(CreateWalletAccountDto createWalletAccountDto) {
        try {
            WalletAccountCreateEvent event = eventFactory.buildWalletAccountCreateEvent(createWalletAccountDto);
            WalletAccountDto newWalletAccount = mapper.readValue(event.publishAndWait(), WalletAccountDto.class);
            return ResponseEntity.ok(newWalletAccount);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<WalletAccountDto> getWalletAccount(UUID id) {
        try {
            ResourceByIdGetEvent event = eventFactory.buildWalletAccountGetEvent(id);
            WalletAccountDto walletAccount = mapper.readValue(event.publishAndWait(), WalletAccountDto.class);
            return ResponseEntity.ok(walletAccount);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
