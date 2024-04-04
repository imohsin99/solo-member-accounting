package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.accountStatement.*;
import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.model.CreateAccountStatementDto;
import com.solofunds.memberaccounting.model.Format;
import com.solofunds.memberaccounting.model.MemberDataDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountStatementDelegateImplTest {

    @Mock
    EventFactory mockEventFactory;

    @MockBean
    MessagePublisher<Event> mockPublisher;

    @InjectMocks
    private AccountStatementDelegateImpl accountStatementDelegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetAccountStatementById_JsonFormat() throws Exception {
        UUID statementId = UUID.randomUUID();
        AccountStatementDto expectedStatementDTO = new AccountStatementDto();
        expectedStatementDTO.setId(statementId);

        AccountStatementGetEvent accountStatementGetEvent = AccountStatementGetEvent.builder().id(statementId).publisher(mockPublisher).build();
        when(mockEventFactory.buildAccountStatementGetEvent(any(UUID.class), isNull(), isNull())).thenReturn(accountStatementGetEvent);

        when(mockPublisher.publishAndReceive(any(AccountStatementGetEvent.class), any(String.class), any())).thenReturn(expectedStatementDTO);

        ResponseEntity<Object> response = accountStatementDelegate.getAccountStatementById(statementId, null, null);

        verify(mockPublisher, times(1)).publishAndReceive(any(AccountStatementGetEvent.class), any(String.class), any());

        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(expectedStatementDTO, response.getBody());
    }

    @Test
    void testGetAccountStatementById_PDFFormat() throws Exception {
        UUID statementId = UUID.randomUUID();
        MemberDataDto memberDataDTO = new MemberDataDto();
        AccountStatementDto expectedAccountStatementDto = new AccountStatementDto();
        expectedAccountStatementDto.setDocument("Sample PDF Data".getBytes());

        AccountStatementGetEvent accountStatementGetEvent = AccountStatementGetEvent.builder().id(statementId).memberDataDto(memberDataDTO).publisher(mockPublisher).build();
        when(mockEventFactory.buildAccountStatementGetEvent(any(UUID.class), any(MemberDataDto.class), any(Format.class))).thenReturn(accountStatementGetEvent);

        when(mockPublisher.publishAndReceive(any(AccountStatementGetEvent.class), any(String.class), any())).thenReturn(expectedAccountStatementDto);

        ResponseEntity<Object> response = accountStatementDelegate.getAccountStatementById(statementId, Format.PDF, memberDataDTO);

        verify(mockPublisher, times(1)).publishAndReceive(any(AccountStatementGetEvent.class), any(String.class), any());

        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("inline; filename=statement.pdf", response.getHeaders().get("Content-Disposition").get(0));
    }

    @Test
    void testGetAccountStatementById_CSVFormat() throws Exception {
        UUID statementId = UUID.randomUUID();
        MemberDataDto memberDataDTO = new MemberDataDto();
        AccountStatementDto expectedAccountStatementDto = new AccountStatementDto();
        expectedAccountStatementDto.setDocument("Sample CSV Data".getBytes());

        AccountStatementGetEvent accountStatementGetEvent = AccountStatementGetEvent.builder().id(statementId).memberDataDto(memberDataDTO).publisher(mockPublisher).build();
        when(mockEventFactory.buildAccountStatementGetEvent(any(UUID.class), any(MemberDataDto.class), any(Format.class))).thenReturn(accountStatementGetEvent);

        when(mockPublisher.publishAndReceive(any(AccountStatementGetEvent.class), any(String.class), any())).thenReturn(expectedAccountStatementDto);

        ResponseEntity<Object> response = accountStatementDelegate.getAccountStatementById(statementId, Format.CSV, memberDataDTO);

        verify(mockPublisher, times(1)).publishAndReceive(any(AccountStatementGetEvent.class), any(String.class), any());
        MediaType csvMediaType = MediaType.parseMediaType("application/csv");
        assertEquals(csvMediaType, response.getHeaders().getContentType());
        assertEquals("inline; filename=statement.csv", response.getHeaders().get("Content-Disposition").get(0));
    }

    @Test
    void testGetAccountStatementById_PDFMissingMemberData() {
        UUID statementId = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> accountStatementDelegate.getAccountStatementById(statementId, Format.CSV, null));
    }

   // @Test
    void createAccountStatement() throws Exception {
        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        CreateAccountStatementDto createAccountStatementDto = new CreateAccountStatementDto(startDate, endDate);

        AccountStatementDto expectedStatementDto = new AccountStatementDto();
        expectedStatementDto.setWalletAccountId(WALLET_ACCOUNT_ID);

        AccountStatementCreateEvent event = AccountStatementCreateEvent.builder().walletAccountId(WALLET_ACCOUNT_ID).createAccountStatementDto(createAccountStatementDto).build();
        event.setPublisher(mockPublisher);
        when(mockEventFactory.buildAccountStatementCreateEvent(any(UUID.class), any(CreateAccountStatementDto.class))).thenReturn(event);

        when(mockPublisher.publishAndWait(any(AccountStatementCreateEvent.class), any(String.class))).thenReturn(objectMapper.writeValueAsString(expectedStatementDto));

        ResponseEntity<AccountStatementDto> response = accountStatementDelegate.createAccountStatement(WALLET_ACCOUNT_ID, createAccountStatementDto);

        verify(mockPublisher, times(1)).publishAndWait(any(AccountStatementCreateEvent.class), anyString());
        assertEquals(WALLET_ACCOUNT_ID, response.getBody().getWalletAccountId());
    }

    @Test
    void createAccountStatement_NullCreateStatementDto(){
        assertThrows(RuntimeException.class, () -> accountStatementDelegate.createAccountStatement(any(UUID.class), null));
    }

    //@Test
    void deleteAccountStatementById() throws Exception {
        UUID ACCOUNT_STATEMENT_ID = UUID.randomUUID();

        AccountStatementDto expectedStatementDto = new AccountStatementDto();
        expectedStatementDto.setId(ACCOUNT_STATEMENT_ID);

        AccountStatementDeleteEvent event = new AccountStatementDeleteEvent();
        event.setId(ACCOUNT_STATEMENT_ID);
        event.setPublisher(mockPublisher);

        when(mockEventFactory.buildAccountStatementDeleteEvent(any(UUID.class))).thenReturn(event);

        when(mockPublisher.publishAndWait(any(AccountStatementDeleteEvent.class), anyString())).thenReturn(objectMapper.writeValueAsString(expectedStatementDto));

        ResponseEntity<AccountStatementDto> response = accountStatementDelegate.deleteAccountStatementById(ACCOUNT_STATEMENT_ID);

        verify(mockPublisher, times(1)).publishAndWait(any(AccountStatementDeleteEvent.class), anyString());
        assertEquals(ACCOUNT_STATEMENT_ID, response.getBody().getId());
    }

    @Test
    void deleteAccountStatementById_NullId(){
        assertThrows(RuntimeException.class, () -> accountStatementDelegate.deleteAccountStatementById(null));
    }

    //@Test
    void getAllAccountStatements() throws Exception {
        AccountStatementGetAllEvent event =  new AccountStatementGetAllEvent();
        event.setPublisher(mockPublisher);

        List<AccountStatementDto> statementDtos = new ArrayList<>();
        statementDtos.add(new AccountStatementDto());

        when(mockEventFactory.buildAccountStatementGetAllEvent()).thenReturn(event);

        when(mockPublisher.publishAndWait(any(AccountStatementGetAllEvent.class), anyString())).thenReturn(objectMapper.writeValueAsString(statementDtos));

        ResponseEntity<List<AccountStatementDto>> response = accountStatementDelegate.getAllAccountStatements();

        verify(mockPublisher, times(1)).publishAndWait(any(AccountStatementGetAllEvent.class), anyString());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllAccountStatements_NullPublisher(){
        when(mockEventFactory.buildAccountStatementGetAllEvent()).thenReturn(null);
        assertThrows(RuntimeException.class, () -> accountStatementDelegate.getAllAccountStatements());
    }

    @Test
    void getAllAccountStatementsByWalletAccountId() throws Exception {
        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

        AccountStatementGetByWalletIdEvent event = new AccountStatementGetByWalletIdEvent();
        event.setWalletAccountId(WALLET_ACCOUNT_ID);
        event.setPublisher(mockPublisher);

        AccountStatementDto statementDto1 = new AccountStatementDto();
        statementDto1.setWalletAccountId(WALLET_ACCOUNT_ID);
        List<AccountStatementDto> statementDtos = new ArrayList<>();
        statementDtos.add(statementDto1);

        when(mockEventFactory.buildAccountStatementGetByWalletIdEvent(any(UUID.class))).thenReturn(event);

        when(mockPublisher.publishAndReceive(any(AccountStatementGetByWalletIdEvent.class), anyString(), any())).thenReturn(statementDtos);

        ResponseEntity<List<AccountStatementDto>> response = accountStatementDelegate.getAllAccountStatementsByWalletAccountId(WALLET_ACCOUNT_ID);

        verify(mockPublisher, times(1)).publishAndReceive(any(AccountStatementGetByWalletIdEvent.class), anyString(), any());
        assertEquals(1, response.getBody().size());
        assertEquals(WALLET_ACCOUNT_ID, response.getBody().get(0).getWalletAccountId());
    }

    @Test
    void getAllAccountStatementsByWalletAccountId_NullWalletAccountId(){
        assertThrows(RuntimeException.class, () -> accountStatementDelegate.getAllAccountStatementsByWalletAccountId(null));
    }
}
