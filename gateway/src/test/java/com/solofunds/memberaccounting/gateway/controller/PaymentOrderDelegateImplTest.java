package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.paymentOrder.PaymentOrderCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.AccountType;
import com.solofunds.memberaccounting.model.CreatePaymentOrderDto;
import com.solofunds.memberaccounting.model.PaymentOrderDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentOrderDelegateImplTest {

    @Mock
    MessagePublisher mockPublisher;

    @Mock
    EventFactory mockEventFactory;

    @InjectMocks
    private PaymentOrderDelegateImpl paymentOrderApiDelegate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createPaymentOrder() throws Exception {
        UUID memberTransactionId = UUID.randomUUID();
        UUID originatingWalletId = UUID.randomUUID();
        UUID receivingWalletId = UUID.randomUUID();

        CreatePaymentOrderDto createPaymentOrderDto = new CreatePaymentOrderDto();
        createPaymentOrderDto.setMemberTransactionId(memberTransactionId);
        createPaymentOrderDto.setOriginatingWalletId(originatingWalletId);
        createPaymentOrderDto.setOriginatingAccountType(AccountType.INTERNAL);
        createPaymentOrderDto.setReceivingWalletId(receivingWalletId);
        createPaymentOrderDto.setReceivingAccountType(AccountType.INTERNAL);

        PaymentOrderCreateEvent paymentOrderCreateEvent = new PaymentOrderCreateEvent();
        paymentOrderCreateEvent.setPublisher(mockPublisher);
        paymentOrderCreateEvent.setCreatePaymentOrderDto(createPaymentOrderDto);

        when(mockEventFactory.buildPaymentOrderCreateEvent(any(CreatePaymentOrderDto.class))).thenReturn(paymentOrderCreateEvent);

        // The return value of this function is the saved PaymentOrderDto, but for simplicity we will return the same object
        when(mockPublisher
                .publishAndWait(any(PaymentOrderCreateEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(createPaymentOrderDto));

        ResponseEntity<PaymentOrderDto> response = paymentOrderApiDelegate.createPaymentOrder(createPaymentOrderDto);

        verify(mockPublisher, times(1)).publishAndWait(any(PaymentOrderCreateEvent.class), any(String.class));

        assertNotNull(response.getBody());
    }

    @Test
    void createPaymentOrder_nullPaymentOrderDto() {
        assertThrows(RuntimeException.class,
                () -> paymentOrderApiDelegate.createPaymentOrder(null)
        );
    }

    @Test
    void getPaymentOrdersByWalletAccountId() throws Exception {
        UUID walletAccountId = UUID.randomUUID();
        UUID memberTransactionId = UUID.randomUUID();
        UUID originatingWalletId = UUID.randomUUID();
        UUID receivingWalletId = UUID.randomUUID();

        PaymentOrderDto paymentOrderDto = new PaymentOrderDto();
        paymentOrderDto.setMemberTransactionId(memberTransactionId);
        paymentOrderDto.setOriginatingWalletId(originatingWalletId);
        paymentOrderDto.setOriginatingAccountType(AccountType.INTERNAL);
        paymentOrderDto.setReceivingWalletId(receivingWalletId);
        paymentOrderDto.setReceivingAccountType(AccountType.INTERNAL);

        List<PaymentOrderDto> paymentOrders = new ArrayList<>();
        paymentOrders.add(paymentOrderDto);

        ResourceByIdGetEvent paymentOrderGetEvent = ResourceByIdGetEvent.builder()
                .publisher(mockPublisher)
                .topic(ResourceByIdGetEvent.PAYMENT_ORDER_BY_WALLET_ACCOUNT_GET_EVENT)
                .id(walletAccountId)
                .build();

        when(mockEventFactory.buildPaymentOrderByWalletAccountIdGetEvent(any(UUID.class))).thenReturn(paymentOrderGetEvent);

        // The return value of this function is the saved PaymentOrderDto, but for simplicity we will return the same object
        when(mockPublisher
                .publishAndWait(any(ResourceByIdGetEvent.class), any(String.class)))
                .thenReturn(objectMapper.writeValueAsString(paymentOrders));

        ResponseEntity<List<PaymentOrderDto>> response = paymentOrderApiDelegate.getPaymentOrdersByWalletAccountId(walletAccountId);

        verify(mockPublisher, times(1)).publishAndWait(any(ResourceByIdGetEvent.class), any(String.class));

        assertNotNull(response.getBody());
        assertEquals(memberTransactionId, response.getBody().get(0).getMemberTransactionId());
    }

    @Test
    void getPaymentOrdersByWalletAccountId_nullWalletAccountId() {
        assertThrows(RuntimeException.class,
                () -> paymentOrderApiDelegate.getPaymentOrdersByWalletAccountId(null)
        );
    }
}