package com.solofunds.memberaccounting.service.eventhandler.interchangeNode;

import com.solofunds.memberaccounting.messaging.messenger.event.interchangeNode.InterchangeNodeCreateEvent;
import com.solofunds.memberaccounting.model.InterchangeNodeDto;
import com.solofunds.memberaccounting.model.CreateInterchangeNodeDto;
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

public class InterchangeNodeCreateEventHandlerTest {

    @Mock
    InterchangeNodeService interchangeNodeService;
    @Mock
    Message<InterchangeNodeCreateEvent> interchangeNodeCreateEventMessage;
    @InjectMocks
    InterchangeNodeCreateEventHandler interchangeNodeCreateEventHandler;
    Fixture fixture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interchangeNodeCreateEventHandler = new InterchangeNodeCreateEventHandler(interchangeNodeService);
        fixture = new Fixture();
    }

    @Test
    void testInterchangeNodeCreateEventHandlerSuccess() {
        fixture.givenInterchangeNodeCreateEventIsProvided();
        fixture.givenInterchangeNodeServiceReturnsInterchangeNodeDto();
        fixture.whenConsumeCreateMessageIsCalled();
        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    private class Fixture{
        UUID INTERCHANGE_NODE_ID = UUID.randomUUID();
        InterchangeNodeCreateEvent interchangeNodeCreateEvent;
        CreateInterchangeNodeDto createInterchangeNodeDto;
        InterchangeNodeDto interchangeNodeDto;
        Message<InterchangeNodeDto> response;

        public void givenInterchangeNodeCreateEventIsProvided() {
            createInterchangeNodeDto = new CreateInterchangeNodeDto();
            interchangeNodeCreateEvent=InterchangeNodeCreateEvent
                    .builder()
                    .createInterchangeNodeDto(createInterchangeNodeDto)
                    .build();
        }

        public void givenInterchangeNodeServiceReturnsInterchangeNodeDto() {
            interchangeNodeDto = new InterchangeNodeDto();
            interchangeNodeDto.setId(INTERCHANGE_NODE_ID);
            when(interchangeNodeService.createInterchangeNode(any(CreateInterchangeNodeDto.class))).thenReturn(interchangeNodeDto);
        }

        public void whenConsumeCreateMessageIsCalled() {
            when(interchangeNodeCreateEventMessage.getPayload()).thenReturn(interchangeNodeCreateEvent);
            response = interchangeNodeCreateEventHandler.consumeEvent(interchangeNodeCreateEventMessage);
        }

        public void thenVerifyExpectedJsonResponseIsReturned() {
            assertNotNull(response);
            InterchangeNodeDto interchangeNodeDtoResponse = response.getPayload();
            assertEquals(INTERCHANGE_NODE_ID, interchangeNodeDtoResponse.getId());
        }
    }
}
