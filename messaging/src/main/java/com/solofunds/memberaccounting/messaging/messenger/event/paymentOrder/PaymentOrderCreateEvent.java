package com.solofunds.memberaccounting.messaging.messenger.event.paymentOrder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreatePaymentOrderDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class PaymentOrderCreateEvent extends Event {

    public static final String TOPIC = "paymentOrder.create.requested";

    @JsonProperty
    private CreatePaymentOrderDto createPaymentOrderDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }

}
