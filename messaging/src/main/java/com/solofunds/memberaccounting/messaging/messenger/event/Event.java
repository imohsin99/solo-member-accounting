package com.solofunds.memberaccounting.messaging.messenger.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Event {
    @JsonIgnore
    MessagePublisher<Event> publisher;

    public abstract String getDestination();

    @Deprecated
    public String publishAndWait() throws Exception {
        return publisher.publishAndWait(this, getDestination());
    }

    @SneakyThrows
    public <R> R publishAndReceive(Class<R> type) {
        return publisher.publishAndReceive(this, getDestination(), type);
    }
}
