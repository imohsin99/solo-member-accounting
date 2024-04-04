package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.memberTransaction.MemberTransactionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateMemberTransactionDto;
import com.solofunds.memberaccounting.model.MemberTransactionDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class MemberTransactionDelegateImplTest {

    @Mock
    MessagePublisher mockPublisher;

    @Mock
    EventFactory mockEventFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    MemberTransactionDelegateImpl memberTransactionDelegate;

    @Test
    void getTransactionByTransactionId() throws Exception {
        UUID MEMBER_TRANSACTION_ID = UUID.randomUUID();
        MemberTransactionDto memberTransactionDto = new MemberTransactionDto();
        memberTransactionDto.setId(MEMBER_TRANSACTION_ID);

        ResourceByIdGetEvent event = ResourceByIdGetEvent.builder()
                .id(MEMBER_TRANSACTION_ID)
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.MEMBER_TRANSACTION_GET_TOPIC)
                .build();
        when(mockEventFactory.buildMemberTransactionGetEvent(any(UUID.class))).thenReturn(event);

        when(mockPublisher.publishAndWait(any(ResourceByIdGetEvent.class), anyString())).thenReturn(objectMapper.writeValueAsString(memberTransactionDto));

        ResponseEntity<MemberTransactionDto> response = memberTransactionDelegate.getTransactionByTransactionId(MEMBER_TRANSACTION_ID);

        verify(mockPublisher, times(1)).publishAndWait(any(ResourceByIdGetEvent.class), anyString());

        assertEquals(MEMBER_TRANSACTION_ID, response.getBody().getId());
        assertNotNull(response.getBody());
    }

    @Test
    void getTransactionByTransactionId_NullId() {
        assertThrows(RuntimeException.class, () -> memberTransactionDelegate.getTransactionByTransactionId(null));
    }

    @Test
    void createMemberTransaction() throws Exception {
        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();
        UUID MEMBER_TRANSACTION_ID = UUID.randomUUID();
        UUID LOAN_ID = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(1000);

        CreateMemberTransactionDto createMemberTransactionDto = new CreateMemberTransactionDto();
        createMemberTransactionDto.setWalletAccountId(WALLET_ACCOUNT_ID);
        createMemberTransactionDto.setLoanId(LOAN_ID);
        createMemberTransactionDto.setAmount(amount);

        MemberTransactionDto savedMemberTransactionDto = new MemberTransactionDto();
        savedMemberTransactionDto.setId(MEMBER_TRANSACTION_ID);
        savedMemberTransactionDto.setWalletAccountId(WALLET_ACCOUNT_ID);
        savedMemberTransactionDto.setLoanId(LOAN_ID);
        savedMemberTransactionDto.setAmount(amount);

        MemberTransactionCreateEvent event = MemberTransactionCreateEvent.builder()
                .publisher(mockPublisher)
                .createMemberTransactionDto(createMemberTransactionDto)
                .build();

        when(mockEventFactory.buildMemberTransactionCreateEvent(any(CreateMemberTransactionDto.class))).thenReturn(event);

        when(mockPublisher.publishAndWait(any(MemberTransactionCreateEvent.class), anyString())).thenReturn(objectMapper.writeValueAsString(savedMemberTransactionDto));

        ResponseEntity<MemberTransactionDto> response = memberTransactionDelegate.createMemberTransaction(createMemberTransactionDto);

        verify(mockPublisher, times(1)).publishAndWait(any(MemberTransactionCreateEvent.class), anyString());

        assertNotNull(response.getBody());
        assertEquals(MEMBER_TRANSACTION_ID, response.getBody().getId());
        assertEquals(WALLET_ACCOUNT_ID, response.getBody().getWalletAccountId());
        assertEquals(amount, response.getBody().getAmount());
        assertEquals(LOAN_ID, response.getBody().getLoanId());
    }

    @Test
    void createMemberTransaction_ExceptionCase(){
        assertThrows(RuntimeException.class, () -> memberTransactionDelegate.createMemberTransaction(null));
    }
}
