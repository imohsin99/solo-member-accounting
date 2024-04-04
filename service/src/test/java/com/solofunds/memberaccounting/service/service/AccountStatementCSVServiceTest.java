package com.solofunds.memberaccounting.service.service;

import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.model.MemberTransactionDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountStatementCSVServiceTest {

    @Mock
    private AccountStatementService statementService;

    @InjectMocks
    private AccountStatementCSVService csvService;


    @Test
    void testStatementCsvGeneration() throws Exception {
        UUID accountStatementId = UUID.randomUUID();
        AccountStatementDto mockStatementDto = createMockStatementDtoWithTransactions();
        when(statementService.getAccountStatementById(any(UUID.class))).thenReturn(mockStatementDto);

        byte[] csvData = csvService.statementCsv(accountStatementId);

        List<String> csvLines = parseCsvData(csvData);

        String expectedHeader = "member_transaction_id,wallet_account_id,solo_member_GUID,loan_id," +
                "ledger_transaction_id,payment_order_id,amount,currency_code,currency_exponent,type,category," +
                "direction,description,detail_text,status,transaction_date,posting_date,created_at,updated_at";
        assertThat(csvLines.get(0)).isEqualTo(expectedHeader);

        String[] expectedTransactionFields = {
                mockStatementDto.getMemberTransactions().iterator().next().getId().toString(),
                null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null
        };
        assertThat(csvLines.get(1)).isEqualTo(String.join(",", expectedTransactionFields));

        verify(statementService, times(1)).getAccountStatementById(accountStatementId);
        assertNotNull(csvData);
    }

    @Test
    void testStatementCSVGeneration_Exception(){
        assertThrows(RuntimeException.class, () ->  csvService.statementCsv(null));
    }

    private AccountStatementDto createMockStatementDtoWithTransactions() {
        AccountStatementDto mockDto = new AccountStatementDto();
        mockDto.setId(UUID.randomUUID());

        List<MemberTransactionDto> memberTransactionsList = new ArrayList<>();
        MemberTransactionDto transaction1 = new MemberTransactionDto();
        transaction1.setId(UUID.randomUUID());
        memberTransactionsList.add(transaction1);

        MemberTransactionDto transaction2 = new MemberTransactionDto();
        transaction2.setId(UUID.randomUUID());
        memberTransactionsList.add(transaction2);

        mockDto.setMemberTransactions(memberTransactionsList);

        return mockDto;
    }

    private List<String> parseCsvData(byte[] csvData) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData);
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        CsvListReader csvReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);
        List<String> lines = new ArrayList<>();
        List<String> line;
        while ((line = csvReader.read()) != null) {
            lines.add(String.join(",", line));
        }
        return lines;
    }
}
