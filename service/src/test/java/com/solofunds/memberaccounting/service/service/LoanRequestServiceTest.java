package com.solofunds.memberaccounting.service.service;

import com.solofunds.memberaccounting.model.*;
import com.solofunds.memberaccounting.service.entities.FundingProposal;
import com.solofunds.memberaccounting.service.entities.LoanRequest;
import com.solofunds.memberaccounting.service.mappers.LoanRequestMapper;
import com.solofunds.memberaccounting.service.repositories.FundingProposalRepository;
import com.solofunds.memberaccounting.service.repositories.LoanRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoanRequestServiceTest {

    @Mock
    LoanRequest loanRequest;

    @Mock
    CreateLoanRequestDto createLoanRequestDto;

    @Mock
    LoanRequestDto loanRequestDto;

    @Mock
    LoanRequestMapper loanRequestMapper;

    @Mock
    LoanRequestRepository loanRequestRepository;
    @Mock
    FundingProposalRepository fundingProposalRepository;

    @InjectMocks
    LoanRequestService loanRequestService;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Test
    void testCreateLoanRequestWithGuaranteeableTrue() {
        fixture.givenValidCreateLoanRequestDtoIsMocked();
        fixture.givenLoanRequestDtoIsMappedToLoanRequest();
        fixture.givenValidLoanRequestIsMocked();
        fixture.givenValidLoanRequestIsMockedWithFieldGuaranteeableTrue();
        fixture.givenSaveLoanRequestOfRepositoryIsCalled();

        fixture.whenCreateLoanRequestOfServiceIsCalled();

        fixture.thenAssertSavedLoanRequestDtoIsNotNul();
    }

    @Test
    void testCreateLoanRequestWithGuaranteeableFalse() {
        fixture.givenValidCreateLoanRequestDtoIsMocked();
        fixture.givenLoanRequestDtoIsMappedToLoanRequest();
        fixture.givenValidLoanRequestIsMocked();
        fixture.givenValidLoanRequestIsMockedWithFieldGuaranteeableFalse();
        fixture.givenSaveLoanRequestOfRepositoryIsCalled();

        fixture.whenCreateLoanRequestOfServiceIsCalled();

        fixture.thenAssertSavedLoanRequestDtoIsNotNul();
    }

    @Test
    void testGetLoanRequestById() {
        fixture.givenFindByIdOfRepositoryIsCalled();
        fixture.givenLoanRequestIsMappedToLoanRequestDto();

        fixture.whenGetLoanRequestByIdOfServiceIsCalled();

        fixture.thenAssertExpectedLoanRequestIsNotNull();
    }

    @Test
    void testDeleteLoanRequestById() {
        fixture.givenLoanRequestExistsByIdSuccess();
        fixture.givenFindByIdOfRepositoryIsCalled();

        fixture.whenDeleteLoanRequestById();

        fixture.thenDeleteLoanRequestById();
        fixture.thenVerifySaveLoanRequestOfRepositoryIsCalled();
    }

    @Test
    void getFilteredAndSortedEntitiesWithStatus(){
        fixture.givenValidLoanRequestIsMocked();
        fixture.givenFilteredLoanRequestListIsCalled();
        fixture.givenLoanRequestRepositoryIsCalledWithStatus();

        fixture.whenGetFilteredAndSortedEntitiesOfLoanRequestServiceIsCalled();

        fixture.thenVerifyFilteredAndSortedEntitiesCalledWithStatus();
    }

    @Test
    void getFilteredAndSortedEntitiesWithSlp(){
        fixture.givenValidLoanRequestIsMocked();
        fixture.givenFilteredLoanRequestListIsCalled();
        fixture.givenLoanRequestRepositoryIsCalledWithStatusAndSlp();

        fixture.whenGetFilteredAndSortedEntitiesOfLoanRequestServiceIsCalledWithSlp();

        fixture.thenVerifyFilteredAndSortedEntitiesCalledWithStatusWithSlp();
    }

    @Test
    void testGetAllFundingProposalByLoanRequestId() {
        fixture.givenFindByLoanRequest_IdOfRepositoryIsCalled();

        fixture.whenGetAllFundingProposalByLoanRequestIdOfServiceIsCalled();

        fixture.thenVerifyFundingProposalListSize();
        fixture.thenVerifySameLoanRequestIds();
        fixture.thenVerifyFindByLoanRequest_IdOfFundingProposalRepositoryIsCalled();
    }

    private class Fixture {

        final UUID ID = UUID.randomUUID();

        final BigDecimal PRINCIPAL = new BigDecimal("1000");

        final BigDecimal SOLO_SCORE = new BigDecimal("50");

        final BigDecimal TIP_AMOUNT = new BigDecimal("200");

        final BigDecimal DONATION_AMOUNT = new BigDecimal("5");

        final BigDecimal DURATION = new BigDecimal("12");

        final LoanRequestStatus status = LoanRequestStatus.ACTIVE;

        final SortBy sortBy = SortBy.SOLO_SCORE;

        final SortDirection sortDirection = SortDirection.ASC;

        final BigDecimal AMOUNT = new BigDecimal("200");

        final Boolean slpEligible = null;

        List<FundingProposalDto> fundingProposalDtoList;

        List<LoanRequest> filteredLoanRequests;

        LoanRequestDto savedLoanRequestDto;

        LoanRequestDto expectedLoanRequestDto;

        void givenValidCreateLoanRequestDtoIsMocked() {
            when(createLoanRequestDto.getPrincipal()).thenReturn(PRINCIPAL);
            when(createLoanRequestDto.getSoloScore()).thenReturn(SOLO_SCORE);
            when(createLoanRequestDto.getTipAmount()).thenReturn(TIP_AMOUNT);
            when(createLoanRequestDto.getDonationAmount()).thenReturn(DONATION_AMOUNT);
            when(createLoanRequestDto.getDuration()).thenReturn(DURATION.longValue());
            when(createLoanRequestDto.getPrincipal()).thenReturn(AMOUNT);
        }

        void givenLoanRequestDtoIsMappedToLoanRequest() {
            when(loanRequestMapper.toEntity(createLoanRequestDto)).thenReturn(loanRequest);
        }

        void givenValidLoanRequestIsMocked() {
            when(loanRequest.getPrincipal()).thenReturn(PRINCIPAL);
            when(loanRequest.getTipAmount()).thenReturn(TIP_AMOUNT);
            when(loanRequest.getDonationAmount()).thenReturn(DONATION_AMOUNT);
            when(loanRequest.getDuration()).thenReturn(DURATION);
        }

        void givenValidLoanRequestIsMockedWithFieldGuaranteeableTrue() {
            when(loanRequest.isGuaranteeable()).thenReturn(true);
        }

        void givenValidLoanRequestIsMockedWithFieldGuaranteeableFalse() {
            when(loanRequest.isGuaranteeable()).thenReturn(false);
        }

        void givenSaveLoanRequestOfRepositoryIsCalled() {
            when(loanRequestRepository.save(any(LoanRequest.class))).thenReturn(loanRequest);
        }

        void whenCreateLoanRequestOfServiceIsCalled() {
            savedLoanRequestDto = loanRequestService.createLoanRequest(createLoanRequestDto);
        }

        void thenAssertSavedLoanRequestDtoIsNotNul() {
            assertNotNull(savedLoanRequestDto);
        }

        void givenFindByIdOfRepositoryIsCalled() {
            when(loanRequestRepository.findById(ID)).thenReturn(Optional.of(loanRequest));
        }

        void givenLoanRequestIsMappedToLoanRequestDto() {
            when(loanRequestMapper.toDto(loanRequest)).thenReturn(loanRequestDto);
        }

        void whenGetLoanRequestByIdOfServiceIsCalled() {
            expectedLoanRequestDto = loanRequestService.getLoanRequestById(ID);
        }

        void thenAssertExpectedLoanRequestIsNotNull() {
            assertNotNull(expectedLoanRequestDto);
        }

        void givenLoanRequestExistsByIdSuccess() {
            when(loanRequestRepository.existsById(ID)).thenReturn(true);
        }

        void whenDeleteLoanRequestById() {
            loanRequestService.deleteLoanRequest(ID);
        }

        void thenDeleteLoanRequestById() {
            verify(loanRequest).setStatus(LoanRequestStatus.CANCELLED);
        }

        void thenVerifySaveLoanRequestOfRepositoryIsCalled() {
            verify(loanRequestRepository, times(1)).save(loanRequest);
        }

        void givenFindByLoanRequest_IdOfRepositoryIsCalled() {
            FundingProposal first = new FundingProposal();
            first.setId(UUID.randomUUID());
            first.setLoanRequest(loanRequest);

            FundingProposal second = new FundingProposal();
            second.setId(UUID.randomUUID());
            second.setLoanRequest(loanRequest);

            when(fundingProposalRepository.findByLoanRequest_Id(ID)).thenReturn(Arrays.asList(first, second));
        }

        void whenGetAllFundingProposalByLoanRequestIdOfServiceIsCalled() {
            fundingProposalDtoList = loanRequestService.getAllFundingProposalByLoanRequestId(ID);
        }

        void thenVerifyFundingProposalListSize() {
            assertEquals(2, fundingProposalDtoList.size());
        }

        void thenVerifySameLoanRequestIds() {
            assertEquals(fundingProposalDtoList.get(0).getLoanRequestId(), fundingProposalDtoList.get(1).getLoanRequestId());
        }

        void thenVerifyFindByLoanRequest_IdOfFundingProposalRepositoryIsCalled()  {
            verify(fundingProposalRepository, times(1)).findByLoanRequest_Id(ID);
        }

        void givenLoanRequestRepositoryIsCalledWithStatus(){
            when(loanRequestRepository.findByStatus(status)).thenReturn(filteredLoanRequests);
        }

        void givenLoanRequestRepositoryIsCalledWithStatusAndSlp(){
            when(loanRequestRepository.findByGuaranteeableAndStatus(true, status)).thenReturn(filteredLoanRequests);
        }
        void whenGetFilteredAndSortedEntitiesOfLoanRequestServiceIsCalled() {
            loanRequestService.getFilteredAndSortedEntities(status, sortDirection, sortBy, slpEligible);
        }

        void whenGetFilteredAndSortedEntitiesOfLoanRequestServiceIsCalledWithSlp() {
            loanRequestService.getFilteredAndSortedEntities(status, SortDirection.DESC, SortBy.CREATED_AT, true);
        }

        void thenVerifyFilteredAndSortedEntitiesCalledWithStatus()  {
            verify(loanRequestRepository, times(1)).findByStatus(status);
        }

        void thenVerifyFilteredAndSortedEntitiesCalledWithStatusWithSlp()  {
            verify(loanRequestRepository, times(1)).findByGuaranteeableAndStatus(true, status);
        }

        void givenFilteredLoanRequestListIsCalled(){
            filteredLoanRequests = Collections.singletonList(
                    LoanRequest.builder().total(BigDecimal.valueOf(10))
                            .soloScore(BigDecimal.ONE)
                            .tipAmount(BigDecimal.ONE)
                            .principal(BigDecimal.valueOf(30))
                            .guaranteeable(true)
                            .build()
            );
        }
    }
}