package com.solofunds.memberaccounting.service.service.impl;

import com.solofunds.memberaccounting.model.CreateLoanDto;
import com.solofunds.memberaccounting.model.LoanDto;
import com.solofunds.memberaccounting.model.UpdateLoanDto;
import com.solofunds.memberaccounting.service.entities.FundingProposal;
import com.solofunds.memberaccounting.service.entities.Loan;
import com.solofunds.memberaccounting.service.entities.LoanRequest;
import com.solofunds.memberaccounting.service.entities.WalletAccount;
import com.solofunds.memberaccounting.service.enums.LoanStatus;
import com.solofunds.memberaccounting.service.exception.custom.ResourceNotFoundException;
import com.solofunds.memberaccounting.service.mappers.LoanMapper;
import com.solofunds.memberaccounting.service.repositories.FundingProposalRepository;
import com.solofunds.memberaccounting.service.repositories.LoanRepository;
import com.solofunds.memberaccounting.service.repositories.LoanRequestRepository;
import com.solofunds.memberaccounting.service.repositories.WalletAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoanServiceImplTest {

    @Mock
    private Loan loan;

    @Mock
    private LoanRequest loanRequest;

    @Mock
    private CreateLoanDto createLoanDto;

    @Mock
    private FundingProposal fundingProposal;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanMapper loanMapper;

    @Mock
    private LoanDto loanDTO;

    @Mock
    private UpdateLoanDto updateLoanDTO;

    @Mock
    private LoanRequestRepository loanRequestRepo;

    @Mock
    private FundingProposalRepository fundingProposalRepository;

    @Mock
    private WalletAccountRepository walletAccountRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void getLoanById() {
        fixture.givenFindByIdOfRepositoryIsCalled();
        fixture.givenLoanIsMappedToLoanDTO();

        fixture.whenGetLoanByIdOfServiceIsCalled();

        fixture.thenAssertExpectedLoanDTOIsNotNull();
    }

    @Test
    void getLoanByIdIsNotFound(){
        fixture.givenFindByIdOfRepositoryIsCalledAndReturnEmpty();

        fixture.givenResourceNotFoundExceptionThrowsWhenNoAccountStatementIsFound();
        fixture.thenAssertThatExceptionMessageMatchesExpectedMessage();
    }

    @Test
    void createLoan() {
        fixture.givenGetLoanRequestAndFundingProposalIdIsCalled();

        fixture.givenFindByIdOfLoanRequestRepositoryIsCalled();
        fixture.givenFindByIdOfFundingProposalRepositoryIsCalled();
        fixture.givenFindByIdOfWalletAccountsIsCalled();

        fixture.givenValidCrateLoanDtoIsMocked();
        fixture.givenLoanCreateRequestIsMappedToLoan();
        fixture.givenValidLoanIsMocked();
        fixture.givenSaveLoanOfRepositoryIsCalled();
        fixture.givenLoanIsMappedToLoanDTO();

        fixture.whenCreateLoanOfServiceIsCalled();

        fixture.thenAssertSavedLoanDtoIsNotNul();
    }

    @Test
    void getLoansByMemberId() {
        fixture.givenFindAllByBorrowerSoloMemberIdOrLenderSoloMemberIdIsCalled();

        fixture.whenGetLoansByMemberIdIsCalled();

        fixture.thenVerifyListSize();
        fixture.thenVerifyGetLoansByMemberIdIsCalled();
    }

    @Test
    void getLoansByMemberIdAndLoanStatus() {
        fixture.givenFindAllByStatusAndBorrowerSoloMemberIdOrStatusAndLenderSoloMemberIdCalled();

        fixture.whenGetLoansByLoanStatusAndMemberIdCalled();

        fixture.thenVerifyListSize();
        fixture.thenVerifyGetLoansByLoanStatusAndMemberIdCalled();
    }

    @Test
    void updateLoan() {
        fixture.givenFindByIdOfRepositoryIsCalled();
        fixture.givenUpdateLoanDtoIsMappedToLoan();
        fixture.givenSaveLoanOfRepositoryIsCalled();
        fixture.givenLoanIsMappedToLoanDTO();

        fixture.whenUpdateLoanOfServiceIsCalled();

        fixture.thenAssertSavedLoanDtoIsNotNul();
    }

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Nested
    public class Fixture{

        final UUID LOAN_ID = UUID.randomUUID();

        final UUID LOAN_REQUEST_ID = UUID.randomUUID();

        final UUID FUNDING_PROPOSAL_ID = UUID.randomUUID();

        final UUID SOLO_MEMBER_GUID = UUID.randomUUID();

        final UUID BORROWER_WALLET_ACCOUNT_ID = UUID.randomUUID();

        final UUID LENDER_WALLET_ACCOUNT_ID = UUID.randomUUID();

        LoanDto expectedLoanDTO;

        LoanDto savedLoan;

        List<LoanDto> loanDTOS;

        final String CURRENCY_CODE = "USD";

        final BigDecimal CURRENCY_EXPONENT = new BigDecimal(2);

        final LocalDateTime DUE_DATE = LocalDateTime.now().plusDays(30);

        final BigDecimal TIP_AMOUNT = new BigDecimal(20);

        final BigDecimal AMOUNT = new BigDecimal(500);

        final BigDecimal PRINCIPAL = new BigDecimal(1000);

        final BigDecimal SOLO_CREDITS_AMOUNT = new BigDecimal(1000);

        final BigDecimal DONATION_AMOUNT = new BigDecimal(20);

        ResourceNotFoundException resourceNotFoundException;

        void givenFindByIdOfRepositoryIsCalled(){
            when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        }

        void givenLoanIsMappedToLoanDTO(){
            when(loanMapper.toDTO(loan)).thenReturn(loanDTO);
        }

        void whenGetLoanByIdOfServiceIsCalled(){
            expectedLoanDTO = loanService.getLoanById(LOAN_ID);
        }

        void thenAssertExpectedLoanDTOIsNotNull(){
            assertNotNull(expectedLoanDTO);
        }

        void givenFindByIdOfRepositoryIsCalledAndReturnEmpty(){
            when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.empty());
        }

        void givenResourceNotFoundExceptionThrowsWhenNoAccountStatementIsFound(){
            resourceNotFoundException = assertThrows(
                    ResourceNotFoundException.class,
                    () -> loanService.getLoanById(LOAN_ID)
            );
        }

        void thenAssertThatExceptionMessageMatchesExpectedMessage(){
            assertEquals("Unable to find Loan with id: " + LOAN_ID, resourceNotFoundException.getMessage());
        }

        void givenGetLoanRequestAndFundingProposalIdIsCalled(){
            when(createLoanDto.getLoanRequestId()).thenReturn(LOAN_REQUEST_ID);
            when(createLoanDto.getAcceptedLoanProposalId()).thenReturn(FUNDING_PROPOSAL_ID);
            when(createLoanDto.getBorrowerWalletAccountId()).thenReturn(BORROWER_WALLET_ACCOUNT_ID);
            when(createLoanDto.getLenderWalletAccountId()).thenReturn(LENDER_WALLET_ACCOUNT_ID);
        }

        void givenFindByIdOfLoanRequestRepositoryIsCalled(){
            when(loanRequestRepo.findById(LOAN_REQUEST_ID)).thenReturn(Optional.of(loanRequest));
        }

        void givenFindByIdOfFundingProposalRepositoryIsCalled(){
            when(fundingProposalRepository.findById(FUNDING_PROPOSAL_ID)).thenReturn(Optional.of(fundingProposal));
        }

        void givenFindByIdOfWalletAccountsIsCalled(){
            WalletAccount borrowerWalletAccount = new WalletAccount();
            borrowerWalletAccount.setId(BORROWER_WALLET_ACCOUNT_ID);
            when(walletAccountRepository.findById(BORROWER_WALLET_ACCOUNT_ID)).thenReturn(Optional.of(borrowerWalletAccount));

            WalletAccount lenderWalletAccount = new WalletAccount();
            lenderWalletAccount.setId(LENDER_WALLET_ACCOUNT_ID);
            when(walletAccountRepository.findById(LENDER_WALLET_ACCOUNT_ID)).thenReturn(Optional.of(lenderWalletAccount));
        }

        void givenValidCrateLoanDtoIsMocked(){
            when(fundingProposal.getCurrencyCode()).thenReturn(CURRENCY_CODE);
            when(fundingProposal.getProposedTipAmount()).thenReturn(TIP_AMOUNT);

            when(loanRequest.isGuaranteeable()).thenReturn(true);
            when(loanRequest.getDueDate()).thenReturn(DUE_DATE);

            when(fundingProposal.getCurrencyExponent()).thenReturn(CURRENCY_EXPONENT);
            when(fundingProposal.getAmount()).thenReturn(AMOUNT);
            when(fundingProposal.getSoloCreditsAmount()).thenReturn(SOLO_CREDITS_AMOUNT);
            when(fundingProposal.isSlp()).thenReturn(true);

            when(loanRequest.getPrincipal()).thenReturn(PRINCIPAL);
            when(loanRequest.getDonationAmount()).thenReturn(DONATION_AMOUNT);
        }

        void givenLoanCreateRequestIsMappedToLoan(){
            when(loanMapper.toEntity(any(CreateLoanDto.class))).thenReturn(loan);
        }

        void givenValidLoanIsMocked(){
            when(fundingProposal.getCurrencyCode()).thenReturn(CURRENCY_CODE);
            when(fundingProposal.getProposedTipAmount()).thenReturn(TIP_AMOUNT);

            when(loanRequest.isGuaranteeable()).thenReturn(true);
            when(loanRequest.getDueDate()).thenReturn(DUE_DATE);

            when(fundingProposal.getCurrencyExponent()).thenReturn(CURRENCY_EXPONENT);
            when(fundingProposal.getAmount()).thenReturn(AMOUNT);
            when(fundingProposal.getSoloCreditsAmount()).thenReturn(SOLO_CREDITS_AMOUNT);
            when(fundingProposal.isSlp()).thenReturn(true);

            when(loanRequest.getPrincipal()).thenReturn(PRINCIPAL);
            when(loanRequest.getDonationAmount()).thenReturn(DONATION_AMOUNT);
        }

        void givenSaveLoanOfRepositoryIsCalled() {
            when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        }

        void whenCreateLoanOfServiceIsCalled() {
            savedLoan = loanService.createLoan(createLoanDto);
        }

        void whenUpdateLoanOfServiceIsCalled() {
            savedLoan = loanService.updateLoan(LOAN_ID, updateLoanDTO);
        }

        void thenAssertSavedLoanDtoIsNotNul() {
            assertNotNull(savedLoan);
        }

        void givenFindAllByBorrowerSoloMemberIdOrLenderSoloMemberIdIsCalled(){
            when(loanRepository.findAllByBorrowerSoloMemberIdOrLenderSoloMemberId(SOLO_MEMBER_GUID, SOLO_MEMBER_GUID))
                    .thenReturn(List.of(loan, loan, loan));
        }

        void givenFindAllByStatusAndBorrowerSoloMemberIdOrStatusAndLenderSoloMemberIdCalled(){
            when(loanRepository.findAllByStatusAndBorrowerSoloMemberIdOrStatusAndLenderSoloMemberId(LoanStatus.COLLECTIONS,SOLO_MEMBER_GUID,LoanStatus.COLLECTIONS, SOLO_MEMBER_GUID))
                    .thenReturn(List.of(loan, loan, loan));
        }

        void whenGetLoansByMemberIdIsCalled(){
            loanDTOS = loanService.getLoansByMemberId(SOLO_MEMBER_GUID);
        }

        void whenGetLoansByLoanStatusAndMemberIdCalled(){
            loanDTOS = loanService.getLoansByLoanStatusAndMemberId(SOLO_MEMBER_GUID,LoanStatus.COLLECTIONS);
        }

        void thenVerifyListSize() {
            assertEquals(3, loanDTOS.size());
        }

        void thenVerifyGetLoansByMemberIdIsCalled()  {
            verify(loanRepository, times(1))
                    .findAllByBorrowerSoloMemberIdOrLenderSoloMemberId(SOLO_MEMBER_GUID, SOLO_MEMBER_GUID);
        }


        void thenVerifyGetLoansByLoanStatusAndMemberIdCalled()  {
            verify(loanRepository, times(1))
                    .findAllByStatusAndBorrowerSoloMemberIdOrStatusAndLenderSoloMemberId(LoanStatus.COLLECTIONS,SOLO_MEMBER_GUID, LoanStatus.COLLECTIONS,SOLO_MEMBER_GUID);
        }



        void givenUpdateLoanDtoIsMappedToLoan(){
            when(loanMapper.toUpdateLoan(any(UpdateLoanDto.class), any(Loan.class))).thenReturn(loan);
        }
    }
}