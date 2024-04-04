package com.solofunds.memberaccounting.service.service;

import com.solofunds.memberaccounting.model.CreateFundingProposalDto;
import com.solofunds.memberaccounting.model.FundingProposalDto;
import com.solofunds.memberaccounting.model.FundingStatus;
import com.solofunds.memberaccounting.model.UpdateFundingProposalDto;
import com.solofunds.memberaccounting.service.entities.FundingProposal;
import com.solofunds.memberaccounting.service.mappers.FundingProposalMapper;
import com.solofunds.memberaccounting.service.repositories.FundingProposalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FundingProposalServiceTest {
    @Mock
    FundingProposal fundingProposal;
    @Mock
    CreateFundingProposalDto createFundingProposalDto;
    @Mock
    FundingProposalDto fundingProposalDto;
    @Mock
    UpdateFundingProposalDto updateFundingProposalDto;
    @Mock
    FundingProposalMapper fundingProposalMapper;
    @Mock
    FundingProposalRepository fundingProposalRepository;

    @InjectMocks
    FundingProposalService fundingProposalService;

    @Test
    void testCreateFundingProposal() {
        fixture.givenValidFundingProposalDtoIsMocked();
        fixture.givenFundingProposalDtoIsMappedToFundingProposal();
        fixture.givenSaveFundingProposalOfRepositoryIsCalled();

        fixture.whenCreateFundingProposalOfServiceIsCalled();

        fixture.thenAssertSavedFundingProposalDtoIsNotNul();
    }

    @Test
    void testGetFundingProposalById() {
        fixture.givenFindByIdOfRepositoryIsCalled();
        fixture.givenFundingProposalIsMappedToFundingProposalDto();

        fixture.whenGetFundingProposalByIdOfServiceIsCalled();

        fixture.thenAssertExpectedFundingProposalIsNotNull();
    }

    @Test
    void testDeleteFundingProposalById() {
        fixture.givenFundingProposalExistsByIdOfRepositoryIsCalled();
        fixture.givenFindByIdOfRepositoryIsCalled();
        fixture.givenSaveFundingProposalOfRepositoryIsCalled();

        fixture.whenDeleteFundingProposalById();

        fixture.thenDeleteFundingProposalById();
        fixture.thenVerifySaveFundingProposalOfRepositoryIsCalled();
    }

    @Test
    void testUpdateFundingProposal() {
        fixture.givenFundingProposalExistsByIdOfRepositoryIsCalled();
        fixture.givenFindByIdOfRepositoryIsCalled();

        fixture.whenUpdateFundingProposalStatusofServiceIsCalled();

        fixture.thenVerifySaveFundingProposalOfRepositoryIsCalled();
    }

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Nested
    private class Fixture {
        final UUID FUNDING_PROPOSAL_ID = UUID.randomUUID();
        FundingProposalDto savedFundingProposalDto;
        FundingProposalDto expectedFundingProposalDto;
        Boolean deleteResult;
        void givenValidFundingProposalDtoIsMocked() { }

        void givenFundingProposalDtoIsMappedToFundingProposal() {
            when(fundingProposalMapper.toEntity(createFundingProposalDto)).thenReturn(fundingProposal);
        }

        void givenSaveFundingProposalOfRepositoryIsCalled() {
            when(fundingProposalRepository.save(any(FundingProposal.class))).thenReturn(fundingProposal);
        }

        void whenCreateFundingProposalOfServiceIsCalled() {
            savedFundingProposalDto = fundingProposalService.createFundingProposal(createFundingProposalDto);
        }

        void thenAssertSavedFundingProposalDtoIsNotNul() {
            assertNotNull(savedFundingProposalDto);
        }

        void givenFindByIdOfRepositoryIsCalled() {
            when(fundingProposalRepository.findById(FUNDING_PROPOSAL_ID)).thenReturn(Optional.of(fundingProposal));
        }

        void givenFundingProposalIsMappedToFundingProposalDto() {
            when(fundingProposalMapper.toDto(fundingProposal)).thenReturn(fundingProposalDto);
        }

        void whenGetFundingProposalByIdOfServiceIsCalled() {
            expectedFundingProposalDto = fundingProposalService.getFundingProposalById(FUNDING_PROPOSAL_ID);
        }

        void thenAssertExpectedFundingProposalIsNotNull() {
            assertNotNull(expectedFundingProposalDto);
        }

        void givenFundingProposalExistsByIdOfRepositoryIsCalled() {
            when(fundingProposalRepository.existsById(FUNDING_PROPOSAL_ID)).thenReturn(true);
        }

        void whenDeleteFundingProposalById() {
            deleteResult = fundingProposalService.deleteFundingProposal(FUNDING_PROPOSAL_ID);
        }

        void thenDeleteFundingProposalById() {
            assertTrue(deleteResult);
            verify(fundingProposal).setStatus(FundingStatus.CANCELLED);
        }

        void thenVerifySaveFundingProposalOfRepositoryIsCalled() {
            verify(fundingProposalRepository, times(1)).save(fundingProposal);
        }

        void whenUpdateFundingProposalStatusofServiceIsCalled() {
            expectedFundingProposalDto = fundingProposalService.updateFundingProposalStatus(FUNDING_PROPOSAL_ID, updateFundingProposalDto);
        }
    }
}
