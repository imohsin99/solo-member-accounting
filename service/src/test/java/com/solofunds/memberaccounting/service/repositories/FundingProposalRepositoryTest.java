package com.solofunds.memberaccounting.service.repositories;

import com.solofunds.memberaccounting.service.config.DatabaseSetupExtension;
import com.solofunds.memberaccounting.service.entities.FundingProposal;
import com.solofunds.memberaccounting.service.entities.LoanRequest;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(DatabaseSetupExtension.class)
public class FundingProposalRepositoryTest {
    @Autowired
    FundingProposalRepository fundingProposalRepository;

    @Autowired
    LoanRequestRepository loanRequestRepository;

    @Autowired
    WalletAccountRepository walletAccountRepository;

    @Test
    void findAllByWalletAccountId() {
        WalletAccount walletAccount = walletAccountRepository.save(new WalletAccount());
        LoanRequest loanRequest = loanRequestRepository.save(new LoanRequest());

        LocalDateTime startDateTime =  LocalDateTime.now().minusMonths(1);
        LocalDateTime postingDateTime = LocalDateTime.now();

        FundingProposal fundingProposal = new FundingProposal();
        fundingProposal.setLoanRequest(loanRequest);
        fundingProposal.setWalletAccount(walletAccount);
        fundingProposal.setUpdatedAt(postingDateTime);
        fundingProposal.setCreatedAt(startDateTime);

        fundingProposalRepository.save(fundingProposal);

        List<FundingProposal> fundingProposalList = fundingProposalRepository.findByLoanRequest_Id(loanRequest.getId());

        assertEquals(1, fundingProposalList.size());
        assertEquals(loanRequest.getId(), fundingProposalList.get(0).getLoanRequest().getId());
    }
}