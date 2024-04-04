package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanGetAllByMemberIdAndStatusEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanPatchEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateLoanDto;
import com.solofunds.memberaccounting.model.LoanDto;
import com.solofunds.memberaccounting.model.LoanStatus;
import com.solofunds.memberaccounting.model.UpdateLoanDto;
import com.solofunds.memberaccounting.service.service.LoanService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoanEventHandlerTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private LoanDto loanDTO;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanEventHandler loanEventHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void consumeCreateMessage() throws JsonProcessingException {

        UUID loanRequestUUID = UUID.randomUUID();
        UUID fundingProposalUUID = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();
        UUID borrowerWalletAccountId = UUID.randomUUID();
        UUID lenderWalletAccountId = UUID.randomUUID();

        CreateLoanDto loanCreateRequest = new CreateLoanDto(loanRequestUUID, fundingProposalUUID, borrowerWalletAccountId, lenderWalletAccountId);

        LoanCreateEvent loanCreateEvent = LoanCreateEvent.builder().createLoanDto(loanCreateRequest).build();
        String payloadJson = objectMapper.writeValueAsString(loanCreateEvent);
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payloadJson);
        when(mapper.readValue(payloadJson, LoanCreateEvent.class)).thenReturn(loanCreateEvent);

        LoanDto loanDTO = new LoanDto();
        loanDTO.setId(loanId);
        loanDTO.setLoanRequestId(loanRequestUUID);
        loanDTO.acceptedLoanProposalId(fundingProposalUUID);
        String json = objectMapper.writeValueAsString(loanDTO);
        when(mapper.writeValueAsString(loanDTO)).thenReturn(json);
        when(loanService.createLoan(loanCreateRequest)).thenReturn(loanDTO);

        String result = loanEventHandler.consumeCreateMessage(message);

        verify(loanService, times(1)).createLoan(loanCreateRequest);
        verify(message, times(1)).getPayload();

        assertEquals(json, result);
    }

    @Test
    void testConsumeCreateMessage_JsonProcessingException() throws Exception {
        // Mock the Message object and its payload to simulate a JSON processing exception
        String invalidPayloadJson = "Invalid JSON Payload";
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(invalidPayloadJson);
        when(mapper.readValue(invalidPayloadJson, LoanCreateEvent.class)).thenThrow(JsonProcessingException.class);

        // Call the method and verify that it throws a RuntimeException
        assertThrows(RuntimeException.class, () -> loanEventHandler.consumeCreateMessage(message));

        // Verify the interactions
        verify(loanService, never()).createLoan(any(CreateLoanDto.class)); // We should not call the service if there's a JSON processing exception.
        verify(message, times(1)).getPayload();
    }

    @Test
    void consumeGetMessage() throws JsonProcessingException {
        UUID loanId = UUID.randomUUID();

        ResourceByIdGetEvent getEvent = ResourceByIdGetEvent.builder().id(loanId).build();
        String payloadJson = objectMapper.writeValueAsString(getEvent);

        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payloadJson);
        when(mapper.readValue(payloadJson, ResourceByIdGetEvent.class)).thenReturn(getEvent);

        LoanDto loanDTO = new LoanDto();
        loanDTO.setId(loanId);
        String json = objectMapper.writeValueAsString(loanDTO);
        when(mapper.writeValueAsString(loanDTO)).thenReturn(json);
        when(loanService.getLoanById(loanId)).thenReturn(loanDTO);

        String result = loanEventHandler.consumeGetMessage(message);

        verify(loanService, times(1)).getLoanById(loanId);
        verify(message, times(1)).getPayload();

        assertEquals(json, result);
    }

    @Test
    void testConsumeGetMessage_JsonProcessingException() throws Exception {
        // Mock the Message object and its payload to simulate a JSON processing exception
        String invalidPayloadJson = "Invalid JSON Payload";
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(invalidPayloadJson);
        when(mapper.readValue(invalidPayloadJson, ResourceByIdGetEvent.class)).thenThrow(JsonProcessingException.class);

        // Call the method and verify that it throws a RuntimeException
        assertThrows(RuntimeException.class, () -> loanEventHandler.consumeGetMessage(message));

        // Verify the interactions
        verify(loanService, never()).getLoanById(any(UUID.class)); // We should not call the service if there's a JSON processing exception.
        verify(message, times(1)).getPayload();
    }

    @Test
    void consumeGetAllMessage() throws JsonProcessingException {
        UUID soloMemberId = UUID.randomUUID();

        LoanGetAllByMemberIdAndStatusEvent event = LoanGetAllByMemberIdAndStatusEvent.builder()
                .id(soloMemberId)
                .build();
        String payloadJson = objectMapper.writeValueAsString(event);

        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payloadJson);
        when(mapper.readValue(payloadJson, LoanGetAllByMemberIdAndStatusEvent.class)).thenReturn(event);

        List<LoanDto> loanDTOS = List.of(loanDTO, loanDTO);
        String json = objectMapper.writeValueAsString(loanDTOS);
        when(mapper.writeValueAsString(loanDTOS)).thenReturn(json);
        when(loanService.getLoansByMemberId(soloMemberId)).thenReturn(loanDTOS);

        String result = loanEventHandler.consumeGetAllMessage(message);

        verify(loanService, times(1)).getLoansByMemberId(soloMemberId);
        verify(message, times(1)).getPayload();

        assertEquals(json, result);
    }

    @Test
    void consumeGetAllMessage_WithLoanStatus() throws JsonProcessingException {
        UUID soloMemberId = UUID.randomUUID();

        LoanGetAllByMemberIdAndStatusEvent event = LoanGetAllByMemberIdAndStatusEvent.builder()
                .id(soloMemberId)
                .status(LoanStatus.COLLECTIONS)
                .build();
        String payloadJson = objectMapper.writeValueAsString(event);

        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payloadJson);
        when(mapper.readValue(payloadJson, LoanGetAllByMemberIdAndStatusEvent.class)).thenReturn(event);

        List<LoanDto> loanDTOS = List.of(loanDTO, loanDTO);
        String json = objectMapper.writeValueAsString(loanDTOS);
        when(mapper.writeValueAsString(loanDTOS)).thenReturn(json);
        when(loanService.getLoansByLoanStatusAndMemberId(soloMemberId, com.solofunds.memberaccounting.service.enums.LoanStatus.valueOf(LoanStatus.COLLECTIONS.toString()))).thenReturn(loanDTOS);

        String result = loanEventHandler.consumeGetAllMessage(message);

        verify(loanService, times(1)).getLoansByLoanStatusAndMemberId(soloMemberId,com.solofunds.memberaccounting.service.enums.LoanStatus.valueOf(LoanStatus.COLLECTIONS.toString()));
        verify(message, times(1)).getPayload();

        assertEquals(json, result);
    }

    @Test
    void testConsumeGetAllMessage_JsonProcessingException() throws Exception {
        // Mock the Message object and its payload to simulate a JSON processing exception
        String invalidPayloadJson = "Invalid JSON Payload";
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(invalidPayloadJson);
        when(mapper.readValue(invalidPayloadJson, ResourceByIdGetEvent.class)).thenThrow(JsonProcessingException.class);

        // Call the method and verify that it throws a RuntimeException
        assertThrows(RuntimeException.class, () -> loanEventHandler.consumeGetAllMessage(message));

        // Verify the interactions
        verify(loanService, never()).getLoansByMemberId(any(UUID.class)); // We should not call the service if there's a JSON processing exception.
        verify(message, times(1)).getPayload();
    }

    @Test
    void consumeUpdateMessage() throws JsonProcessingException {
        UUID loanId = UUID.randomUUID();

        UpdateLoanDto updateLoanDTO = new UpdateLoanDto();
        LoanPatchEvent loanPatchEvent = LoanPatchEvent.builder().id(loanId).updateLoanDTO(updateLoanDTO).build();
        String payloadJson = objectMapper.writeValueAsString(loanPatchEvent);
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payloadJson);
        when(mapper.readValue(payloadJson, LoanPatchEvent.class)).thenReturn(loanPatchEvent);

        LoanDto loanDTO = new LoanDto();
        loanDTO.setId(loanId);
        String json = objectMapper.writeValueAsString(loanDTO);
        when(mapper.writeValueAsString(loanDTO)).thenReturn(json);
        when(loanService.updateLoan(loanId, updateLoanDTO)).thenReturn(loanDTO);

        String result = loanEventHandler.consumeUpdateMessage(message);

        verify(loanService, times(1)).updateLoan(loanId, updateLoanDTO);
        verify(message, times(1)).getPayload();

        assertEquals(json, result);
    }

    @Test
    void testConsumeUpdateMessage_JsonProcessingException() throws Exception {
        // Mock the Message object and its payload to simulate a JSON processing exception
        String invalidPayloadJson = "Invalid JSON Payload";
        Message<String> message = mock(Message.class);
        when(message.getPayload()).thenReturn(invalidPayloadJson);
        when(mapper.readValue(invalidPayloadJson, LoanPatchEvent.class)).thenThrow(JsonProcessingException.class);

        // Call the method and verify that it throws a RuntimeException
        assertThrows(RuntimeException.class, () -> loanEventHandler.consumeUpdateMessage(message));

        // Verify the interactions
        verify(loanService, never()).updateLoan(any(UUID.class), any(UpdateLoanDto.class)); // We should not call the service if there's a JSON processing exception.
        verify(message, times(1)).getPayload();
    }
}