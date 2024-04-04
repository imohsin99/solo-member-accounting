package com.solofunds.memberaccounting.service.repositories;

import com.solofunds.memberaccounting.service.config.DatabaseSetupExtension;
import com.solofunds.memberaccounting.service.entities.MemberTransactions;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(DatabaseSetupExtension.class)
class MemberTransactionsRepositoryTest {

    @Autowired
    WalletAccountRepository walletAccountRepository;

    @Autowired
    MemberTransactionsRepository memberTransactionsRepository;

    private WalletAccount savedWalletAccount;
    private LocalDateTime lastSecondDateTime;
    private LocalDateTime lastSecondDateTime2;

    private MemberTransactions savedMemberTransaction1;

    @BeforeEach
    void setUp() {
        WalletAccount walletAccount = new WalletAccount();
        savedWalletAccount = walletAccountRepository.save(walletAccount);

        lastSecondDateTime = LocalDateTime.of(2023, 7, 31, 23, 59, 59, 999999499);
        lastSecondDateTime2 = LocalDateTime.of(2023, 7, 31, 23, 59, 59, 999999999);

        MemberTransactions memberTransactions1 = createMemberTransaction(savedWalletAccount.getId(), lastSecondDateTime);
        savedMemberTransaction1 = memberTransactionsRepository.save(memberTransactions1);

        MemberTransactions memberTransactions2 = createMemberTransaction(savedWalletAccount.getId(), lastSecondDateTime2);
        memberTransactionsRepository.save(memberTransactions2);
    }

    @Test
    void findAllByPostingDateBetween() {
        LocalDateTime startingDate = LocalDateTime.parse("2023-01-01T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2023-07-31T23:59:59.999999");

        List<MemberTransactions> memberTransactionsList = memberTransactionsRepository.findAllByPostingDateBetween(startingDate, endDate);

        assertEquals(1, memberTransactionsList.size());
        assertEquals(savedMemberTransaction1.getId(), memberTransactionsList.get(0).getId());
    }

    @Test
    void findAllByWalletAccountIdAndTransactionDateBetween() {
        LocalDateTime startingDate = LocalDateTime.parse("2023-01-01T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2023-07-31T23:59:59.999999");

        List<MemberTransactions> memberTransactionsList = memberTransactionsRepository.findAllByWalletAccountIdAndTransactionDateBetween(savedWalletAccount.getId(), startingDate, endDate);

        assertEquals(1, memberTransactionsList.size());
        assertEquals(savedMemberTransaction1.getId(), memberTransactionsList.get(0).getId());
    }

    private MemberTransactions createMemberTransaction(UUID walletAccountId, LocalDateTime transactionDate) {
        MemberTransactions memberTransactions = new MemberTransactions();
        memberTransactions.setWalletAccountId(walletAccountId);
        memberTransactions.setTransactionDate(transactionDate);
        memberTransactions.setPostingDate(transactionDate);
        return memberTransactions;
    }
}
