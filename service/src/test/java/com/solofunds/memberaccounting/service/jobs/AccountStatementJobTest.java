package com.solofunds.memberaccounting.service.jobs;

import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import com.solofunds.memberaccounting.service.repositories.StartingBalanceRepository;
import com.solofunds.memberaccounting.service.repositories.WalletAccountRepository;
import com.solofunds.memberaccounting.service.service.AccountStatementService;
import com.solofunds.memberaccounting.service.service.BalanceService;
import org.jobrunr.scheduling.JobScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountStatementJobTest {

    private AccountStatementJob accountStatementJob;

    private JobScheduler jobScheduler;

    private StartingBalanceRepository balanceRepository;

    private WalletAccountRepository walletAccountRepository;

    private AccountStatementService accountStatementService;

    private BalanceService balanceService;

    @BeforeEach
    public void setup() {
        jobScheduler = mock(JobScheduler.class);
        balanceRepository = mock(StartingBalanceRepository.class);
        walletAccountRepository = mock(WalletAccountRepository.class);
        accountStatementService = mock(AccountStatementService.class);
        balanceService = mock(BalanceService.class);

        accountStatementJob = new AccountStatementJob(
                jobScheduler,
                balanceRepository,
                walletAccountRepository,
                accountStatementService,
                balanceService
        );
    }

    @Test
    void testCallCreateAccountStatement() throws Exception {
        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

        YearMonth currentYearMonth = YearMonth.from(LocalDateTime.now());
        LocalDateTime startDate = currentYearMonth.minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime endDate = currentYearMonth.atDay(1).atStartOfDay();

        List<WalletAccount> walletAccounts = new ArrayList<>();
        WalletAccount walletAccount1 = new WalletAccount();
        walletAccount1.setId(WALLET_ACCOUNT_ID);
        walletAccounts.add(walletAccount1);

        when(balanceService.calculateStartingBalance(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(BigInteger.ONE);

        when(walletAccountRepository.findAll()).thenReturn(walletAccounts);

        AccountStatementDto expectedAccountStatementDTO = new AccountStatementDto();

        when(accountStatementService.createAccountStatement(any(UUID.class), any(), any()))
                .thenReturn(expectedAccountStatementDTO);

        // Call the method to be tested
        accountStatementJob.callCreateAccountStatement();

        verify(balanceService).calculateStartingBalance(startDate, endDate);
        verify(walletAccountRepository).findAll();
        verify(accountStatementService).createAccountStatement(any(UUID.class), eq(startDate.toLocalDate()), eq(endDate.toLocalDate().minusDays(1)));
    }
}