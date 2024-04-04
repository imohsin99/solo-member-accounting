package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.MemberApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanGetAllByMemberIdAndStatusEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.walletAccount.WalletAccountGetAllByMemberIdEvent;
import com.solofunds.memberaccounting.model.LoanDto;
import com.solofunds.memberaccounting.model.LoanStatus;
import com.solofunds.memberaccounting.model.WalletAccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class MemberApiDelegateImpl implements MemberApiDelegate {

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<List<LoanDto>> getAllLoansByMemberId(UUID memberAccountId, LoanStatus status) {
        try {
            LoanGetAllByMemberIdAndStatusEvent event = eventFactory.buildLoansByMemberAccountIdGetEvent(memberAccountId, status);
            List<LoanDto> loanDTOList = mapper.readValue(event.publishAndWait(),  new TypeReference<>() {});
            return ResponseEntity.ok(loanDTOList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<WalletAccountDto>> getAllWalletAccountsByMemberId(UUID memberId) {
        try {
            WalletAccountGetAllByMemberIdEvent event = eventFactory.buildWalletAccountGetAllByMemberIdEvent(memberId);
            List<WalletAccountDto> walletAccountDtoList = mapper.readValue(event.publishAndWait(),  new TypeReference<>() {});
            return ResponseEntity.ok(walletAccountDtoList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
