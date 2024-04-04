package com.solofunds.memberaccounting.service.eventhandler.interchangeNode;

import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.InterchangeNodeDto;
import com.solofunds.memberaccounting.service.service.InterchangeNodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class InterchangeNodeGetEventHandlerTest {

    @Mock
    InterchangeNodeService interchangeNodeService;
    @Mock
    Message<ResourceByIdGetEvent> interchangeNodeGetEventMessage;
    @InjectMocks
    InterchangeNodeGetEventHandler interchangeNodeGetEventHandler;
    Fixture fixture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interchangeNodeGetEventHandler = new InterchangeNodeGetEventHandler(interchangeNodeService);
        fixture = new Fixture();
    }

    @Test
    void testInterchangeNodeGetEventHandlerSuccess() {
        fixture.givenInterchangeNodeGetEventIsProvided();
        fixture.givenInterchangeNodeServiceReturnsInterchangeNodeDto();
        fixture.whenConsumeCreateMessageIsCalled();
        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    private class Fixture{
        UUID INTERCHANGE_NODE_ID = UUID.randomUUID();
        ResourceByIdGetEvent interchangeNodeGetEvent;
        InterchangeNodeDto interchangeNodeDto;
        Message<InterchangeNodeDto> response;

        public void givenInterchangeNodeGetEventIsProvided() {
            interchangeNodeGetEvent = ResourceByIdGetEvent
                    .builder()
                    .topic(ResourceByIdGetEvent.INTERCHANGE_NODE_GET_EVENT)
                    .id(INTERCHANGE_NODE_ID)
                    .build();
        }

        public void givenInterchangeNodeServiceReturnsInterchangeNodeDto() {
            interchangeNodeDto = new InterchangeNodeDto();
            interchangeNodeDto.setId(INTERCHANGE_NODE_ID);
            when(interchangeNodeService.getInterchangeNodeById(any(UUID.class))).thenReturn(interchangeNodeDto);
        }

        public void whenConsumeCreateMessageIsCalled() {
            when(interchangeNodeGetEventMessage.getPayload()).thenReturn(interchangeNodeGetEvent);
            response = interchangeNodeGetEventHandler.consumeEvent(interchangeNodeGetEventMessage);
        }

        public void thenVerifyExpectedJsonResponseIsReturned() {
            assertNotNull(response);
            InterchangeNodeDto interchangeNodeDtoResponse = response.getPayload();
            assertEquals(INTERCHANGE_NODE_ID, interchangeNodeDtoResponse.getId());
        }
    }
}
