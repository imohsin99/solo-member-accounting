package com.solofunds.memberaccounting.service.service.impl;

import com.solofunds.memberaccounting.model.CreateMemberTransactionDto;
import com.solofunds.memberaccounting.model.MemberTransactionDto;
import com.solofunds.memberaccounting.service.entities.MemberTransactions;
import com.solofunds.memberaccounting.service.mappers.MemberTransactionMapper;
import com.solofunds.memberaccounting.service.repositories.MemberTransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
@SpringBootTest
class MemberTransactionServiceImplTest {
 
    @Mock
    MemberTransactionsRepository memberTransactionsRepository;
 
    @Mock
    MemberTransactionMapper mapper;
 
    @Mock
    MemberTransactions memberTransaction;
 
    @Mock
    MemberTransactionDto memberTransactionDTO;

    @Mock
    CreateMemberTransactionDto createMemberTransactionDto;
 
    @InjectMocks
    MemberTransactionServiceImpl memberTransactionService;
 
         
    Fixture fixture;
 
    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }
 
    @Test
    void testGetMemberTransactionByIdSuccess(){
        fixture.givenMemberTransactionIsMocked();
        fixture.givenMemberTransactionDTOIsMocked();
        fixture.givenMemberTransactionIsMapperToDTO();
        fixture.givenMemberTransactionRepositoryIsMockedToFindMemberTransaction();

        fixture.whenGetMemberTransactionByIdOfServiceIsCalled();

        fixture.thenVerifyMemberTransactionIsNotNull();
        fixture.thenVerifyResultById();
        fixture.thenVerifyThatRepositoryIsCalledOnlyOnce();
    }
 
    @Test
    void testGetMemberTransactionByIdFailure(){
        fixture.givenMemberTransactionRepositoryIsMockedToGetEmptyMemberTransaction();

        fixture.thenVerifyResourceNotFoundException();
    }
 
    @Test
    void testCreateMemberTransaction(){
        fixture.givenMemberTransactionRequestIsMocked();
        fixture.givenMemberTransactionIsMocked();
        fixture.givenMemberTransactionRequestIsMapperToMemberTransactionDTO();
        fixture.givenMemberTransactionDTOIsMocked();
        fixture.givenMemberTransactionIsMapperToDTO();
        fixture.givenMemberTransactionRepositoryIsMockedToSaveMemberTransaction();

        fixture.whenCreateMemberTransactionOfServiceIsCalled();

        fixture.thenVerifyThatRepositoryIsCalledOnlyOnceWhenSaveIsCalled();
    }
 
    private class Fixture{
        private final UUID MEMBER_TRANSACTION_ID = UUID.randomUUID();
        private final UUID WALLET_ACCOUNT_ID = UUID.randomUUID();
        private final UUID SOLO_MEMBER_GUID = UUID.randomUUID();
        private final UUID LOAN_ID = UUID.randomUUID();
        private final UUID LEDGER_TRANSACTION_ID = UUID.randomUUID();
        private final UUID PAYMENT_ORDER_ID = UUID.randomUUID();
        private final BigDecimal AMOUNT  = BigDecimal.valueOf(100);
        private final String CURRENCY_CODE = "USD";
        private final String CURRENCY_EXPONENT = "2";
        MemberTransactionDto actualMemberTransaction;
 
        void givenMemberTransactionIsMocked(){
            when(memberTransaction.getId()).thenReturn(MEMBER_TRANSACTION_ID);
            when(memberTransaction.getWalletAccountId()).thenReturn(WALLET_ACCOUNT_ID);
            when(memberTransaction.getSoloMemberGUID()).thenReturn(SOLO_MEMBER_GUID);
            when(memberTransaction.getLoanId()).thenReturn(LOAN_ID);
            when(memberTransaction.getLedgerTransactionId()).thenReturn(LEDGER_TRANSACTION_ID);
            when(memberTransaction.getPaymentOrderId()).thenReturn(PAYMENT_ORDER_ID);
            when(memberTransaction.getAmount()).thenReturn(AMOUNT);
            when(memberTransaction.getCurrencyCode()).thenReturn(CURRENCY_CODE);
            when(memberTransaction.getCurrencyExponent()).thenReturn(CURRENCY_EXPONENT);
        }
 
        void givenMemberTransactionDTOIsMocked(){
            when(memberTransactionDTO.getId()).thenReturn(MEMBER_TRANSACTION_ID);
            when(memberTransactionDTO.getWalletAccountId()).thenReturn(WALLET_ACCOUNT_ID);
            when(memberTransactionDTO.getSoloMemberGUID()).thenReturn(SOLO_MEMBER_GUID);
            when(memberTransactionDTO.getLoanId()).thenReturn(LOAN_ID);
            when(memberTransactionDTO.getLedgerTransactionId()).thenReturn(LEDGER_TRANSACTION_ID);
            when(memberTransactionDTO.getPaymentOrderId()).thenReturn(PAYMENT_ORDER_ID);
            when(memberTransactionDTO.getAmount()).thenReturn(AMOUNT);
            when(memberTransactionDTO.getCurrencyCode()).thenReturn(CURRENCY_CODE);
            when(memberTransactionDTO.getCurrencyExponent()).thenReturn(CURRENCY_EXPONENT);
        }
 
        void givenMemberTransactionIsMapperToDTO(){
            when(mapper.toDTO(memberTransaction)).thenReturn(memberTransactionDTO);
        }
 
        void givenMemberTransactionRequestIsMocked(){
            when(createMemberTransactionDto.getWalletAccountId()).thenReturn(WALLET_ACCOUNT_ID);
            when(createMemberTransactionDto.getSoloMemberGUID()).thenReturn(SOLO_MEMBER_GUID);
            when(createMemberTransactionDto.getLoanId()).thenReturn(LOAN_ID);
            when(createMemberTransactionDto.getLedgerTransactionId()).thenReturn(LEDGER_TRANSACTION_ID);
            when(createMemberTransactionDto.getPaymentOrderId()).thenReturn(PAYMENT_ORDER_ID);
            when(createMemberTransactionDto.getAmount()).thenReturn(AMOUNT);
            when(createMemberTransactionDto.getCurrencyCode()).thenReturn(CURRENCY_CODE);
            when(createMemberTransactionDto.getCurrencyExponent()).thenReturn(CURRENCY_EXPONENT);
        }
 
        void givenMemberTransactionRequestIsMapperToMemberTransactionDTO(){
            when(mapper.toMemberTransactions(createMemberTransactionDto)).thenReturn(memberTransaction);
        }
 
        public void givenMemberTransactionRepositoryIsMockedToFindMemberTransaction() {
            when(memberTransactionsRepository.findById(MEMBER_TRANSACTION_ID)).thenReturn(Optional.of(memberTransaction));
        }
 
        public void whenGetMemberTransactionByIdOfServiceIsCalled() {
            actualMemberTransaction = memberTransactionService.getMemberTransactionById(MEMBER_TRANSACTION_ID);
        }
 
        public void thenVerifyMemberTransactionIsNotNull() {
            assertNotNull(actualMemberTransaction);
        }
 
        public void thenVerifyResultById() {
            assertEquals(memberTransaction.getId(),actualMemberTransaction.getId());
        }
 
        public void thenVerifyThatRepositoryIsCalledOnlyOnce() {
            verify(memberTransactionsRepository, times(1)).findById(MEMBER_TRANSACTION_ID);
        }
 
        public void givenMemberTransactionRepositoryIsMockedToGetEmptyMemberTransaction() {
            when(memberTransactionsRepository.findById(MEMBER_TRANSACTION_ID)).thenReturn(Optional.empty());
        }
 
        public void thenVerifyResourceNotFoundException() {
            assertThrows(NoSuchElementException.class, () -> {
                memberTransactionService.getMemberTransactionById(MEMBER_TRANSACTION_ID);
            });
        }
 
        public void givenMemberTransactionRepositoryIsMockedToSaveMemberTransaction() {
            when(memberTransactionsRepository.save(any(MemberTransactions.class))).thenReturn(memberTransaction);
        }
 
        public void thenVerifyThatRepositoryIsCalledOnlyOnceWhenSaveIsCalled() {
            verify(memberTransactionsRepository, times(1)).save(any(MemberTransactions.class));
        }
 
        public void whenCreateMemberTransactionOfServiceIsCalled() {
            actualMemberTransaction=memberTransactionService.createMemberTransaction(createMemberTransactionDto);
        }
    }

}