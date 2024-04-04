package com.solofunds.memberaccounting.messaging.messenger.event.interchangeNode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreateInterchangeNodeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class InterchangeNodeCreateEvent extends Event implements Serializable {

    @JsonIgnore
    public static final String TOPIC = "interchangeNode.create.requested";

    private UUID id;

    private CreateInterchangeNodeDto createInterchangeNodeDto;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
