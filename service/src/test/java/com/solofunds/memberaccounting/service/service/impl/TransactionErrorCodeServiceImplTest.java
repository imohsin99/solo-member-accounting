package com.solofunds.memberaccounting.service.service.impl;

import com.solofunds.memberaccounting.model.TransactionErrorCodeDTO;
import com.solofunds.memberaccounting.service.entities.TransactionErrorCodeCapture;
import com.solofunds.memberaccounting.service.repositories.TransactionErrorCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionErrorCodeServiceImplTest {

    @Mock
    TransactionErrorCodeRepository errorCodeRepository;

    @Mock
    TransactionErrorCodeCapture transactionErrorCodeCapture;

    @Mock
    TransactionErrorCodeDTO transactionErrorCodeDTO;

    @InjectMocks
    TransactionErrorCodeServiceImpl service;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Test
    void addTransactionErrorCode() {
        fixture.givenDtoIsSavedToDB();

        fixture.whenAddTransactionErrorCodeIsCalled();

        fixture.thenVerifySaveTransactionErrorIsCalled();
        fixture.thenVerifyDtoIsNotNull();
    }

    @Test
    void getTransactionErrorCodeByWalletId() {
        fixture.givenRepositoryIsMockedToGetAllTransactionErrorsByWalletId();

        fixture.whenGetAllTransactionErrorsByWalletIdIsCalled();

        fixture.thenVerifySize();
    }

    @Nested
    public class Fixture {

        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

        TransactionErrorCodeDTO actualTransactionErrorCodeDTO;

        List<TransactionErrorCodeDTO> actualTransactionErrors;

        List<TransactionErrorCodeCapture> transactionErrorCodeCaptures = List.of(new TransactionErrorCodeCapture());

        void givenDtoIsSavedToDB() {
            when(errorCodeRepository.save(any(TransactionErrorCodeCapture.class))).thenReturn(transactionErrorCodeCapture);
        }

        void whenAddTransactionErrorCodeIsCalled() {
            actualTransactionErrorCodeDTO = service.addTransactionErrorCode(transactionErrorCodeDTO);
        }

        void thenVerifySaveTransactionErrorIsCalled() {
            verify(errorCodeRepository, times(1)).save(any(TransactionErrorCodeCapture.class));
        }

        void thenVerifyDtoIsNotNull() {
            assertNotNull(actualTransactionErrorCodeDTO);
        }

        void givenRepositoryIsMockedToGetAllTransactionErrorsByWalletId() {
            when(errorCodeRepository.findAllByWalletAccountId(any(UUID.class))).thenReturn(transactionErrorCodeCaptures);
        }

        void whenGetAllTransactionErrorsByWalletIdIsCalled() {
            actualTransactionErrors = service.getTransactionErrorCodeByWalletId(WALLET_ACCOUNT_ID);
        }

        void thenVerifySize() {
            assertEquals(1, actualTransactionErrors.size());
        }
    }
}
