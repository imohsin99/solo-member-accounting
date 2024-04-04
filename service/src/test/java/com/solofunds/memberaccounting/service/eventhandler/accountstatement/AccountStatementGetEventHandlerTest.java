package com.solofunds.memberaccounting.service.eventhandler.accountstatement;

import com.solofunds.memberaccounting.messaging.messenger.event.accountStatement.AccountStatementGetEvent;
import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.model.Format;
import com.solofunds.memberaccounting.model.MemberDataDto;
import com.solofunds.memberaccounting.service.eventhandler.accountStatement.AccountStatementGetEventHandler;
import com.solofunds.memberaccounting.service.service.AccountStatementCSVService;
import com.solofunds.memberaccounting.service.service.AccountStatementPdfGenerator;
import com.solofunds.memberaccounting.service.service.impl.AccountStatementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountStatementGetEventHandlerTest {
    @Mock
    AccountStatementServiceImpl accountStatementService;

    @Mock
    private AccountStatementCSVService statementCsv;

    @Mock
    private AccountStatementPdfGenerator pdfGenerator;

    @Mock
    private Message<AccountStatementGetEvent> accountStatementGetEventMessage;

    @InjectMocks
    private AccountStatementGetEventHandler accountStatementGetEventHandler;

    private Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Test
    void testConsumeGetMessage_JSON() {
        fixture.givenAccountStatementGetEventIsProvided();
        fixture.givenAccountStatementServiceReturnsGetAccountStatement();

        fixture.whenConsumeGetMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturnedForGetEvent();
    }

    @Test
    void consumeGetMessage_CSV() {
        fixture.givenAccountStatementGetEventCSVIsProvided();
        fixture.givenAccountStatementCsvServiceReturnsByteArrayInputStream();

        fixture.whenConsumeGetMessageIsCalled();

        fixture.thenVerifyExpectedCsvResponseIsReturnedForGetEvent();
    }

    @Test
    void consumeGetMessage_PDF() {
        fixture.givenAccountStatementGetEventPDFIsProvided();
        fixture.givenAccountStatementPDFServiceReturnsByteArrayInputStream();

        fixture.whenConsumeGetMessageIsCalled();

        fixture.thenVerifyExpectedPDFResponseIsReturnedForGetEvent();
    }

    private class Fixture {
        final UUID ACCOUNT_STATEMENT_ID = UUID.randomUUID();

        final byte [] csvBytes = "Sample CSV Data".getBytes();

        final ByteArrayInputStream pdfStream = new ByteArrayInputStream("Sample PDF Data".getBytes());

        AccountStatementDto accountStatementDTO;

        @SuppressWarnings("unused")
        MemberDataDto memberDataDTO;

        Message<AccountStatementDto> response;

        public void whenConsumeGetMessageIsCalled() {
            response = accountStatementGetEventHandler.consumeEvent(accountStatementGetEventMessage);
        }

        public void givenAccountStatementGetEventIsProvided(){
            when(accountStatementGetEventMessage.getPayload()).thenReturn(AccountStatementGetEvent
                    .builder()
                    .id(ACCOUNT_STATEMENT_ID)
                    .build());
        }

        public void givenAccountStatementServiceReturnsGetAccountStatement() {
            accountStatementDTO = new AccountStatementDto();
            accountStatementDTO.setId(ACCOUNT_STATEMENT_ID);
            when(accountStatementService.getAccountStatementById(ACCOUNT_STATEMENT_ID)).thenReturn(accountStatementDTO);
        }

        public void thenVerifyExpectedJsonResponseIsReturnedForGetEvent() {
            assertNotNull(response);
            assertEquals(ACCOUNT_STATEMENT_ID, response.getPayload().getId());
        }

        public void givenAccountStatementGetEventCSVIsProvided(){
            when(accountStatementGetEventMessage.getPayload()).thenReturn(AccountStatementGetEvent
                    .builder()
                    .id(ACCOUNT_STATEMENT_ID)
                    .format(Format.CSV)
                    .build());
        }

        public void givenAccountStatementCsvServiceReturnsByteArrayInputStream() {
            when(statementCsv.statementCsv(ACCOUNT_STATEMENT_ID)).thenReturn(csvBytes);
        }

        public void thenVerifyExpectedCsvResponseIsReturnedForGetEvent() {
            assertNotNull(response);
        }

        public void givenAccountStatementPDFServiceReturnsByteArrayInputStream() {
            when(pdfGenerator.statementPdf(ACCOUNT_STATEMENT_ID, memberDataDTO)).thenReturn(pdfStream);
        }


        public void thenVerifyExpectedPDFResponseIsReturnedForGetEvent() {
            assertNotNull(response);
        }

        public void givenAccountStatementGetEventPDFIsProvided(){
            when(accountStatementGetEventMessage.getPayload()).thenReturn(AccountStatementGetEvent
                    .builder()
                    .id(ACCOUNT_STATEMENT_ID)
                    .format(Format.PDF)
                    .memberDataDto(memberDataDTO)
                    .build());
        }
    }
}
