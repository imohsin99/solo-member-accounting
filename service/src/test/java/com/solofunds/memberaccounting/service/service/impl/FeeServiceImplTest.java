package com.solofunds.memberaccounting.service.service.impl;

import com.solofunds.memberaccounting.model.CreateFeeDto;
import com.solofunds.memberaccounting.model.FeeDto;
import com.solofunds.memberaccounting.service.entities.Collection;
import com.solofunds.memberaccounting.service.entities.Fee;
import com.solofunds.memberaccounting.service.entities.FundingProposal;
import com.solofunds.memberaccounting.service.entities.Loan;
import com.solofunds.memberaccounting.service.exception.custom.ResourceNotFoundException;
import com.solofunds.memberaccounting.service.mappers.FeeMapper;
import com.solofunds.memberaccounting.service.repositories.CollectionRepository;
import com.solofunds.memberaccounting.service.repositories.FeeRepository;
import com.solofunds.memberaccounting.service.repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class FeeServiceImplTest {

    @Mock
    FeeRepository feeRepository;

    @Mock
    LoanRepository loanRepository;

    @Mock
    FundingProposal fundingProposal;

    @Mock
    CollectionRepository collectionRepository;

    @Mock
    CreateFeeDto createFeeDto;

    @Mock
    FeeDto feeDTO;

    @Mock
    Fee fee;

    @Mock
    Loan loan;

    @Mock
    Collection collection;

    @Mock
    FeeMapper mapper;

    @InjectMocks
    FeeServiceImpl feeService;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Test
    void testGetAllFeesForLoanIdSuccess(){
        fixture.givenFeeIsMocked();
        fixture.givenFeeDTOIsMocked();
        fixture.givenFeeListIsMocked();
        fixture.givenFeeDTOListIsMocked();
        fixture.givenFeeIsMappedToDTO();
        fixture.givenLoanRepositoryIsMockedToGetExistingLoanById();
        fixture.givenFeeRepositoryIsMockedToFindAllByLoansId();

        fixture.whenFeeServiceIsCalledToGetAllFeesForLoanId();

        fixture.thenVerifySizeOfActualFeeList();
        fixture.thenVerifyTheIdsOfObjects();
    }

    @Test
    void testGetAllFeesForLoanIdFailure(){
        fixture.givenLoanRepositoryIsMockedToGetNonExistingLoanById();
        fixture.thenVerifyResourceNotFoundExceptionIsThrownByGetAllFeesForLoanId();
    }

    @Test
    void testCreateFeeForLoanIdSuccess(){
        fixture.givenFeeIsMocked();
        fixture.givenCreateFeeDTOIsMocked();
        fixture.givenFeeListIsMocked();
        fixture.givenFeeDTOListIsMocked();
        fixture.givenFeeIsMappedToDTO();
        fixture.givenLoanIsMocked();
        fixture.givenLoanRepositoryIsMockedToFindExistingLoanById();
        fixture.givenFeeRepositoryIsMockedToSaveFees();

        fixture.whenFeeServiceIsCalledToCreateFeeForLoanId();

        fixture.thenVerifyTheIdOfActualFee();
    }

    @Test
    void testCreateFeeForLoanIdFailure(){
        fixture.givenLoanRepositoryIsMockedToFindNonExistingLoanById();
        fixture.thenVerifyResourceNotFoundExceptionIsThrownByCreateFeeForLoanId();
    }

    @Test
    void testGetAllFeesForCollectionIdSuccess(){
        fixture.givenFeeIsMocked();
        fixture.givenFeeDTOIsMocked();
        fixture.givenFeeListIsMocked();
        fixture.givenFeeIsMappedToDTO();
        fixture.givenFeeDTOListIsMocked();
        fixture.givenCollectionRepositoryIsMockedToGetFeesByExistingCollectionId();

        fixture.givenFeeRepositoryIsMockedToFindExistingCollection();

        fixture.whenFeeServiceIsMockedToGetFeesByCollectionId();
        fixture.thenVerifySizeOfActualFeeList();
        fixture.thenVerifyTheIdsOfObjects();
    }

    @Test
    void testGetAllFeesForCollectionIdFailure(){
        fixture.givenCollectionRepositoryIsMockedToGetFeesByNonExistingCollectionId();
        fixture.thenVerifyResourceNotFoundExceptionIsThrownByGetAllFeesForCollectionId();
    }

    @Test
    void testCreateFeeForCollectionIdSuccess(){
        fixture.givenFeeIsMocked();
        fixture.givenCreateFeeDTOIsMocked();
        fixture.givenFeeListIsMocked();
        fixture.givenFeeIsMappedToDTO();
        fixture.givenFeeDTOListIsMocked();
        fixture.givenCollectionIsMocked();
        fixture.givenCollectionRepositoryIsMockedToFindTheExistingCollectionById();

        fixture.givenFeeRepositoryIsMockedToSaveFees();

        fixture.whenFeeServiceIsCalledToCreateFeeForCollectionId();
        fixture.thenVerifyTheIdOfActualFee();
    }

    @Test
    void testCreateFeeForCollectionIdFailure(){
        fixture.givenCollectionRepositoryIsMockedToFindTheNonExistingCollectionById();
        fixture.thenVerifyResourceNotFoundExceptionIsThrownByCreateFeeForCollectionId();
    }

    private class Fixture{

        public final UUID FEE_ID = UUID.randomUUID();

        public final BigDecimal AMOUNT = BigDecimal.valueOf(100);

        public final String CURRENCY_CODE = "USD";

        public final String CURRENCY_EXPONENT = "2";

        public final UUID LOAN_ID = UUID.randomUUID();

        public final UUID COLLECTIONS_ID = UUID.randomUUID();

        public final Boolean LOAN_FOUND = true;

        public final Boolean NO_LOAN_FOUND = false;

        public final Boolean COLLECTION_FOUND = true;

        public final Boolean NO_COLLECTION_FOUND = false;

        public UUID BORROWER_SOLO_MEMBER_ID = UUID.randomUUID();

        public UUID LENDER_SOLO_MEMBER_ID = UUID.randomUUID();

        public UUID LOAN_PROPOSAL_ID = UUID.randomUUID();

        List<Fee> feeList;

        List<FeeDto> feeDTOList;

        List<FeeDto> actualFees;

        FeeDto actualFee;

        public void givenFeeIsMocked(){
            when(fee.getId()).thenReturn(FEE_ID);
            when(fee.getCurrencyCode()).thenReturn(CURRENCY_CODE);
            when(fee.getCurrencyExponent()).thenReturn(CURRENCY_EXPONENT);
            when(fee.getAmount()).thenReturn(AMOUNT);
        }

        public void givenLoanIsMocked() {
            when(loan.getId()).thenReturn(LOAN_ID);
            when(loan.getAcceptedLoanProposal()).thenReturn(fundingProposal);
            when(loan.getBorrowerWalletAccountId()).thenReturn(LOAN_ID);
            when(loan.getBorrowerSoloMemberId()).thenReturn(BORROWER_SOLO_MEMBER_ID);
            when(loan.getLenderSoloMemberId()).thenReturn(LENDER_SOLO_MEMBER_ID);
        }

        public void givenCollectionIsMocked() {
            when(collection.getId()).thenReturn(COLLECTIONS_ID);
            when(collection.getBorrowersTotalDue()).thenReturn(AMOUNT);
            when(collection.getTotalPaidBackAmount()).thenReturn(AMOUNT);
            when(collection.getPartialPaymentAmount()).thenReturn(AMOUNT);
        }
        public void givenFeeListIsMocked(){
            feeList=List.of(fee);
        }

        public void givenFeeIsMappedToDTO(){
            when(mapper.toDTO(any(Fee.class))).thenReturn(feeDTO);
        }

        public void givenFeeDTOIsMocked(){
            when(feeDTO.getId()).thenReturn(FEE_ID);
            when(feeDTO.getCurrencyCode()).thenReturn(CURRENCY_CODE);
            when(feeDTO.getCurrencyExponent()).thenReturn(CURRENCY_EXPONENT);
            when(feeDTO.getAmount()).thenReturn(AMOUNT);
        }

        public void givenCreateFeeDTOIsMocked(){
            when(createFeeDto.getCurrencyCode()).thenReturn(CURRENCY_CODE);
            when(createFeeDto.getCurrencyExponent()).thenReturn(CURRENCY_EXPONENT);
            when(createFeeDto.getAmount()).thenReturn(AMOUNT);
        }

        public void givenFeeDTOListIsMocked(){
            feeDTOList=List.of(feeDTO);
        }

        public void givenLoanRepositoryIsMockedToGetExistingLoanById() {
            when(loanRepository.existsById(LOAN_ID)).thenReturn(LOAN_FOUND);
        }

        public void givenFeeRepositoryIsMockedToFindAllByLoansId() {
            when(feeRepository.findAllByLoansId(LOAN_ID)).thenReturn(feeList);
        }

        public void whenFeeServiceIsCalledToGetAllFeesForLoanId() {
            actualFees = feeService.getAllFeesForLoanId(LOAN_ID);
        }

        public void thenVerifySizeOfActualFeeList() {
            assertEquals(feeList.size(),actualFees.size());
        }

        public void thenVerifyTheIdsOfObjects() {
            assertEquals(feeList.get(0).getId(),actualFees.get(0).getId());
        }

        public void givenLoanRepositoryIsMockedToGetNonExistingLoanById() {
            when(loanRepository.existsById(LOAN_ID)).thenReturn(NO_LOAN_FOUND);
        }

        public void thenVerifyResourceNotFoundExceptionIsThrownByGetAllFeesForLoanId() {
            assertThrows(ResourceNotFoundException.class, () -> {
                feeService.getAllFeesForLoanId(LOAN_ID);
            });
        }

        public void givenFeeRepositoryIsMockedToSaveFees() {
            when(feeRepository.save(any(Fee.class))).thenReturn(fee);
        }

        public void whenFeeServiceIsCalledToCreateFeeForLoanId() {
            actualFee=feeService.createFeeForLoanId(LOAN_ID,createFeeDto);
        }

        public void thenVerifyTheIdOfActualFee() {
            assertEquals(actualFee.getId(), fee.getId());
        }

        public void thenVerifyResourceNotFoundExceptionIsThrownByCreateFeeForLoanId() {
            assertThrows(ResourceNotFoundException.class,() -> {
                feeService.createFeeForLoanId(LOAN_ID, createFeeDto);
            });
        }

        public void givenCollectionRepositoryIsMockedToGetFeesByExistingCollectionId() {
            when(collectionRepository.existsById(COLLECTIONS_ID)).thenReturn(COLLECTION_FOUND);
        }

        public void givenFeeRepositoryIsMockedToFindExistingCollection() {
            when(feeRepository.findAllByCollectionsId(COLLECTIONS_ID)).thenReturn(feeList);
        }

        public void whenFeeServiceIsMockedToGetFeesByCollectionId() {
            actualFees = feeService.getAllFeesForCollectionId(COLLECTIONS_ID);
        }

        public void givenCollectionRepositoryIsMockedToGetFeesByNonExistingCollectionId() {
            when(collectionRepository.existsById(COLLECTIONS_ID)).thenReturn(NO_COLLECTION_FOUND);
        }

        public void thenVerifyResourceNotFoundExceptionIsThrownByGetAllFeesForCollectionId() {
            assertThrows(ResourceNotFoundException.class, () -> {
                feeService.getAllFeesForCollectionId(COLLECTIONS_ID);
            });
        }

        public void whenFeeServiceIsCalledToCreateFeeForCollectionId() {
            actualFee=feeService.createFeeForCollectionId(COLLECTIONS_ID,createFeeDto);
        }

        public void thenVerifyResourceNotFoundExceptionIsThrownByCreateFeeForCollectionId() {
            assertThrows(ResourceNotFoundException.class,() -> {
                feeService.createFeeForCollectionId(COLLECTIONS_ID, createFeeDto);
            });
        }

        public void givenCollectionRepositoryIsMockedToFindTheExistingCollectionById() {
            when(collectionRepository.findById(COLLECTIONS_ID)).thenReturn(Optional.of(collection));
        }

        public void givenCollectionRepositoryIsMockedToFindTheNonExistingCollectionById() {
            when(collectionRepository.findById(COLLECTIONS_ID)).thenReturn(Optional.empty());
        }

        public void givenLoanRepositoryIsMockedToFindExistingLoanById() {
            when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));
        }

        public void givenLoanRepositoryIsMockedToFindNonExistingLoanById() {
            when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.empty());
        }
    }
}