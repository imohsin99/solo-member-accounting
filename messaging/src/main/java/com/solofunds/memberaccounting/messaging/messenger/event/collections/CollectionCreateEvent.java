package com.solofunds.memberaccounting.messaging.messenger.event.collections;

import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.CreateCollectionsDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class CollectionCreateEvent extends Event {

    public static final String TOPIC = "collection.create.requested";

    private CreateCollectionsDto createCollectionsDTO;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
