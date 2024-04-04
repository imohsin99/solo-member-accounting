package com.solofunds.memberaccounting.service.repositories;

import com.solofunds.memberaccounting.service.config.DatabaseSetupExtension;
import com.solofunds.memberaccounting.service.entities.AccountStatements;
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
class AccountStatementsRepositoryTest {

    @Autowired
    AccountStatementsRepository accountStatementsRepository;

    @Autowired
    WalletAccountRepository walletAccountRepository;

    @Test
    void findAllByWalletAccountId() {
        WalletAccount walletAccount = walletAccountRepository.save(new WalletAccount());

        LocalDateTime startDateTime =  LocalDateTime.now().minusMonths(1);
        LocalDateTime postingDateTime = LocalDateTime.now();

        AccountStatements statements = new AccountStatements();
        statements.setWalletAccountId(walletAccount.getId());
        statements.setStartDate(startDateTime);
        statements.setPostingDate(postingDateTime);
        accountStatementsRepository.save(statements);

        List<AccountStatements> statementsList = accountStatementsRepository.findAllByWalletAccountId(walletAccount.getId());

        assertEquals(1, statementsList.size());
        assertEquals(walletAccount.getId(), statementsList.get(0).getWalletAccountId());
    }
}
