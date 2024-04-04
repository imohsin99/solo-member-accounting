package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerByLoanIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerByMemberIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerEntryCreateEvent;
import com.solofunds.memberaccounting.model.LedgerEntryDto;
import com.solofunds.memberaccounting.model.Status;
import com.solofunds.memberaccounting.service.service.LedgerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
public class LedgerEntryEventHandlerTest {


    @Mock
    private ObjectMapper mapper;

    @Mock
    private LedgerService ledgerService;

    @Mock
    private LedgerEntryDto ledgerEntryDto;

    @InjectMocks
    private LedgerEntryEventHandler ledgerEntryEventHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void consumeCreateMessage() throws JsonProcessingException {

        UUID loanId = UUID.randomUUID();
        UUID LOAN_LEDGER_ID = UUID.randomUUID();
        UUID SOLO_MEMBER_ID = UUID.randomUUID();
        UUID MEMBER_TRANSACTION_ID = UUID.randomUUID();

        LedgerEntryDto ledgerEntryDto = new LedgerEntryDto(UUID.randomUUID(),null);

        ledgerEntryDto.setLoanLedgerId(LOAN_LEDGER_ID);
        ledgerEntryDto.soloMemberGuid(SOLO_MEMBER_ID);
        ledgerEntryDto.setMemberTransactionIds(List.of(MEMBER_TRANSACTION_ID));
        ledgerEntryDto.setStatus(Status.PENDING);
        LedgerEntryCreateEvent ledgerEntryCreateEvent = LedgerEntryCreateEvent.builder()
                .loan_id(loanId)
                .ledgerEntryToCreate(ledgerEntryDto)
                .build();
        String payloadJson = objectMapper.writeValueAsString(ledgerEntryCreateEvent);
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payloadJson);
        when(mapper.readValue(payloadJson, LedgerEntryCreateEvent.class)).thenReturn(ledgerEntryCreateEvent);

        String json = objectMapper.writeValueAsString(ledgerEntryDto);
        when(mapper.writeValueAsString(ledgerEntryDto)).thenReturn(json);
        when(ledgerService.createLedgerEntry(loanId,ledgerEntryDto)).thenReturn(ledgerEntryDto);
        String result = ledgerEntryEventHandler.consumeCreateMessage(message);

        verify(ledgerService, times(1)).createLedgerEntry(loanId,ledgerEntryDto);
        verify(message, times(1)).getPayload();

        assertEquals(json, result);

    }

    @Test
    void testConsumeCreateMessage_JsonProcessingException() throws Exception {
        // Mock the Message object and its payload to simulate a JSON processing exception
        String invalidPayloadJson = "Invalid JSON Payload";
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(invalidPayloadJson);
        when(mapper.readValue(invalidPayloadJson, LedgerEntryCreateEvent.class)).thenThrow(JsonProcessingException.class);

        // Call the method and verify that it throws a RuntimeException
        assertThrows(RuntimeException.class, () -> ledgerEntryEventHandler.consumeCreateMessage(message));

        // Verify the interactions
        verify(ledgerService, never()).createLedgerEntry(any(UUID.class),any(LedgerEntryDto.class)); // We should not call the service if there's a JSON processing exception.
        verify(message, times(1)).getPayload();
    }

    @Test
    void consumeGetAllByLoanId() throws JsonProcessingException {
        UUID loanId = UUID.randomUUID();

        LedgerByLoanIdGetEvent event = LedgerByLoanIdGetEvent.builder()
                .loan_id(loanId)
                .build();
        String payloadJson = objectMapper.writeValueAsString(event);

        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payloadJson);
        when(mapper.readValue(payloadJson, LedgerByLoanIdGetEvent.class)).thenReturn(event);

        List<LedgerEntryDto> ledgerEntryDtos = List.of(ledgerEntryDto, ledgerEntryDto);
        String json = objectMapper.writeValueAsString(ledgerEntryDtos);
        when(mapper.writeValueAsString(ledgerEntryDtos)).thenReturn(json);
        when(ledgerService.ledgerEntriesByLoanId(loanId)).thenReturn(ledgerEntryDtos);

        String result = ledgerEntryEventHandler.consumeGetAllByLoanId(message);

        verify(ledgerService, times(1)).ledgerEntriesByLoanId(loanId);
        verify(message, times(1)).getPayload();
        assertEquals(json, result);

    }

    @Test
    void testConsumeGetAllByLoanId_JsonProcessingException() throws Exception {
        // Mock the Message object and its payload to simulate a JSON processing exception
        String invalidPayloadJson = "Invalid JSON Payload";
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(invalidPayloadJson);
        when(mapper.readValue(invalidPayloadJson, LedgerByLoanIdGetEvent.class)).thenThrow(JsonProcessingException.class);

        // Call the method and verify that it throws a RuntimeException
        assertThrows(RuntimeException.class, () -> ledgerEntryEventHandler.consumeGetAllByLoanId(message));

        // Verify the interactions
        verify(ledgerService, never()).ledgerEntriesByLoanId(any(UUID.class)); // We should not call the service if there's a JSON processing exception.
        verify(message, times(1)).getPayload();
    }

    @Test
    void consumeGetAllByMemberId() throws JsonProcessingException {
        UUID memberId = UUID.randomUUID();

        LedgerByMemberIdGetEvent event = LedgerByMemberIdGetEvent.builder()
                .memberId(memberId)
                .build();
        String payloadJson = objectMapper.writeValueAsString(event);

        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payloadJson);
        when(mapper.readValue(payloadJson, LedgerByMemberIdGetEvent.class)).thenReturn(event);

        List<LedgerEntryDto> ledgerEntryDtos = List.of(ledgerEntryDto, ledgerEntryDto);
        String json = objectMapper.writeValueAsString(ledgerEntryDtos);
        when(mapper.writeValueAsString(ledgerEntryDtos)).thenReturn(json);
        when(ledgerService.ledgerEntriesByMemberId(memberId)).thenReturn(ledgerEntryDtos);

        String result = ledgerEntryEventHandler.consumeGetAllByMemberId(message);

        verify(ledgerService, times(1)).ledgerEntriesByMemberId(memberId);
        verify(message, times(1)).getPayload();
        assertEquals(json, result);

    }


    @Test
    void testConsumeGetAllByMemberId_JsonProcessingException() throws Exception {
        // Mock the Message object and its payload to simulate a JSON processing exception
        String invalidPayloadJson = "Invalid JSON Payload";
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(invalidPayloadJson);
        when(mapper.readValue(invalidPayloadJson, LedgerByMemberIdGetEvent.class)).thenThrow(JsonProcessingException.class);

        // Call the method and verify that it throws a RuntimeException
        assertThrows(RuntimeException.class, () -> ledgerEntryEventHandler.consumeGetAllByMemberId(message));

        // Verify the interactions
        verify(ledgerService, never()).ledgerEntriesByMemberId(any(UUID.class)); // We should not call the service if there's a JSON processing exception.
        verify(message, times(1)).getPayload();
    }



}
