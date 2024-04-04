package com.solofunds.memberaccounting.service.service;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.solofunds.memberaccounting.model.*;
import com.solofunds.memberaccounting.service.entities.AccountStatements;
import com.solofunds.memberaccounting.service.entities.StartingBalance;
import com.solofunds.memberaccounting.service.mappers.AccountStatementMapper;
import com.solofunds.memberaccounting.service.repositories.LoanRepository;
import com.solofunds.memberaccounting.service.repositories.StartingBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountStatementPdfGeneratorTest {

    @Mock
    private AccountStatementService statementService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private FeeService feeService;

    @Mock
    private StartingBalanceRepository startingBalanceRepository;

    @Mock
    private AccountStatementMapper statementMapper;

    @InjectMocks
    private AccountStatementPdfGenerator pdfGenerator;

    private final UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

    AccountStatementPdfGeneratorTest() {
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void statementPdf() throws IOException {
        UUID accountStatementId = UUID.randomUUID();

        MemberDataDto memberDataDTO = createMockMemberDataDto();
        AccountStatementDto mockStatementDto = createMockStatementDto(accountStatementId);
        AccountStatements mockStatements = AccountStatementMapper.MAPPER.toAccountStatements(mockStatementDto);

        when(statementService.getAccountStatementById(accountStatementId)).thenReturn(mockStatementDto);
        when(statementMapper.toAccountStatements(any(AccountStatementDto.class))).thenReturn(mockStatements);
        when(loanRepository.findAllByBorrowerWalletAccountId(any())).thenReturn(new ArrayList<>());

        List<FeeDto> mockFeeDtos = createMockFeeDtos();
        when(feeService.getAllFeesForLoanId(any())).thenReturn(mockFeeDtos);

        StartingBalance mockStartingBalance = createMockStartingBalance();
        when(startingBalanceRepository.findByWalletAccountId(any())).thenReturn(Collections.singletonList(mockStartingBalance));

        ByteArrayInputStream pdfData = pdfGenerator.statementPdf(accountStatementId, memberDataDTO);

        assertThat(pdfData).isNotNull();

        PdfReader pdfReader = new PdfReader(pdfData);
        String pdfText = PdfTextExtractor.getTextFromPage(pdfReader, 1);

        verify(statementService, times(1)).getAccountStatementById(accountStatementId);

        assertThat(pdfText)
                .contains(memberDataDTO.getSoloMemberName())
                .contains("Previous Balance. $" + mockStartingBalance.getAvailableAmount())
                .contains("Payments and Other Credits  + " + mockStatementDto.getMemberTransactions().get(0).getAmount())
                .contains("Purchases and Adjustments  - " + mockStatementDto.getMemberTransactions().get(1).getAmount())
                .contains(mockStatementDto.getMemberTransactions().get(0).getId().toString().substring(mockStatementDto.getMemberTransactions().get(0).getId().toString().length() - 4));

        pdfReader.close();
    }

    private StartingBalance createMockStartingBalance() {
        StartingBalance startingBalance = new StartingBalance();
        startingBalance.setId(UUID.randomUUID());
        startingBalance.setWalletAccountId(UUID.randomUUID());
        startingBalance.setPostingDate(LocalDateTime.now().minusMonths(1));
        startingBalance.setAvailableAmount(new BigDecimal(1000));

        return startingBalance;
    }

    private List<FeeDto> createMockFeeDtos() {
        List<FeeDto> feeDtos = new ArrayList<>();

        FeeDto feeDto1 = new FeeDto();
        feeDto1.setId(UUID.randomUUID());
        feeDto1.setAmount(new BigDecimal(50));
        feeDto1.setPostedAt(OffsetDateTime.now().minusDays(15));

        feeDtos.add(feeDto1);

        return feeDtos;
    }

    private List<MemberTransactionDto> createMockMemberTransactions() {
        List<MemberTransactionDto> mockTransactions = new ArrayList<>();

        MemberTransactionDto transaction1 = new MemberTransactionDto();
        transaction1.setId(UUID.randomUUID());
        transaction1.setAmount(new BigDecimal(100));
        transaction1.setDirection(Direction.CREDIT);
        transaction1.setTransactionDate(OffsetDateTime.now().minusDays(10));
        transaction1.setPostingDate(OffsetDateTime.now().minusDays(10));
        transaction1.setWalletAccountId(WALLET_ACCOUNT_ID);

        mockTransactions.add(transaction1);

        MemberTransactionDto transaction2 = new MemberTransactionDto();
        transaction2.setId(UUID.randomUUID());
        transaction2.setAmount(new BigDecimal(50));
        transaction2.setDirection(Direction.DEBIT);
        transaction2.setTransactionDate(OffsetDateTime.now().minusDays(20));
        transaction2.setPostingDate(OffsetDateTime.now().minusDays(20));
        transaction2.setWalletAccountId(WALLET_ACCOUNT_ID);

        mockTransactions.add(transaction2);

        return mockTransactions;
    }

    private MemberDataDto createMockMemberDataDto() {
        MemberDataDto mockMemberDataDto = new MemberDataDto();
        mockMemberDataDto.setSoloMemberName("Test User");
        return mockMemberDataDto;
    }

    private AccountStatementDto createMockStatementDto(UUID accountStatementId) {
        AccountStatementDto mockStatementDto = new AccountStatementDto();
        mockStatementDto.setId(accountStatementId);
        mockStatementDto.setMemberTransactions(createMockMemberTransactions());
        mockStatementDto.setStartDate(OffsetDateTime.now().minusDays(30));
        mockStatementDto.setPostingDate(OffsetDateTime.now());
        mockStatementDto.setWalletAccountId(WALLET_ACCOUNT_ID);
        return mockStatementDto;
    }
}
