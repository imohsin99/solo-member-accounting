package com.solofunds.memberaccounting.service.eventhandler.loanRequest;

import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.LoanRequestDto;
import com.solofunds.memberaccounting.service.eventhandler.loanrequest.LoanRequestGetEventHandler;
import com.solofunds.memberaccounting.service.service.LoanRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanRequestGetEventHandlerTest {
    @Mock
    LoanRequestService loanRequestService;

    @InjectMocks
    LoanRequestGetEventHandler loanRequestGetEventHandler;

    LoanRequestGetEventHandlerTest.Fixture fixture;

    @Mock
    Message<ResourceByIdGetEvent> loanRequestGetEventMessage;

    @BeforeEach
    void setUp() {
        fixture = new LoanRequestGetEventHandlerTest.Fixture();
    }

    @Test
    void testGetLoanRequestEventHandlerSuccess() {
        fixture.givenLoanRequestGetEventIsProvided();
        fixture.givenLoanRequestServiceReturnsGetLoanRequest();

        fixture.whenConsumeGetMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    private class Fixture {
        final UUID LOAN_REQUEST_ID = UUID.randomUUID();

        LoanRequestDto response;

        LoanRequestDto loanRequestDto;

        public void thenVerifyExpectedJsonResponseIsReturned() {
            assertNotNull(response);
            assertEquals(LOAN_REQUEST_ID, response.getLoanRequestId());
        }

        public void givenLoanRequestGetEventIsProvided() {
            when(loanRequestGetEventMessage.getPayload()).thenReturn(ResourceByIdGetEvent
                    .builder()
                    .id(LOAN_REQUEST_ID)
                    .build());
        }

        public void givenLoanRequestServiceReturnsGetLoanRequest() {
            loanRequestDto = new LoanRequestDto(null, null, null, null, null, null, null, null);
            loanRequestDto.setLoanRequestId(LOAN_REQUEST_ID);
            when(loanRequestService.getLoanRequestById(LOAN_REQUEST_ID)).thenReturn(loanRequestDto);
        }

        public void whenConsumeGetMessageIsCalled() {
            response = loanRequestGetEventHandler.consumeEvent(loanRequestGetEventMessage).getPayload();
        }
    }
}
