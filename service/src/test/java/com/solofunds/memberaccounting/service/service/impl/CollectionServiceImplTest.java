package com.solofunds.memberaccounting.service.service.impl;

import com.solofunds.memberaccounting.model.CollectionsDto;
import com.solofunds.memberaccounting.model.CreateCollectionsDto;
import com.solofunds.memberaccounting.model.LoanDto;
import com.solofunds.memberaccounting.service.entities.Collection;
import com.solofunds.memberaccounting.service.entities.Loan;
import com.solofunds.memberaccounting.service.entities.LoanRequest;
import com.solofunds.memberaccounting.service.exception.custom.ResourceNotFoundException;
import com.solofunds.memberaccounting.service.mappers.CollectionMapper;
import com.solofunds.memberaccounting.service.repositories.CollectionRepository;
import com.solofunds.memberaccounting.service.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CollectionServiceImplTest {

    @Mock
    CollectionRepository collectionRepository;

    @Mock
    CollectionMapper collectionsMapper;

    @Mock
    LoanService loanService;

    @Mock
    Collection collection;

    @Mock
    Loan loan;

    @Mock
    LoanRequest loanRequest;

    @Mock
    LoanDto loanDTO;

    @Mock
    CreateCollectionsDto createCollectionsDto;

    @Mock
    CollectionsDto collectionsDTO;

    @InjectMocks
    CollectionServiceImpl collectionsService;

    Fixture fixture;

    @BeforeEach
    public void setUp() {
        fixture = new Fixture();
    }

    @Test
    void createCollections() {
        fixture.givenGetLoanByIdOfLoanServiceIsCalled();
        fixture.givenCollectionIsMocked();
        fixture.givenLoanIsMocked();
        fixture.givenCreateCollectionsDTOIsMocked();
        fixture.givenCollectionIsMappedToDTO();
        fixture.givenCollectionsRepositoryIsMockedForCreateCollection();

        fixture.whenCollectionServiceIsCalledToCreateCollection();

        fixture.thenVerifyCollectionIdIsNotNull();
        fixture.thenVerifyInteractionWithCollectionRepositoryToSaveCollection();
    }

    @Test
    void createCollectionsWithDiffCondition() {
        fixture.givenGetLoanByIdOfLoanServiceIsCalled();
        fixture.givenCollectionIsMockedWithDiffCondition();
        fixture.givenLoanIsMocked();
        fixture.givenCreateCollectionsDTOIsMockedWithDiffCondition();
        fixture.givenCollectionIsMappedToDTO();
        fixture.givenCollectionsRepositoryIsMockedForCreateCollection();

        fixture.whenCollectionServiceIsCalledToCreateCollection();

        fixture.thenVerifyCollectionIdIsNotNull();
        fixture.thenVerifyInteractionWithCollectionRepositoryToSaveCollection();
    }

    @Test
    void getCollectionsById() {
        fixture.givenFindByIdOfCollectionRepoIsCalled();
        fixture.givenCollectionIsMappedToDTO();

        fixture.whenCollectionServiceIsCalledToGetCollectionById();

        fixture.thenVerifyCollectionId();
        fixture.thenVerifyInteractionWithCollectionRepositoryToGetCollection();
        fixture.thenVerifyCollectionDTOIsNotNull();
    }

    @Test
    void getCollectionsById_NullCase() {
        fixture.givenFindByIdOfCollectionRepoIsCalledThenReturnNull();

        fixture.whenCollectionServiceThrowResourceNotFound();

        fixture.thenAssertThatExceptionMessageMatchesExpectedMessage();
    }

    @Nested
    private class Fixture {

        final UUID COLLECTION_ID = UUID.randomUUID();

        final UUID LOAN_ID = UUID.randomUUID();

        final BigDecimal ORIGINAL_DUE_FROM_BORROWER = BigDecimal.valueOf(10);

        final BigDecimal TOTAL_PAID_BACK_AMOUNT = BigDecimal.valueOf(20);

        final BigDecimal BORROWER_TOTAL_DUE = BigDecimal.valueOf(20);

        final BigDecimal TOTAL_GOING_TO_SOLO= BigDecimal.valueOf(10);

        final BigDecimal TOTAL_GOING_TO_LENDER= BigDecimal.valueOf(10);

        final Boolean PARTIAL_PAYMENT_AGREEMENT = true;

        final BigDecimal PARTIAL_PAYMENT_AMOUNT = BigDecimal.valueOf(10);

        final BigDecimal PRINCIPAL =  BigDecimal.valueOf(10);
        final BigDecimal DONATION_AMOUNT =  BigDecimal.valueOf(10);

        final BigDecimal TIP_AMOUNT =  BigDecimal.valueOf(10);

        ResourceNotFoundException exception;

        CollectionsDto actualCollectionsDTO;

        void givenCollectionIsMocked(){
            when(collection.getId()).thenReturn(COLLECTION_ID);
            when(collection.getOriginalTotalDueFromBorrower()).thenReturn(ORIGINAL_DUE_FROM_BORROWER);
            when(collection.getTotalPaidBackAmount()).thenReturn(TOTAL_PAID_BACK_AMOUNT);
            when(collection.getTotalGoingToSolo()).thenReturn(TOTAL_GOING_TO_SOLO);
            when(collection.getTotalGoingToLender()).thenReturn(TOTAL_GOING_TO_LENDER);
            when(collection.getPartialPaymentAgreement()).thenReturn(PARTIAL_PAYMENT_AGREEMENT);
            when(collection.getPartialPaymentAmount()).thenReturn(PARTIAL_PAYMENT_AMOUNT);
            when(collection.getLoan()).thenReturn(loan);
        }

        void givenCollectionIsMockedWithDiffCondition(){
            when(collection.getId()).thenReturn(COLLECTION_ID);
            when(collection.getTotalPaidBackAmount()).thenReturn(TOTAL_PAID_BACK_AMOUNT);
            when(collection.getBorrowersTotalDue()).thenReturn(BORROWER_TOTAL_DUE);
            when(collection.getTotalGoingToSolo()).thenReturn(TOTAL_GOING_TO_SOLO);
            when(collection.getTotalGoingToLender()).thenReturn(TOTAL_GOING_TO_LENDER);
            when(collection.getPartialPaymentAgreement()).thenReturn(PARTIAL_PAYMENT_AGREEMENT);
            when(collection.getPartialPaymentAmount()).thenReturn(PARTIAL_PAYMENT_AMOUNT);
            when(collection.getLoan()).thenReturn(loan);
        }

        void givenLoanIsMocked(){
            when(loan.getLoanRequest()).thenReturn(loanRequest);
            when(loanRequest.getPrincipal()).thenReturn(PRINCIPAL);
            when(loanRequest.getDonationAmount()).thenReturn(DONATION_AMOUNT);
            when(loanRequest.getTipAmount()).thenReturn(TIP_AMOUNT);
            when(loanService.getLoanEntityById(LOAN_ID)).thenReturn(loan);
        }

        void givenCreateCollectionsDTOIsMocked(){
            when(createCollectionsDto.getLoanId()).thenReturn(LOAN_ID);
            when(createCollectionsDto.getOriginalTotalDueFromBorrower()).thenReturn(ORIGINAL_DUE_FROM_BORROWER);
            when(createCollectionsDto.getTotalPaidBackAmount()).thenReturn(TOTAL_PAID_BACK_AMOUNT);
            when(createCollectionsDto.getTotalGoingToSolo()).thenReturn(TOTAL_GOING_TO_SOLO);
            when(createCollectionsDto.getTotalGoingToLender()).thenReturn(TOTAL_GOING_TO_LENDER);
            when(createCollectionsDto.getPartialPaymentAgreement()).thenReturn(PARTIAL_PAYMENT_AGREEMENT);
            when(createCollectionsDto.getPartialPaymentAmount()).thenReturn(PARTIAL_PAYMENT_AMOUNT);
        }

        void givenCreateCollectionsDTOIsMockedWithDiffCondition(){
            when(createCollectionsDto.getLoanId()).thenReturn(LOAN_ID);
            when(createCollectionsDto.getTotalPaidBackAmount()).thenReturn(TOTAL_PAID_BACK_AMOUNT);
            when(createCollectionsDto.getBorrowersTotalDue()).thenReturn(BORROWER_TOTAL_DUE);
            when(createCollectionsDto.getTotalGoingToSolo()).thenReturn(TOTAL_GOING_TO_SOLO);
            when(createCollectionsDto.getTotalGoingToLender()).thenReturn(TOTAL_GOING_TO_LENDER);
            when(createCollectionsDto.getPartialPaymentAgreement()).thenReturn(PARTIAL_PAYMENT_AGREEMENT);
            when(createCollectionsDto.getPartialPaymentAmount()).thenReturn(PARTIAL_PAYMENT_AMOUNT);
        }

        void givenFindByIdOfCollectionRepoIsCalled(){
            when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.of(collection));
        }

        void givenCollectionIsMappedToDTO(){
            when(collectionsMapper.toDTO(collection)).thenReturn(collectionsDTO);
        }

        void whenCollectionServiceIsCalledToGetCollectionById() {
            actualCollectionsDTO = collectionsService.getCollectionsById(COLLECTION_ID);
        }

        void thenVerifyCollectionId() {
            assertEquals(collectionsDTO.getId(), actualCollectionsDTO.getId());
        }

        void thenVerifyInteractionWithCollectionRepositoryToGetCollection() {
            verify(collectionRepository, times(1)).findById(COLLECTION_ID);
        }

        void thenVerifyCollectionDTOIsNotNull() {
            assertNotNull(collectionsDTO);
        }

        void givenFindByIdOfCollectionRepoIsCalledThenReturnNull() {
            when(collectionRepository.findById(COLLECTION_ID)).thenReturn(Optional.empty());
        }

        public void whenCollectionServiceThrowResourceNotFound() {
            exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> collectionsService.getCollectionsById(COLLECTION_ID)
            );
        }

        void thenAssertThatExceptionMessageMatchesExpectedMessage(){
            assertEquals("Unable to find collection with id: " + COLLECTION_ID, exception.getMessage());
        }

        void givenCollectionsRepositoryIsMockedForCreateCollection() {
            when(collectionRepository.save(any(Collection.class))).thenReturn(collection);
        }

        void whenCollectionServiceIsCalledToCreateCollection() {
            collectionsDTO = collectionsService.createCollections(createCollectionsDto);
        }

        void thenVerifyCollectionIdIsNotNull() {
            assertNotNull(collectionsDTO.getId());
        }

        void thenVerifyInteractionWithCollectionRepositoryToSaveCollection() {
            verify(collectionRepository, times(1)).save(any(Collection.class));
        }

        void givenGetLoanByIdOfLoanServiceIsCalled(){
            when(loanService.getLoanById(any(UUID.class))).thenReturn(loanDTO);
        }
    }
}