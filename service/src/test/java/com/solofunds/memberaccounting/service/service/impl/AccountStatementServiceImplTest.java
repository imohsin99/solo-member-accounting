package com.solofunds.memberaccounting.service.service.impl;

import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.model.MemberTransactionDto;
import com.solofunds.memberaccounting.service.entities.AccountStatements;
import com.solofunds.memberaccounting.service.entities.MemberTransactions;
import com.solofunds.memberaccounting.service.mappers.AccountStatementMapper;
import com.solofunds.memberaccounting.service.repositories.AccountStatementsRepository;
import com.solofunds.memberaccounting.service.repositories.MemberTransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
 
@SpringBootTest
class AccountStatementServiceImplTest {
 
    @Mock
    AccountStatementsRepository accountStatementsRepository;
 
    @Mock
    MemberTransactionsRepository memberTransactionsRepository;
 
    @Mock
    AccountStatementMapper accountStatementMapper;
 
    @InjectMocks
    AccountStatementServiceImpl accountStatementService;
 
    @Mock
    AccountStatements accountStatement;
 
    @Mock
    AccountStatementDto accountStatementDTO;
 
    @Mock
    MemberTransactions memberTransaction;
 
    @Mock MemberTransactionDto memberTransactionDTO;
 
    Fixture fixture;
 
    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }
 
    @Test
    void testGetAccountStatementByIdSuccess(){
        fixture.givenAccountStatementIsMocked();
        fixture.givenAccountStatementDTOIsMocked();
        fixture.givenAccountStatementIsMappedToAccountStatementDTO();
        fixture.givenAccountStatementRepositoryIsMockedToFindByIdSuccess();
 
        fixture.whenAccountStatementServiceIsCalledToGetAccountStatementById();
 
        fixture.thenVerifyTheAccountStatementById();
        fixture.thenVerifyThatFindByIdCalledOnce();
    }
 
    @Test
    void testGetAccountStatementByIdFailure(){
        fixture.givenAccountStatementRepositoryIsMockedToFindByIdFailure();
        fixture.thenVerifyGetAccountStatementByIdThrowException();
    }
 
    @Test
    void testGetAllAccountStatement(){
        fixture.givenAccountStatementIsMocked();
        fixture.givenAccountStatementDTOIsMocked();
        fixture.givenAccountStatementIsMappedToAccountStatementDTO();
        fixture.givenAccountStatementRepositoryIsMockedToFindAll();
 
        fixture.whenAccountStatementServiceIsCalledToGetAllAccountStatements();
 
        fixture.thenVerifyTheFirstAccountStatementById();
        fixture.thenVerifyTheSizeOfAccountStatementList();
        fixture.thenVerifyThatFindAllCalledOnce();
    }
 
    @Test
    void testDeleteAccountStatementById(){
        fixture.givenAccountStatementIsMocked();
        fixture.givenAccountStatementDTOIsMocked();
        fixture.givenMemberTransactionIsMocked();
        fixture.givenAccountStatementIsMappedToAccountStatementDTO();
        fixture.givenAccountStatementRepositoryIsMockedToFindByIdSuccess();
        fixture.givenAccountStatementDTOIsMappedToAccountStatement();
 
        fixture.whenAccountStatementServiceIsCalledToDeleteAllAccountStatements();
 
        fixture.thenVerifyByWalletIdOfAccountStatement();
        fixture.thenVerifyByTheSoloMemberGUIDOfMemberTransaction();
        fixture.thenVerifyThatDeleteMethodOfRepositoryCalledOnce();
    }

    @Test
    void testCreateAccountStatement(){
        fixture.givenMemberTransactionIsMocked();
        fixture.givenAccountStatementIsMappedToAccountStatementDTO();
        fixture.givenMemberTransactionRepositoryIsMockedToFindAllByTransactionDateAfter();
        fixture.givenAccountStatementRepositoryIsMockedToSave();

        fixture.whenAccountStatementServiceIsCalledToCreateAccountStatement();

        fixture.thenVerifyMemberTransactionRepositoryIsCalledToFindAllByTransactionDateAfter();
        fixture.thenVerifyThatAccountStatementRepositoryIsCalledOnce();
    }

    @Test
    void testGetAccountStatementByWalletId(){
        fixture.givenAccountStatementIsMocked();
        fixture.givenAccountStatementRepositoryIsMockedToFindByWalletId();

        fixture.whenAccountStatementServiceIsCalledToGetAccountStatementByWalletId();

        fixture.thenVerifyTheSizeOfAccountStatementList();
        fixture.thenVerifyThatFindAllByWalletIdCalledOnce();
        fixture.thenVerifyThatAccountStatementHasSameWalletId();
    }

    @Test
    void testCreateAccountStatementFailure(){
        fixture.thenVerifyIllegalArgumentExceptionIsThrownByCreateAccountStatement();
    }

    private class Fixture{
        private final UUID ACCOUNT_STATEMENT_ID = UUID.randomUUID();
 
        private final UUID MEMBER_TRANSACTION_ID = UUID.randomUUID();

        private final UUID WALLET_ACCOUNT_ID = UUID.randomUUID();
 
        private final UUID SOLO_MEMBER_GUID = UUID.randomUUID();
 
        private final UUID LOAN_ID = UUID.randomUUID();
 
        private final UUID LEDGER_TRANSACTION_ID = UUID.randomUUID();
 
        private final UUID PAYMENT_ORDER_ID = UUID.randomUUID();
 
        private final BigDecimal AMOUNT = BigDecimal.valueOf(100);
 
        private final String CURRENCY_CODE = "USD";
 
        private final String CURRENCY_EXPONENT = "2";
 
        private final LocalDate START_TIME = LocalDate.now();

        private final LocalDate END_TIME = LocalDate.now();

        private final LocalDateTime START_DATE_TIME = LocalDateTime.now().toLocalDate().atTime(0, 0);

        private final LocalDateTime END_DATE_TIME = LocalDateTime.now().toLocalDate().atTime(23, 59, 59, 999999499);
 
        AccountStatementDto actualAccountStatement;
 
        List<AccountStatementDto> actualAccountStatements;
 
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
            when(memberTransaction.getCreatedAt()).thenReturn(LocalDateTime.now());
        }
 
        void givenAccountStatementIsMocked(){
            when(accountStatement.getId()).thenReturn(ACCOUNT_STATEMENT_ID);
            when(accountStatement.getMemberTransactions()).thenReturn(Set.of(memberTransaction));
            when(accountStatement.getWalletAccountId()).thenReturn(WALLET_ACCOUNT_ID);
            when(accountStatement.getStartDate()).thenReturn(START_DATE_TIME);
        }
 
        void givenAccountStatementDTOIsMocked(){
            when(accountStatementDTO.getId()).thenReturn(ACCOUNT_STATEMENT_ID);
            when(accountStatementDTO.getMemberTransactions()).thenReturn(List.of(memberTransactionDTO));
            when(accountStatementDTO.getWalletAccountId()).thenReturn(WALLET_ACCOUNT_ID);
            when(accountStatementDTO.getStartDate()).thenReturn(START_DATE_TIME.atOffset(ZoneOffset.UTC));
        }
 
        void givenAccountStatementIsMappedToAccountStatementDTO(){
            when(accountStatementMapper.toDTO(any(AccountStatements.class))).thenReturn(accountStatementDTO);
        }
 
        void givenAccountStatementDTOIsMappedToAccountStatement(){
            when(accountStatementMapper.toDTO(accountStatement)).thenReturn(any(AccountStatementDto.class));
        }
 
        public void givenAccountStatementRepositoryIsMockedToFindByIdSuccess() {
            when(accountStatementsRepository.findById(ACCOUNT_STATEMENT_ID)).thenReturn(Optional.of(accountStatement));
        }
 
        public void whenAccountStatementServiceIsCalledToGetAccountStatementById() {
            actualAccountStatement = accountStatementService.getAccountStatementById(ACCOUNT_STATEMENT_ID);
        }
 
        public void thenVerifyTheAccountStatementById() {
            assertEquals(ACCOUNT_STATEMENT_ID, actualAccountStatement.getId());
        }
 
        public void thenVerifyThatFindByIdCalledOnce() {
            verify(accountStatementsRepository, times(1)).findById(ACCOUNT_STATEMENT_ID);
        }
 
        public void givenAccountStatementRepositoryIsMockedToFindByIdFailure() {
            when(accountStatementsRepository.findById(ACCOUNT_STATEMENT_ID)).thenReturn(Optional.empty());
        }
 
        public void thenVerifyGetAccountStatementByIdThrowException() {
            assertThrows(NoSuchElementException.class, () -> accountStatementService.getAccountStatementById(ACCOUNT_STATEMENT_ID));
        }
 
        public void givenAccountStatementRepositoryIsMockedToFindAll() {
            when(accountStatementsRepository.findAll()).thenReturn(List.of(accountStatement));
        }
 
        public void whenAccountStatementServiceIsCalledToGetAllAccountStatements() {
            actualAccountStatements = accountStatementService.getAllAccountStatements();
        }
 
        public void thenVerifyTheFirstAccountStatementById() {
            assertEquals(ACCOUNT_STATEMENT_ID, actualAccountStatements.get(0).getId());
        }
 
        public void thenVerifyTheSizeOfAccountStatementList() {
            assertEquals(1, actualAccountStatements.size());
        }
 
        public void thenVerifyThatFindAllCalledOnce() {
            verify(accountStatementsRepository, times(1)).findAll();
        }
 
        public void whenAccountStatementServiceIsCalledToDeleteAllAccountStatements() {
            actualAccountStatement = accountStatementService.deleteAccountStatementById(ACCOUNT_STATEMENT_ID);
        }
 
        public void thenVerifyByWalletIdOfAccountStatement() {
            assertEquals(WALLET_ACCOUNT_ID, actualAccountStatement.getWalletAccountId());
        }
 
        public void thenVerifyByTheSoloMemberGUIDOfMemberTransaction() {
            assertEquals(SOLO_MEMBER_GUID, actualAccountStatement.getMemberTransactions().get(0).getSoloMemberGUID());
        }
 
        public void thenVerifyThatDeleteMethodOfRepositoryCalledOnce() {
            verify(accountStatementsRepository, times(1)).delete(any(AccountStatements.class));
        }

        public void givenMemberTransactionRepositoryIsMockedToFindAllByTransactionDateAfter() {
            when(
                    memberTransactionsRepository.findAllByWalletAccountIdAndTransactionDateBetween(
                            WALLET_ACCOUNT_ID,
                            START_DATE_TIME,
                            END_DATE_TIME
                    )
            ).thenReturn(List.of(memberTransaction));
        }

        public void givenAccountStatementRepositoryIsMockedToSave() {
            when(accountStatementsRepository.save(any(AccountStatements.class))).thenReturn(accountStatement);
        }

        public void whenAccountStatementServiceIsCalledToCreateAccountStatement() {
            actualAccountStatement = accountStatementService.createAccountStatement(WALLET_ACCOUNT_ID, START_TIME, END_TIME);
        }

        public void thenVerifyMemberTransactionRepositoryIsCalledToFindAllByTransactionDateAfter() {
            verify(
                    memberTransactionsRepository,
                    times(1)
            ).findAllByWalletAccountIdAndTransactionDateBetween(WALLET_ACCOUNT_ID, START_DATE_TIME, END_DATE_TIME);
        }

        public void thenVerifyThatAccountStatementRepositoryIsCalledOnce() {
            verify(accountStatementsRepository, times(1)).save(any(AccountStatements.class));
        }

        public void thenVerifyIllegalArgumentExceptionIsThrownByCreateAccountStatement() {
            assertThrows(IllegalArgumentException.class,
                    () -> accountStatementService.createAccountStatement(WALLET_ACCOUNT_ID, null, null)
            );
        }

        public void givenAccountStatementRepositoryIsMockedToFindByWalletId() {
            when(accountStatementsRepository.findAllByWalletAccountId(WALLET_ACCOUNT_ID)).thenReturn(List.of(accountStatement));
        }

        public void whenAccountStatementServiceIsCalledToGetAccountStatementByWalletId() {
            actualAccountStatements=accountStatementService.getAllAccountStatementsByWalletId(WALLET_ACCOUNT_ID);
        }

        public void thenVerifyThatFindAllByWalletIdCalledOnce() {
            verify(accountStatementsRepository, times(1)).findAllByWalletAccountId(any(UUID.class));
        }

        public void thenVerifyThatAccountStatementHasSameWalletId() {
            assertEquals(WALLET_ACCOUNT_ID,actualAccountStatements.get(0).getWalletAccountId());
        }
    }
}