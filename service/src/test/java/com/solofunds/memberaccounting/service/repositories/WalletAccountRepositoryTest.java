package com.solofunds.memberaccounting.service.repositories;

import com.solofunds.memberaccounting.service.config.DatabaseSetupExtension;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@ExtendWith(DatabaseSetupExtension.class)
public class WalletAccountRepositoryTest {

    @Autowired
    WalletAccountRepository walletAccountRepository;

    @Test
    public void testUUIDGenerationIsSuccessful(){
        WalletAccount walletAccount = new WalletAccount();
        walletAccount.setBankId(UUID.randomUUID());
        walletAccount.setSoloMemberGUID(UUID.randomUUID());
        WalletAccount savedWalletAccount = walletAccountRepository.save(walletAccount);

        Assertions.assertNotNull(savedWalletAccount.getId());
    }

}
