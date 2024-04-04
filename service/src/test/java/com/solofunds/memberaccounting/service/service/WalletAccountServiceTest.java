package com.solofunds.memberaccounting.service.service;

import com.solofunds.memberaccounting.model.CreateWalletAccountDto;
import com.solofunds.memberaccounting.model.WalletAccountDto;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import com.solofunds.memberaccounting.service.mappers.WalletAccountMapper;
import com.solofunds.memberaccounting.service.repositories.WalletAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WalletAccountServiceTest {

    @Mock
    WalletAccountRepository walletAccountRepository;

    @Mock
    WalletAccountMapper walletAccountMapper;

    @Mock
    CreateWalletAccountDto createWalletAccountDto;

    @Mock
    WalletAccountDto walletAccountDto;

    @Mock
    WalletAccount walletAccount;

    @InjectMocks
    WalletAccountService walletAccountService;

    Fixture fixture;

    @BeforeEach
    void setup(){
        fixture = new Fixture();
    }

    @Test
    void testCreateWalletAccount(){
        fixture.givenCreateWalletAccountDtoIsMocked();
        fixture.givenWalletAccountIsMocked();
        fixture.givenWalletAccountDtoISMappedToEntity();
        fixture.givenSaveOfRepoIsCalled();

        fixture.whenCreateWalletAccountOfServiceIsCalled();

        fixture.thenTestAsserts();
    }

    @Test
    void testGetWalletAccountById(){
        fixture.givenFindByIdOfRepositoryIsCalld();
        fixture.givenWalletAccountIsMappedToDto();

        fixture.whenGetWalletAccountByIdOfServiceIsCalled();

        fixture.thenAssertSavedWalletAccountDtoIsNotNul();
    }

    @Test
    void testGetAllWalletAccountByMemberId(){
        fixture.givenFindAllWalletAccountByIdOfRepositoryIsCalled();

        fixture.whenGetAllWalletAccountByMemberIdIsCalled();

        fixture.thenVarifyAssertsForGetAllMembersByMemberId();
    }

    private class Fixture {

        private final UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

        private final UUID SOLO_MEMBER_GUID = UUID.randomUUID();

        private final UUID BANK_ID = UUID.randomUUID();

        private WalletAccountDto savedDto;

        private WalletAccountDto expectedWalletAccountDto;

        private List<WalletAccountDto> walletAccountDtoList;

        public void givenCreateWalletAccountDtoIsMocked(){
            createWalletAccountDto.setBankId(BANK_ID);
            createWalletAccountDto.setSoloMemberGUID(SOLO_MEMBER_GUID);
        }

        public void givenWalletAccountIsMocked(){
            walletAccount.setId(WALLET_ACCOUNT_ID);
            walletAccount.setBankId(walletAccountDto.getBankId());
            walletAccount.setSoloMemberGUID(walletAccountDto.getSoloMemberGUID());
        }

        public void givenWalletAccountDtoISMappedToEntity(){
            when(walletAccountMapper.toEntity(any(CreateWalletAccountDto.class))).thenReturn(walletAccount);
        }

        public void givenSaveOfRepoIsCalled() {
            when(walletAccountRepository.save(any(WalletAccount.class))).thenReturn(walletAccount);
        }

        public void whenCreateWalletAccountOfServiceIsCalled() {
            savedDto = walletAccountService.createWalletAccount(createWalletAccountDto);
        }

        public void thenTestAsserts() {
            assertNotNull(savedDto);
            assertEquals(savedDto.getBankId(), walletAccount.getBankId());
            assertEquals(savedDto.getSoloMemberGUID(), walletAccount.getSoloMemberGUID());
        }

        public void givenFindByIdOfRepositoryIsCalld(){
            when(walletAccountRepository.findById(WALLET_ACCOUNT_ID)).thenReturn(Optional.of(walletAccount));
        }

        public void givenWalletAccountIsMappedToDto(){
            when(walletAccountMapper.toDto(walletAccount)).thenReturn(walletAccountDto);
        }

        public void whenGetWalletAccountByIdOfServiceIsCalled() {
            expectedWalletAccountDto = walletAccountService.getWalletAccountById(WALLET_ACCOUNT_ID);
        }

        public void thenAssertSavedWalletAccountDtoIsNotNul() {
            assertNotNull(expectedWalletAccountDto);
        }

        public void givenFindAllWalletAccountByIdOfRepositoryIsCalled() {
            when(walletAccountRepository.findAllBySoloMemberGUID(SOLO_MEMBER_GUID)).thenReturn(List.of(walletAccount));
        }

        public void whenGetAllWalletAccountByMemberIdIsCalled() {
            walletAccountDtoList = walletAccountService.getAllWalletAccountByMemberId(SOLO_MEMBER_GUID);
        }

        public void thenVarifyAssertsForGetAllMembersByMemberId(){
            assertEquals(1, walletAccountDtoList.size());
        }
    }
}
