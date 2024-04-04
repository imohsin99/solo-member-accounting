package com.solofunds.memberaccounting.service.integrationTest;

import com.solofunds.memberaccounting.model.LedgerEntryDto;
import com.solofunds.memberaccounting.model.LedgerTransactionDto;
import com.solofunds.memberaccounting.service.config.DatabaseSetupExtension;
import com.solofunds.memberaccounting.service.entities.Loan;
import com.solofunds.memberaccounting.service.repositories.LoanRepository;
import com.solofunds.memberaccounting.service.service.LedgerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.UUID;

@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(DatabaseSetupExtension.class)
public class LedgerServiceIT {

    @Autowired
    LedgerService ledgerService;
    @Autowired
    LoanRepository loanRepo;

    @BeforeEach
    void beforeAll() {
        Loan l = new Loan();
        l.setCurrencyCode("TEST");
        Loan savedLoan = loanRepo.save(l);
        sampleLoanId = savedLoan.getId();
    }

    LedgerEntryDto sampleLedgerEntryDto() {
            LedgerEntryDto le = new LedgerEntryDto();
            le.setSoloMemberGuid(sampleMemberGuid);
            var txns = new ArrayList<LedgerTransactionDto>(2);
            txns.add(new LedgerTransactionDto());
            le.setLedgerTransactions(txns);
            return le;
    }

    UUID sampleLoanId;
    final UUID sampleMemberGuid = UUID.randomUUID();

    @Test
    void testLedgerService() {
        LedgerEntryDto newLedgerEntry = ledgerService.createLedgerEntry(sampleLoanId, sampleLedgerEntryDto());
        LedgerEntryDto byLoanId = ledgerService.ledgerEntriesByLoanId(sampleLoanId).get(0);
        LedgerEntryDto byMemberId = ledgerService.ledgerEntriesByMemberId(sampleMemberGuid).get(0);

        validateLookedUpLedgerEntry(newLedgerEntry, byLoanId);
        validateLookedUpLedgerEntry(newLedgerEntry, byMemberId);
    }

    private static void validateLookedUpLedgerEntry(LedgerEntryDto reference, LedgerEntryDto lookedUp) {
        assert (reference.getId() .equals (lookedUp.getId()));
        assert (reference.getLedgerTransactions().get(0).getId()
                .equals
                        (lookedUp.getLedgerTransactions().get(0).getId()));
    }
}
