package com.solofunds.memberaccounting.service.service.impl;

import com.solofunds.memberaccounting.model.BankCardDto;
import com.solofunds.memberaccounting.model.CreateBankCardDto;
import com.solofunds.memberaccounting.service.entities.BankCard;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import com.solofunds.memberaccounting.service.repositories.BankCardRepository;
import com.solofunds.memberaccounting.service.repositories.WalletAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankCardServiceImplTest {

    @Mock
    BankCardRepository bankCardRepository;

    @Mock
    WalletAccountRepository walletAccountRepository;

    @InjectMocks
    BankCardServiceImpl service;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Test
    void addBankCardRecord() {
        fixture.givenDtoIsSavedToDB();
        fixture.walletAccountFindByIsCalled();
        fixture.whenCreateBankCardIsCalled();
        fixture.thenVerifySaveBankCardIsCalled();
        fixture.thenVerifyDtoIsNotNull();
    }

    @Test
    void getBankCardById() {
        fixture.givenRepositoryIsMockedToGetBankCardById();

        fixture.whenGetBankCardByIdIsCalled();

        fixture.thenVerifyDtoIsNotNull();
    }

    @Nested
    public class Fixture {
        UUID BANK_CARD_ID = UUID.randomUUID();
        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();
        BankCardDto actualBankCardDTO;

        void givenDtoIsSavedToDB() {
            BankCard bankCard = new BankCard();
            bankCard.setId(BANK_CARD_ID);
            bankCard.setWalletAccountId(WALLET_ACCOUNT_ID);
            when(bankCardRepository.save(any(BankCard.class))).thenReturn(bankCard);
        }

        void whenCreateBankCardIsCalled() {
            CreateBankCardDto createBankCardDto = new CreateBankCardDto();
            createBankCardDto.setWalletAccountId(WALLET_ACCOUNT_ID);
            actualBankCardDTO = service.createBankCard(createBankCardDto);
        }

        void walletAccountFindByIsCalled() {
            WalletAccount walletAccount = new WalletAccount();
            walletAccount.setId(WALLET_ACCOUNT_ID);

            when(walletAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(walletAccount));
        }

        void thenVerifySaveBankCardIsCalled() {
            verify(bankCardRepository, times(1)).save(any(BankCard.class));
        }

        void thenVerifyDtoIsNotNull() {
            assertNotNull(actualBankCardDTO);
            assertEquals(BANK_CARD_ID, actualBankCardDTO.getId());
        }

        void givenRepositoryIsMockedToGetBankCardById() {
            BankCard bankCard = new BankCard();
            bankCard.setId(BANK_CARD_ID);
            when(bankCardRepository.findById(any(UUID.class))).thenReturn(Optional.of(bankCard));
        }

        void whenGetBankCardByIdIsCalled() {
            actualBankCardDTO = service.getBankCardById(BANK_CARD_ID);
        }
    }
}
