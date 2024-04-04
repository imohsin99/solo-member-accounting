package com.solofunds.memberaccounting.service.service;

import com.solofunds.memberaccounting.model.LedgerEntryDto;
import com.solofunds.memberaccounting.model.LedgerTransactionDto;
import com.solofunds.memberaccounting.service.entities.LedgerEntry;
import com.solofunds.memberaccounting.service.entities.LedgerTransaction;
import com.solofunds.memberaccounting.service.mappers.LedgerMapper;
import com.solofunds.memberaccounting.service.repositories.LedgerRepo;
import com.solofunds.memberaccounting.service.repositories.LedgerTransactionRepo;
import com.solofunds.memberaccounting.service.repositories.LoanLedgerRepo;
import com.solofunds.memberaccounting.service.repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
@SpringBootTest
public class LedgerServiceTest {

    @Mock
    LoanLedgerRepo loanLedgerRepo;
    @Mock
    LedgerRepo ledgerRepo;
    @Mock
    LoanRepository loanRepo;
    @Mock
    LedgerTransactionRepo txnRepo;

    @Mock
    LedgerMapper ledgerMapper;


    @Mock
    LedgerEntryDto ledgerEntryDto;

    @Mock
    LedgerEntryDto expectedLedgerEntryDto;

    @Mock
    LedgerEntry ledgerEntry;


    @Mock
    List<LedgerTransaction> ledgerTransactions;

    @Mock
    LedgerTransactionDto ledgerTransactionsDto;

    @Mock
    List<LedgerTransaction> replacedLedgerTransactions;

    @Mock
    LedgerTransaction ledgerTransaction;
    @Mock
    LedgerEntry savedLedgerEntry;

    @InjectMocks
    LedgerService ledgerService;

    Fixture fixture;

    @BeforeEach
    void setup(){
        fixture = new Fixture();
    }

    @Test
    void testLedgerEntriesByLoanId(){
        fixture.givenFindByLoanLedgerLoanIdIsCalled();
        fixture.givenLedgerEntryIsMappedToDto();
        fixture.whenLedgerEntriesByLoanIdOfServiceIsCalled();
        fixture.thenAssertLedgerEntryDtoListIsNotNul();
    }

    @Test
    void testLedgerEntriesByMemberId(){
        fixture.givenFindBySoloMemberGuidIsCalled();
        fixture.givenLedgerEntryIsMappedToDto();
        fixture.whenLedgerEntriesByMemberIdOfServiceIsCalled();
        fixture.thenAssertLedgerEntryDtoListIsNotNul();
    }


    @Test
    void testCreateLedgerEntry(){
        fixture.givenLedgerEntryDtoIsMappedToEntity();
        fixture.givenSaveOfLedgerRepoIsCalled();
        fixture.givenLedgerEntryTransactionsIsCalled();
        fixture.givenSaveOfLedgerTransactionRepoIsCalled();
        fixture.givenLedgerEntryTransactionsIsCalledSecondTime();
        fixture.givenLedgerDtoIgnoreSetter();
        fixture.givenLedgerTransactionIsMappedToLedgerTransactionDto();
        fixture.givenLedgerEntryIsMappedToDtoNew();
        fixture.whenCreateLedgerEntry();
        fixture.thenAssertLedgerEntryDtoIsNotNull();
    }



    private class Fixture {

        private final UUID LOAN_ID = UUID.randomUUID();

        private List<LedgerEntryDto> expectedLedgerEntryDtoList;
        private LedgerEntryDto expectedLedgerEntryDto;







        public void givenFindByLoanLedgerLoanIdIsCalled(){
            when(ledgerRepo.findByLoanLedgerLoanId(LOAN_ID)).thenReturn(List.of(ledgerEntry));
        }

        public void givenLedgerEntryIsMappedToDto(){
            when( ledgerMapper.toDto(ledgerEntry)).thenReturn(ledgerEntryDto);
        }


        public void whenLedgerEntriesByLoanIdOfServiceIsCalled() {
            expectedLedgerEntryDtoList = ledgerService.ledgerEntriesByLoanId(LOAN_ID);
        }

        public void thenAssertLedgerEntryDtoListIsNotNul() {
            assertNotNull(expectedLedgerEntryDtoList);
        }

        public void givenFindBySoloMemberGuidIsCalled(){
            when(ledgerRepo.findBySoloMemberGuid(LOAN_ID)).thenReturn(List.of(ledgerEntry));
        }

        public void whenLedgerEntriesByMemberIdOfServiceIsCalled() {
            expectedLedgerEntryDtoList = ledgerService.ledgerEntriesByMemberId(LOAN_ID);
        }


        public void givenLedgerEntryDtoIsMappedToEntity(){
            when( ledgerMapper.toEntity(ledgerEntryDto)).thenReturn(ledgerEntry);
        }


        public void givenSaveOfLedgerRepoIsCalled(){
            when(ledgerRepo.save(any(LedgerEntry.class))).thenReturn(savedLedgerEntry);
        }

        public void givenLedgerEntryTransactionsIsCalled(){
            when(savedLedgerEntry.getLedgerTransactions()).thenReturn(ledgerTransactions);
        }
        // Explicitly returning some LedgerTransaction object because LedgerMapperImpl has one protected method ledgerTransactionDtoListToLedgerTransactionList. It needs some object or null
        public void givenLedgerEntryTransactionsIsCalledSecondTime(){
            List<LedgerTransaction> transactions = new ArrayList<>();
            transactions.add(new LedgerTransaction());
            when(savedLedgerEntry.getLedgerTransactions()).thenReturn(transactions);
        }

        public void givenSaveOfLedgerTransactionRepoIsCalled(){
            when(txnRepo.save(any())).thenReturn(ledgerTransaction);
        }

        public void givenLedgerTransactionIsMappedToLedgerTransactionDto(){
            when( ledgerMapper.toTxnDto(ledgerTransaction)).thenReturn(ledgerTransactionsDto);

        }

        public void givenLedgerEntryIsMappedToDtoNew(){
            when( ledgerMapper.toDto(ledgerEntry)).thenReturn(expectedLedgerEntryDto);
        }
        public void givenLedgerDtoIgnoreSetter(){
           doNothing().when(ledgerEntryDto).setLedgerTransactions(any());
        }

        public void whenCreateLedgerEntry() {
            expectedLedgerEntryDto = ledgerService.createLedgerEntry(LOAN_ID,ledgerEntryDto);
        }

        public void thenAssertLedgerEntryDtoIsNotNull() {
            assertNotNull(expectedLedgerEntryDto);
        }





    }
}

