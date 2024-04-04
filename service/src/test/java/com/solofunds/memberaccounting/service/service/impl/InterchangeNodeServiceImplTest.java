package com.solofunds.memberaccounting.service.service.impl;

import com.solofunds.memberaccounting.model.CreateInterchangeNodeDto;
import com.solofunds.memberaccounting.model.InterchangeNodeDto;
import com.solofunds.memberaccounting.service.entities.BankCard;
import com.solofunds.memberaccounting.service.entities.InterchangeNode;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import com.solofunds.memberaccounting.service.repositories.BankCardRepository;
import com.solofunds.memberaccounting.service.repositories.InterchangeNodeRepository;
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
class InterchangeNodeServiceImplTest {

    @Mock
    InterchangeNodeRepository interchangeNodeRepository;

    @Mock
    WalletAccountRepository walletAccountRepository;

    @Mock
    BankCardRepository bankCardRepository;

    @InjectMocks
    InterchangeNodeServiceImpl service;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Test
    void addInterchangeNode() {
        fixture.givenDtoIsSavedToDB();
        fixture.walletAccountFindByIsCalled();
        fixture.bankCardFindByIsCalled();
        fixture.whenCreateInterchangeNodeIsCalled();
        fixture.thenVerifySaveInterchangeNodeIsCalled();
        fixture.thenVerifyDtoIsNotNull();
    }

    @Test
    void getInterchangeNodeById() {
        fixture.givenRepositoryIsMockedToGetInterchangeNodeById();

        fixture.whenGetInterchangeNodeByIdIsCalled();

        fixture.thenVerifyDtoIsNotNull();
    }

    @Nested
    public class Fixture {
        UUID INTERCHANGE_NODE_ID = UUID.randomUUID();
        UUID EXISTING_INTERCHANGE_NODE_ID = UUID.randomUUID();
        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();
        UUID BANK_CARD_ID = UUID.randomUUID();
        InterchangeNodeDto actualInterchangeNodeDTO;

        void givenDtoIsSavedToDB() {
            InterchangeNode interchangeNode = new InterchangeNode();
            interchangeNode.setId(INTERCHANGE_NODE_ID);
            interchangeNode.setWalletAccountId(WALLET_ACCOUNT_ID);
            interchangeNode.setBankCardId(BANK_CARD_ID);
            when(interchangeNodeRepository.save(any(InterchangeNode.class))).thenReturn(interchangeNode);
        }

        void whenCreateInterchangeNodeIsCalled() {
            CreateInterchangeNodeDto createInterchangeNodeDto = new CreateInterchangeNodeDto();
            createInterchangeNodeDto.setWalletAccountId(WALLET_ACCOUNT_ID);
            createInterchangeNodeDto.setBankCardId(BANK_CARD_ID);
            actualInterchangeNodeDTO = service.createInterchangeNode(createInterchangeNodeDto);
        }

        void walletAccountFindByIsCalled() {
            WalletAccount walletAccount = new WalletAccount();
            walletAccount.setId(WALLET_ACCOUNT_ID);
            walletAccount.setBankId(BANK_CARD_ID);

            when(walletAccountRepository.findById(any(UUID.class))).thenReturn(Optional.of(walletAccount));
        }

        void bankCardFindByIsCalled() {
            BankCard bankCard = new BankCard();
            bankCard.setId(BANK_CARD_ID);
            bankCard.setInterchangeNodeId(EXISTING_INTERCHANGE_NODE_ID);

            when(bankCardRepository.findById(any(UUID.class))).thenReturn(Optional.of(bankCard));
        }

        void thenVerifySaveInterchangeNodeIsCalled() {
            verify(interchangeNodeRepository, times(1)).save(any(InterchangeNode.class));
        }

        void thenVerifyDtoIsNotNull() {
            assertNotNull(actualInterchangeNodeDTO);
            assertEquals(INTERCHANGE_NODE_ID, actualInterchangeNodeDTO.getId());
        }

        void givenRepositoryIsMockedToGetInterchangeNodeById() {
            InterchangeNode interchangeNode = new InterchangeNode();
            interchangeNode.setId(INTERCHANGE_NODE_ID);
            when(interchangeNodeRepository.findById(any(UUID.class))).thenReturn(Optional.of(interchangeNode));
        }

        void whenGetInterchangeNodeByIdIsCalled() {
            actualInterchangeNodeDTO = service.getInterchangeNodeById(INTERCHANGE_NODE_ID);
        }
    }
}
