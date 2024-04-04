package com.solofunds.memberaccounting.service.repositories;

import com.solofunds.memberaccounting.model.LoanRequestStatus;
import com.solofunds.memberaccounting.service.config.DatabaseSetupExtension;
import com.solofunds.memberaccounting.service.entities.LoanRequest;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(DatabaseSetupExtension.class)
public class LoanRequestRepositoryTest {

    @Autowired
    LoanRequestRepository loanRequestRepository;

    @Autowired
    WalletAccountRepository walletAccountRepository;

    @Test
    void testFindByStatus() {
        LoanRequestStatus status = LoanRequestStatus.ACTIVE;

        WalletAccount walletAccount = walletAccountRepository.save(new WalletAccount());

        LoanRequest loanRequest = LoanRequest.builder()
                .status(status)
                .walletAccount(walletAccount)
                .build();
        loanRequestRepository.save(loanRequest);

        List<LoanRequest> loanRequestList = loanRequestRepository.findByStatus(status);

        assertEquals(1, loanRequestList.size());
        assertEquals(status, loanRequestList.get(0).getStatus());
    }

    @Test
    void testFindByGuaranteeAbleAndStatus() {
        LoanRequestStatus status = LoanRequestStatus.ACTIVE;
        boolean slpEligible = true;

        WalletAccount walletAccount = walletAccountRepository.save(new WalletAccount());

        LoanRequest loanRequest = LoanRequest.builder()
                .status(status)
                .guaranteeable(slpEligible)
                .walletAccount(walletAccount)
                .build();
        loanRequestRepository.save(loanRequest);

        List<LoanRequest> loanRequestList = loanRequestRepository.findByGuaranteeableAndStatus(slpEligible, status);

        assertEquals(1, loanRequestList.size());
        assertEquals(slpEligible, loanRequestList.get(0).isGuaranteeable());
        assertEquals(status, loanRequestList.get(0).getStatus());
    }
}