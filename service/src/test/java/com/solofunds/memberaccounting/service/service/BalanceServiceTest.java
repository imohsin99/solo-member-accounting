package com.solofunds.memberaccounting.service.service;

import com.solofunds.memberaccounting.model.BalanceDto;
import com.solofunds.memberaccounting.service.entities.MemberTransactions;
import com.solofunds.memberaccounting.service.entities.StartingBalance;
import com.solofunds.memberaccounting.service.enums.Category;
import com.solofunds.memberaccounting.service.enums.Direction;
import com.solofunds.memberaccounting.service.enums.Status;
import com.solofunds.memberaccounting.service.enums.Type;
import com.solofunds.memberaccounting.service.repositories.MemberTransactionsRepository;
import com.solofunds.memberaccounting.service.repositories.StartingBalanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BalanceServiceTest {

    @Mock
    StartingBalanceRepository mockStartingBalanceRepo;

    @Mock
    MemberTransactionsRepository mockMemberTransactionsRepo;

    @InjectMocks
    BalanceService balanceService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getStartingBalanceSuccessful() throws Exception {
        // Save an initial starting balance
        UUID walletAccountId = UUID.randomUUID();
        StartingBalance initialStartingBalance = StartingBalance.builder()
                .walletAccountId(walletAccountId)
                .availableAmount(BigDecimal.valueOf(1))
                .pendingAmount(BigDecimal.valueOf(2))
                .soloCreditAmount(BigDecimal.valueOf(3))
                .currencyCode("USD")
                .currencyExponent("2")
                .postingDate(LocalDateTime.now().minusDays(1))
                .build();
        List<StartingBalance> startingBalances = new ArrayList<>();
        startingBalances.add(initialStartingBalance);

        List<MemberTransactions> memberTransactionsList = new ArrayList<>();

        // Save an initial member transaction (available)
        MemberTransactions initialMemberTransactions = MemberTransactions.builder()
                .soloMemberGUID(UUID.randomUUID())
                .loanId(UUID.randomUUID())
                .ledgerTransactionId(UUID.randomUUID())
                .paymentOrderId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(10.01))
                .currencyCode("USD")
                .currencyExponent("2")
                .type(Type.CREDIT)
                .category(Category.CREDIT)
                .direction(Direction.CREDIT)
                .description("test")
                .detailText("test")
                .status(Status.POSTED)
                .transactionDate(LocalDateTime.now())
                .postingDate(LocalDateTime.now())
                .paymentOrders(new HashSet<>())
                .accountStatements(new HashSet<>())
                .build();
        memberTransactionsList.add(initialMemberTransactions);

        // Save an initial member transaction (pending)
        MemberTransactions initialMemberTransactionsPending = MemberTransactions.builder()
                .soloMemberGUID(UUID.randomUUID())
                .loanId(UUID.randomUUID())
                .ledgerTransactionId(UUID.randomUUID())
                .paymentOrderId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(2.01))
                .currencyCode("USD")
                .currencyExponent("2")
                .type(Type.CREDIT)
                .category(Category.CREDIT)
                .direction(Direction.CREDIT)
                .description("test")
                .detailText("test")
                .status(Status.PENDING)
                .transactionDate(LocalDateTime.now())
                .postingDate(LocalDateTime.now())
                .paymentOrders(new HashSet<>())
                .accountStatements(new HashSet<>())
                .build();
        memberTransactionsList.add(initialMemberTransactionsPending);

        // Save an initial member transaction (solo credit)
        MemberTransactions initialMemberTransactionsSoloCredit = MemberTransactions.builder()
                .soloMemberGUID(UUID.randomUUID())
                .loanId(UUID.randomUUID())
                .ledgerTransactionId(UUID.randomUUID())
                .paymentOrderId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(3.01))
                .currencyCode("USD")
                .currencyExponent("2")
                .type(Type.SOLO_CREDIT)
                .category(Category.CREDIT)
                .direction(Direction.CREDIT)
                .description("test")
                .detailText("test")
                .status(Status.POSTED)
                .transactionDate(LocalDateTime.now())
                .postingDate(LocalDateTime.now())
                .paymentOrders(new HashSet<>())
                .accountStatements(new HashSet<>())
                .build();
        memberTransactionsList.add(initialMemberTransactionsSoloCredit);

        when(mockStartingBalanceRepo.findByWalletAccountId(any(UUID.class))).thenReturn(startingBalances);
        when(mockMemberTransactionsRepo.findByWalletAccountIdAndPostingDateAfter(any(UUID.class), any(LocalDateTime.class))).thenReturn(memberTransactionsList);

        BalanceDto res = balanceService.getBalanceByWalletAccountId(walletAccountId);

        // Initial starting (1) + member transaction amount (10.01)
        Assertions.assertEquals(BigDecimal.valueOf(11.01), res.getAvailableBalance()); // includes Solo Credit

        // Initial starting (2) + member transaction amount (2.01)
        Assertions.assertEquals(BigDecimal.valueOf(4.01), res.getPendingBalance());

        // Initial starting (3) + member transaction amount (3.01)
        Assertions.assertEquals(BigDecimal.valueOf(6.01), res.getSoloCreditBalance());
    }

    @Test
    public void getMostRecentStartingBalance() throws Exception {
        // Save an initial starting balance
        UUID walletAccountId = UUID.randomUUID();
        StartingBalance initialStartingBalance = StartingBalance.builder()
                .walletAccountId(walletAccountId)
                .availableAmount(BigDecimal.valueOf(1))
                .pendingAmount(BigDecimal.valueOf(2))
                .soloCreditAmount(BigDecimal.valueOf(3))
                .currencyCode("USD")
                .currencyExponent("2")
                .postingDate(LocalDateTime.now().minusDays(1))
                .build();
        StartingBalance initialStartingBalance2 = StartingBalance.builder()
                .walletAccountId(walletAccountId)
                .availableAmount(BigDecimal.valueOf(11))
                .pendingAmount(BigDecimal.valueOf(222))
                .soloCreditAmount(BigDecimal.valueOf(33))
                .currencyCode("USD")
                .currencyExponent("2")
                .postingDate(LocalDateTime.now().minusDays(2))
                .build();
        List<StartingBalance> startingBalances = new ArrayList<>();

        // Sandwiching the most recent balance in the List to ensure that sorting works properly in the service function
        startingBalances.add(initialStartingBalance2);
        startingBalances.add(initialStartingBalance);
        startingBalances.add(initialStartingBalance2);

        // Save an initial member transaction
        MemberTransactions initialMemberTransactions = MemberTransactions.builder()
                .soloMemberGUID(UUID.randomUUID())
                .loanId(UUID.randomUUID())
                .ledgerTransactionId(UUID.randomUUID())
                .paymentOrderId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(10.01))
                .currencyCode("USD")
                .currencyExponent("2")
                .type(Type.CREDIT)
                .category(Category.CREDIT)
                .direction(Direction.CREDIT)
                .description("test")
                .detailText("test")
                .status(Status.POSTED)
                .transactionDate(LocalDateTime.now())
                .postingDate(LocalDateTime.now())
                .paymentOrders(new HashSet<>())
                .accountStatements(new HashSet<>())
                .build();
        List<MemberTransactions> memberTransactionsList = new ArrayList<>();
        memberTransactionsList.add(initialMemberTransactions);


        when(mockStartingBalanceRepo.findByWalletAccountId(any(UUID.class))).thenReturn(startingBalances);
        when(mockMemberTransactionsRepo.findByWalletAccountIdAndPostingDateAfter(any(UUID.class), any(LocalDateTime.class))).thenReturn(memberTransactionsList);

        BalanceDto res = balanceService.getBalanceByWalletAccountId(walletAccountId);

        // Member transaction amount (10) + initial starting balance available amount (1)
        Assertions.assertEquals(BigDecimal.valueOf(11.01), res.getAvailableBalance());
    }

    @Test
    public void getStartingBalanceSuccessfulMultipleCurrenciesError() {
        // Save an initial starting balance
        UUID walletAccountId = UUID.randomUUID();
        StartingBalance initialStartingBalance = StartingBalance.builder()
                .walletAccountId(walletAccountId)
                .availableAmount(BigDecimal.valueOf(1))
                .pendingAmount(BigDecimal.valueOf(2))
                .soloCreditAmount(BigDecimal.valueOf(3))
                .currencyCode("USD")
                .currencyExponent("2")
                .postingDate(LocalDateTime.now().minusDays(1))
                .build();
        List<StartingBalance> startingBalances = new ArrayList<>();
        startingBalances.add(initialStartingBalance);

        List<MemberTransactions> memberTransactionsList = new ArrayList<>();

        // Save an initial member transaction (available)
        MemberTransactions initialMemberTransactionsUSD = MemberTransactions.builder()
                .soloMemberGUID(UUID.randomUUID())
                .loanId(UUID.randomUUID())
                .ledgerTransactionId(UUID.randomUUID())
                .paymentOrderId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(10.01))
                .currencyCode("USD")
                .currencyExponent("2")
                .type(Type.CREDIT)
                .category(Category.CREDIT)
                .direction(Direction.CREDIT)
                .description("test")
                .detailText("test")
                .status(Status.POSTED)
                .transactionDate(LocalDateTime.now())
                .postingDate(LocalDateTime.now())
                .paymentOrders(new HashSet<>())
                .accountStatements(new HashSet<>())
                .walletAccountId(walletAccountId)
                .build();
        memberTransactionsList.add(initialMemberTransactionsUSD);

        // Save an initial member transaction (pending)
        MemberTransactions initialMemberTransactionsCAD = MemberTransactions.builder()
                .soloMemberGUID(UUID.randomUUID())
                .loanId(UUID.randomUUID())
                .ledgerTransactionId(UUID.randomUUID())
                .paymentOrderId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(2.01))
                .currencyCode("CAD")
                .currencyExponent("2")
                .type(Type.CREDIT)
                .category(Category.CREDIT)
                .direction(Direction.CREDIT)
                .description("test")
                .detailText("test")
                .status(Status.PENDING)
                .transactionDate(LocalDateTime.now())
                .postingDate(LocalDateTime.now())
                .paymentOrders(new HashSet<>())
                .accountStatements(new HashSet<>())
                .walletAccountId(walletAccountId)
                .build();
        memberTransactionsList.add(initialMemberTransactionsCAD);

        when(mockStartingBalanceRepo.findByWalletAccountId(any(UUID.class))).thenReturn(startingBalances);
        when(mockMemberTransactionsRepo.findByWalletAccountIdAndPostingDateAfter(any(UUID.class), any(LocalDateTime.class))).thenReturn(memberTransactionsList);

        Exception exception = Assertions.assertThrows(Exception.class, () -> balanceService.getBalanceByWalletAccountId(walletAccountId));
        Assertions.assertTrue(exception.getMessage().contains("walletAccountId " + walletAccountId + " contains MemberTransactions records with multiple currency codes"));
    }

    @Test
    public void memberTransactionsStartingBalanceCheckTest() {
        // Save an initial starting balance
        UUID walletAccountId = UUID.randomUUID();
        StartingBalance startingBalance = StartingBalance.builder()
                .walletAccountId(walletAccountId)
                .availableAmount(BigDecimal.valueOf(1))
                .pendingAmount(BigDecimal.valueOf(2))
                .soloCreditAmount(BigDecimal.valueOf(3))
                .currencyCode("USD")
                .currencyExponent("2")
                .postingDate(LocalDateTime.now().minusDays(1))
                .build();

        List<MemberTransactions> memberTransactionsList = new ArrayList<>();

        // Save an initial member transaction (available)
        MemberTransactions initialMemberTransactions = MemberTransactions.builder()
                .soloMemberGUID(UUID.randomUUID())
                .loanId(UUID.randomUUID())
                .ledgerTransactionId(UUID.randomUUID())
                .paymentOrderId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(10.01))
                .currencyCode("USD")
                .currencyExponent("2")
                .type(Type.CREDIT)
                .category(Category.CREDIT)
                .direction(Direction.CREDIT)
                .description("test")
                .detailText("test")
                .status(Status.POSTED)
                .transactionDate(LocalDateTime.now())
                .postingDate(LocalDateTime.now())
                .paymentOrders(new HashSet<>())
                .accountStatements(new HashSet<>())
                .build();
        memberTransactionsList.add(initialMemberTransactions);

        when(mockStartingBalanceRepo.findFirstByPostingDateBeforeOrderByPostingDateDesc(any(LocalDateTime.class))).thenReturn(startingBalance);

        // If we find MemberTransactions after the latest StartingBalance record but before the startTime of the function,
        // we throw an exception to prevent the calculate_starting_balance function from executing.
        when(mockMemberTransactionsRepo.findAllByPostingDateBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(memberTransactionsList);

        Exception exception = Assertions.assertThrows(Exception.class, () -> balanceService.memberTransactionsStartingBalanceCheck(LocalDateTime.now()));
        Assertions.assertTrue(exception.getMessage().contains("There are MemberTransactions between the nearest StartingBalance"));
    }

    @Test
    public void memberTransactionsStartingBalanceCheckSuccessfulTest() {
        // Save an initial starting balance
        UUID walletAccountId = UUID.randomUUID();
        StartingBalance startingBalance = StartingBalance.builder()
                .walletAccountId(walletAccountId)
                .availableAmount(BigDecimal.valueOf(1))
                .pendingAmount(BigDecimal.valueOf(2))
                .soloCreditAmount(BigDecimal.valueOf(3))
                .currencyCode("USD")
                .currencyExponent("2")
                .postingDate(LocalDateTime.now().minusDays(1))
                .build();

        when(mockStartingBalanceRepo.findFirstByPostingDateBeforeOrderByPostingDateDesc(any(LocalDateTime.class))).thenReturn(startingBalance);

        // No records found
        when(mockMemberTransactionsRepo.findAllByPostingDateBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        Assertions.assertDoesNotThrow(() -> balanceService.memberTransactionsStartingBalanceCheck(LocalDateTime.now()));
    }
}
