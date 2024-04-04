package com.solofunds.memberaccounting.service.repositories;

import com.solofunds.memberaccounting.service.config.DatabaseSetupExtension;
import com.solofunds.memberaccounting.service.entities.MemberTransactions;
import com.solofunds.memberaccounting.service.entities.StartingBalance;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import com.solofunds.memberaccounting.service.enums.Category;
import com.solofunds.memberaccounting.service.enums.Direction;
import com.solofunds.memberaccounting.service.enums.Status;
import com.solofunds.memberaccounting.service.enums.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@ExtendWith(DatabaseSetupExtension.class)
public class StartingBalanceRepositoryTest {

    @Autowired
    StartingBalanceRepository startingBalanceRepository;

    @Autowired
    AccountStatementsRepository accountStatementsRepository;

    @Autowired
    WalletAccountRepository walletAccountRepository;

    @Autowired
    MemberTransactionsRepository memberTransactionsRepository;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        fixture.givenCleanDb();
    }

    @Test
    public void testSingleAvailableTx() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenAvailableAmountTx(walletAccount, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        fixture.calculateStartingBalance(startTime, endTime);
        fixture.assertExpectedBalance(walletAccount);
        fixture.assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger.ONE);
        fixture.assertExpectedStartingBalanceRecords(walletAccount, 1);
    }

    @Test
    public void testSinglePendingTx() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenPendingAmountTx(walletAccount, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        fixture.calculateStartingBalance(startTime, endTime);
        fixture.assertExpectedBalance(walletAccount);
        fixture.assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger.ONE);
        fixture.assertExpectedStartingBalanceRecords(walletAccount, 1);
    }

    @Test
    public void testSingleSoloCreditTx() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenSoloCreditAmountTx(walletAccount, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        fixture.calculateStartingBalance(startTime, endTime);
        fixture.assertExpectedBalance(walletAccount);
        fixture.assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger.ONE);
        fixture.assertExpectedStartingBalanceRecords(walletAccount, 1);
    }

    @Test
    public void testMultipleTransactions() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenAvailableAmountTx(walletAccount, postingDate);
        fixture.givenAvailableAmountTx(walletAccount, postingDate);
        fixture.givenAvailableAmountTx(walletAccount, postingDate);
        fixture.givenAvailableAmountTxDebit(walletAccount, postingDate);
        fixture.givenPendingAmountTx(walletAccount, postingDate);
        fixture.givenPendingAmountTx(walletAccount, postingDate);
        fixture.givenPendingAmountTx(walletAccount, postingDate);
        fixture.givenPendingAmountTxDebit(walletAccount, postingDate);
        fixture.givenSoloCreditAmountTx(walletAccount, postingDate);
        fixture.givenSoloCreditAmountTx(walletAccount, postingDate);
        fixture.givenSoloCreditAmountTx(walletAccount, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        fixture.calculateStartingBalance(startTime, endTime);
        fixture.assertExpectedBalance(walletAccount);
        fixture.assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger.ONE);
        fixture.assertExpectedStartingBalanceRecords(walletAccount, 1);
    }

    @Test
    public void testIncludePreviouslyCalculatedStartingBalance() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenAvailableAmountTx(walletAccount, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        fixture.calculateStartingBalance(startTime, endTime);

        // Add another transaction after the endTime date of the previous StartingBalance calculation
        fixture.givenAvailableAmountTx(walletAccount, postingDate.plusDays(1));
        LocalDateTime endTime2 = LocalDateTime.now().plusDays(2);
        // Use the endTime of the previous run as the startTime for the next run
        fixture.calculateStartingBalance(endTime, endTime2);
        // Confirm that the balance includes the previously calculated StartingBalance + the new transaction
        fixture.assertExpectedBalance(walletAccount);
        fixture.assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger.ONE);
        fixture.assertExpectedStartingBalanceRecords(walletAccount, 2);
    }

    @Test
    public void testIncludePreviouslyCalculatedPendingBalance() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenPendingAmountTx(walletAccount, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        fixture.calculateStartingBalance(startTime, endTime);

        // Add another transaction after the endTime date of the previous StartingBalance calculation
        fixture.givenPendingAmountTx(walletAccount, postingDate.plusDays(1));
        LocalDateTime endTime2 = LocalDateTime.now().plusDays(2);
        // Use the endTime of the previous run as the startTime for the next run
        fixture.calculateStartingBalance(endTime, endTime2);
        // Confirm that the balance includes the previously calculated StartingBalance + the new transaction
        fixture.assertExpectedBalance(walletAccount);
        fixture.assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger.ONE);
        fixture.assertExpectedStartingBalanceRecords(walletAccount, 2);
    }

    @Test
    public void testIncludePreviouslyCalculatedSoloCreditBalance() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenPendingAmountTx(walletAccount, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        fixture.calculateStartingBalance(startTime, endTime);

        // Add another transaction after the endTime date of the previous StartingBalance calculation
        fixture.givenSoloCreditAmountTx(walletAccount, postingDate.plusDays(1));
        LocalDateTime endTime2 = LocalDateTime.now().plusDays(2);
        // Use the endTime of the previous run as the startTime for the next run
        fixture.calculateStartingBalance(endTime, endTime2);
        // Confirm that the balance includes the previously calculated StartingBalance + the new transaction
        fixture.assertExpectedBalance(walletAccount);
        fixture.assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger.ONE);
        fixture.assertExpectedStartingBalanceRecords(walletAccount, 2);
    }

    @Test
    public void testMultipleWalletAccounts() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        WalletAccount walletAccount2 = fixture.givenWalletAccount();

        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenPendingAmountTx(walletAccount, postingDate);
        fixture.givenPendingAmountTx(walletAccount2, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        fixture.calculateStartingBalance(startTime, endTime);

        // Add another transaction after the endTime date of the previous StartingBalance calculation
        fixture.givenSoloCreditAmountTx(walletAccount, postingDate.plusDays(1));
        fixture.givenSoloCreditAmountTx(walletAccount2, postingDate.plusDays(1));
        LocalDateTime endTime2 = LocalDateTime.now().plusDays(2);
        // Use the endTime of the previous run as the startTime for the next run
        fixture.calculateStartingBalance(endTime, endTime2);
        // Confirm that the balance includes the previously calculated StartingBalance + the new transaction
        fixture.assertExpectedBalance(walletAccount);
        fixture.assertExpectedBalance(walletAccount2);
        fixture.assertExpectedStartingBalanceRecords(walletAccount, 2);
        fixture.assertExpectedStartingBalanceRecords(walletAccount2, 2);
        fixture.assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger.TWO);
    }

    @Test
    public void testStartingBalanceRecordCreatedIfNoMemberTransactionsForGivenTimePeriod() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        WalletAccount walletAccount2 = fixture.givenWalletAccount();

        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenPendingAmountTx(walletAccount, postingDate);
        fixture.givenPendingAmountTx(walletAccount2, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        fixture.calculateStartingBalance(startTime, endTime);

        // Add another transaction after the endTime date of the previous StartingBalance calculation
        fixture.givenSoloCreditAmountTx(walletAccount, postingDate.plusDays(1));
        LocalDateTime endTime2 = LocalDateTime.now().plusDays(2);
        // Use the endTime of the previous run as the startTime for the next run
        fixture.calculateStartingBalance(endTime, endTime2);

        // Run the function again without creating a new MemberTransactions record
        LocalDateTime endTime3 = LocalDateTime.now().plusDays(3);
        fixture.calculateStartingBalance(endTime2, endTime3);
        // Confirm that the balance includes the previously calculated StartingBalance + the new transaction
        fixture.assertExpectedBalance(walletAccount);
        fixture.assertExpectedBalance(walletAccount2);
        fixture.assertExpectedStartingBalanceRecords(walletAccount, 3);
        fixture.assertExpectedStartingBalanceRecords(walletAccount2, 3);
        fixture.assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger.TWO);
    }

    @Test
    public void testEndDateEdgeCase() {
        WalletAccount walletAccount = fixture.givenWalletAccount();
        LocalDateTime postingDate = OffsetDateTime.now().toLocalDateTime();
        fixture.givenPendingAmountTx(walletAccount, postingDate);
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = postingDate;
        fixture.calculateStartingBalance(startTime, endTime);

        // Because postingDate == endTime, the transaction should not be included in the starting balance record
        fixture.assertZeroBalance(walletAccount);

        fixture.calculateStartingBalance(endTime, endTime.plusDays(1));
        fixture.assertExpectedBalance(walletAccount);
    }


    @Nested
    private class Fixture {
        class Balances {
            BigDecimal availableAmount = BigDecimal.ZERO;
            BigDecimal pendingAmount = BigDecimal.ZERO;
            BigDecimal soloCreditAmount = BigDecimal.ZERO;
        }
        Map<UUID, Balances> walletBalances = new HashMap<>();
        BigDecimal ninetyNineAmount = BigDecimal.valueOf(99.99);
        BigInteger numberOfRecordsAdded;

        void givenCleanDb() {
            memberTransactionsRepository.deleteAll();
            startingBalanceRepository.deleteAll();
            accountStatementsRepository.deleteAll();
            walletAccountRepository.deleteAll();
        }

        WalletAccount givenWalletAccount() {
            WalletAccount newWalletAccount = new WalletAccount();
            WalletAccount walletAccountRes = walletAccountRepository.save(newWalletAccount);
            walletBalances.put(walletAccountRes.getId(), new Balances());
            return walletAccountRes;
        }

        void givenAvailableAmountTx(WalletAccount walletAccount, LocalDateTime postingDate) {
            // Update WalletBalances map
            Balances walletBalance = walletBalances.get(walletAccount.getId());
            walletBalance.availableAmount = walletBalance.availableAmount.add(ninetyNineAmount);
            walletBalances.put(walletAccount.getId(), walletBalance);

            // Save MemberTransactions record
            MemberTransactions memberTransactions = new MemberTransactions();
            memberTransactions.setWalletAccountId(walletAccount.getId());
            memberTransactions.setPostingDate(postingDate);
            memberTransactions.setCurrencyCode("USD");
            memberTransactions.setCurrencyExponent("2");
            memberTransactions.setAmount(ninetyNineAmount);
            memberTransactions.setType(Type.CREDIT);
            memberTransactions.setCategory(Category.CREDIT);
            memberTransactions.setDirection(Direction.CREDIT);
            memberTransactions.setStatus(Status.POSTED);
            memberTransactionsRepository.save(memberTransactions);
        }

        void givenAvailableAmountTxDebit(WalletAccount walletAccount, LocalDateTime postingDate) {
            // Update WalletBalances map
            Balances walletBalance = walletBalances.get(walletAccount.getId());
            walletBalance.availableAmount = walletBalance.availableAmount.subtract(ninetyNineAmount);
            walletBalances.put(walletAccount.getId(), walletBalance);

            // Save MemberTransactions record
            MemberTransactions memberTransactions = new MemberTransactions();
            memberTransactions.setWalletAccountId(walletAccount.getId());
            memberTransactions.setPostingDate(postingDate);
            memberTransactions.setCurrencyCode("USD");
            memberTransactions.setCurrencyExponent("2");
            memberTransactions.setAmount(ninetyNineAmount);
            memberTransactions.setType(Type.DEBIT);
            memberTransactions.setCategory(Category.DEBIT);
            memberTransactions.setDirection(Direction.DEBIT);
            memberTransactions.setStatus(Status.POSTED);
            memberTransactionsRepository.save(memberTransactions);
        }

        void givenPendingAmountTx(WalletAccount walletAccount, LocalDateTime postingDate) {
            // Update WalletBalances map
            Balances walletBalance = walletBalances.get(walletAccount.getId());
            walletBalance.pendingAmount = walletBalance.pendingAmount.add(ninetyNineAmount);
            walletBalances.put(walletAccount.getId(), walletBalance);

            MemberTransactions memberTransactions = new MemberTransactions();
            memberTransactions.setWalletAccountId(walletAccount.getId());
            memberTransactions.setPostingDate(postingDate);
            memberTransactions.setCurrencyCode("USD");
            memberTransactions.setCurrencyExponent("2");
            memberTransactions.setAmount(ninetyNineAmount);
            memberTransactions.setType(Type.CREDIT);
            memberTransactions.setCategory(Category.CREDIT);
            memberTransactions.setDirection(Direction.CREDIT);
            memberTransactions.setStatus(Status.PENDING);
            memberTransactionsRepository.save(memberTransactions);
        }

        void givenPendingAmountTxDebit(WalletAccount walletAccount, LocalDateTime postingDate) {
            // Update WalletBalances map
            Balances walletBalance = walletBalances.get(walletAccount.getId());
            walletBalance.pendingAmount = walletBalance.pendingAmount.subtract(ninetyNineAmount);
            walletBalances.put(walletAccount.getId(), walletBalance);

            MemberTransactions memberTransactions = new MemberTransactions();
            memberTransactions.setWalletAccountId(walletAccount.getId());
            memberTransactions.setPostingDate(postingDate);
            memberTransactions.setCurrencyCode("USD");
            memberTransactions.setCurrencyExponent("2");
            memberTransactions.setAmount(ninetyNineAmount);
            memberTransactions.setType(Type.DEBIT);
            memberTransactions.setCategory(Category.DEBIT);
            memberTransactions.setDirection(Direction.DEBIT);
            memberTransactions.setStatus(Status.PENDING);
            memberTransactionsRepository.save(memberTransactions);
        }

        void givenSoloCreditAmountTx(WalletAccount walletAccount, LocalDateTime postingDate) {
            // Update WalletBalances map
            Balances walletBalance = walletBalances.get(walletAccount.getId());
            walletBalance.soloCreditAmount = walletBalance.soloCreditAmount.add(ninetyNineAmount);
            walletBalances.put(walletAccount.getId(), walletBalance);

            MemberTransactions memberTransactions = new MemberTransactions();
            memberTransactions.setWalletAccountId(walletAccount.getId());
            memberTransactions.setPostingDate(postingDate);
            memberTransactions.setCurrencyCode("USD");
            memberTransactions.setCurrencyExponent("2");
            memberTransactions.setAmount(ninetyNineAmount);
            memberTransactions.setType(Type.SOLO_CREDIT);
            memberTransactions.setCategory(Category.SOLO_CREDIT);
            memberTransactions.setDirection(Direction.CREDIT);
            memberTransactions.setStatus(Status.POSTED);
            memberTransactionsRepository.save(memberTransactions);
        }

        void calculateStartingBalance(LocalDateTime startTime, LocalDateTime endTime) {
            numberOfRecordsAdded = startingBalanceRepository.triggerCalculateStartingBalanceFunction(startTime, endTime);
        }

        void assertExpectedBalance(WalletAccount walletAccount) {
            List<StartingBalance> startingBalanceList = startingBalanceRepository.findByWalletAccountId(walletAccount.getId());
            startingBalanceList.sort((sb1, sb2) -> sb2.getPostingDate().compareTo(sb1.getPostingDate())); // Sort List by postingDate descending
            Assertions.assertEquals(walletAccount.getId(), startingBalanceList.get(0).getWalletAccountId());
            Balances walletBalance = walletBalances.get(walletAccount.getId());
            Assertions.assertEquals(walletBalance.availableAmount, startingBalanceList.get(0).getAvailableAmount());
            Assertions.assertEquals(walletBalance.pendingAmount, startingBalanceList.get(0).getPendingAmount());
            Assertions.assertEquals(walletBalance.soloCreditAmount, startingBalanceList.get(0).getSoloCreditAmount());
        }

        void assertZeroBalance(WalletAccount walletAccount) {
            List<StartingBalance> startingBalanceList = startingBalanceRepository.findByWalletAccountId(walletAccount.getId());
            startingBalanceList.sort((sb1, sb2) -> sb2.getPostingDate().compareTo(sb1.getPostingDate())); // Sort List by postingDate descending
            Assertions.assertEquals(walletAccount.getId(), startingBalanceList.get(0).getWalletAccountId());
            Assertions.assertEquals(BigDecimal.ZERO, startingBalanceList.get(0).getAvailableAmount());
            Assertions.assertEquals(BigDecimal.ZERO, startingBalanceList.get(0).getPendingAmount());
            Assertions.assertEquals(BigDecimal.ZERO, startingBalanceList.get(0).getSoloCreditAmount());
        }

        void assertExpectedStartingBalanceRecords(WalletAccount walletAccount, Integer expectedNumberOfRecords) {
            List<StartingBalance> startingBalanceList = startingBalanceRepository.findByWalletAccountId(walletAccount.getId());
            Assertions.assertEquals(expectedNumberOfRecords, startingBalanceList.size());
        }

        void assertExpectedNumberOfRecordsAddedByLastFunctionExecution(BigInteger expectedNumberOfRecords) {
            Assertions.assertEquals(expectedNumberOfRecords, numberOfRecordsAdded);
        }

    }
}

