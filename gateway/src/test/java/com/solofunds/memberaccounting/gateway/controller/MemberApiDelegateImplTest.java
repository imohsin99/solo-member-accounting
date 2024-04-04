package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanGetAllByMemberIdAndStatusEvent;
import com.solofunds.memberaccounting.model.LoanDto;
import com.solofunds.memberaccounting.model.LoanStatus;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberApiDelegateImplTest {

    @Mock
    MessagePublisher mockPublisher;

    @Mock
    EventFactory mockEventFactory;

    @InjectMocks
    private MemberApiDelegateImpl memberApiDelegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllLoansByMemberId() throws Exception {
        UUID soloMemberId = UUID.randomUUID();

        LoanDto loanDTO = new LoanDto();
        loanDTO.setId(UUID.randomUUID());

        List<LoanDto> loanDTOS = List.of(loanDTO, loanDTO);

        LoanGetAllByMemberIdAndStatusEvent loansByMemberIdGetEvent = LoanGetAllByMemberIdAndStatusEvent.builder()
                        .publisher(mockPublisher)
                        .id(soloMemberId)
                        .build();

        when(mockEventFactory.buildLoansByMemberAccountIdGetEvent(any(UUID.class),any())).thenReturn(loansByMemberIdGetEvent);

        when(mockPublisher
                .publishAndWait(any(LoanGetAllByMemberIdAndStatusEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(loanDTOS));

        ResponseEntity<List<LoanDto>> response = memberApiDelegate.getAllLoansByMemberId(soloMemberId,null);

        verify(mockPublisher, times(1)).publishAndWait(any(LoanGetAllByMemberIdAndStatusEvent.class), any(String.class));

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllLoansByMemberIdAndLoanStatus() throws Exception {
        UUID soloMemberId = UUID.randomUUID();

        LoanDto loanDto = new LoanDto();
        loanDto.setId(UUID.randomUUID());
        loanDto.setStatus(LoanStatus.COLLECTIONS);

        List<LoanDto> loanDTOS = List.of(loanDto, loanDto);

        LoanGetAllByMemberIdAndStatusEvent loansByMemberIdGetEvent = LoanGetAllByMemberIdAndStatusEvent.builder()
                .publisher(mockPublisher)
                .id(soloMemberId)
                .status(LoanStatus.COLLECTIONS)
                .build();

        when(mockEventFactory.buildLoansByMemberAccountIdGetEvent(any(UUID.class),any())).thenReturn(loansByMemberIdGetEvent);

        when(mockPublisher
                .publishAndWait(any(LoanGetAllByMemberIdAndStatusEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(loanDTOS));

        ResponseEntity<List<LoanDto>> response = memberApiDelegate.getAllLoansByMemberId(soloMemberId,LoanStatus.COLLECTIONS);

        verify(mockPublisher, times(1)).publishAndWait(any(LoanGetAllByMemberIdAndStatusEvent.class), any(String.class));

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(LoanStatus.COLLECTIONS,response.getBody().get(0).getStatus());
    }

    @Test
    void getAllLoansByMemberId_invalidMemberUUID(){
        assertThrows(RuntimeException.class,
                () -> memberApiDelegate.getAllLoansByMemberId(null,null)
        );
    }
}