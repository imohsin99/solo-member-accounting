package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.PaymentOrderApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.paymentOrder.PaymentOrderCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreatePaymentOrderDto;
import com.solofunds.memberaccounting.model.PaymentOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class PaymentOrderDelegateImpl implements PaymentOrderApiDelegate {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<PaymentOrderDto> createPaymentOrder(CreatePaymentOrderDto createPaymentOrderDto) {
        try {
            PaymentOrderCreateEvent paymentOrderCreateEvent = eventFactory.buildPaymentOrderCreateEvent(createPaymentOrderDto);
            PaymentOrderDto createdPaymentOrderDto = objectMapper.readValue(paymentOrderCreateEvent.publishAndWait(), PaymentOrderDto.class);
            return ResponseEntity.ok(createdPaymentOrderDto);
        } catch (Exception e) {
            log.error("Exception encountered during createPaymentOrder", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public ResponseEntity<List<PaymentOrderDto>> getPaymentOrdersByWalletAccountId(UUID walletAccountId) {
        try {
            ResourceByIdGetEvent event = eventFactory.buildPaymentOrderByWalletAccountIdGetEvent(walletAccountId);
            List<PaymentOrderDto> paymentOrders = objectMapper.readValue(event.publishAndWait(), new TypeReference<>() {});
            return ResponseEntity.ok(paymentOrders);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
