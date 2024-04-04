package com.solofunds.memberaccounting.messaging.messenger.event.healthCheck;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class HealthCheckEvent extends Event {
    public static final String HEALTHCHECK_DESTINATION = "solo.queue.healthcheck.requested";

    @JsonProperty
    private final String healthCheckValue;

    @Override
    public String getDestination() {
        return HEALTHCHECK_DESTINATION;
    }
}
