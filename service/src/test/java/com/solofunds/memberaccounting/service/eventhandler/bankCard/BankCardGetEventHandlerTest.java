package com.solofunds.memberaccounting.service.eventhandler.bankCard;

import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.BankCardDto;
import com.solofunds.memberaccounting.service.service.BankCardService;
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

public class BankCardGetEventHandlerTest {

    @Mock
    BankCardService bankCardService;
    @Mock
    Message<ResourceByIdGetEvent> bankCardGetEventMessage;
    @InjectMocks
    BankCardGetEventHandler bankCardGetEventHandler;
    Fixture fixture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bankCardGetEventHandler = new BankCardGetEventHandler(bankCardService);
        fixture = new Fixture();
    }

    @Test
    void testBankCardGetEventHandlerSuccess() {
        fixture.givenBankCardGetEventIsProvided();
        fixture.givenBankCardServiceReturnsBankCardDto();
        fixture.whenConsumeCreateMessageIsCalled();
        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    private class Fixture{
        UUID BANK_CARD_ID = UUID.randomUUID();
        ResourceByIdGetEvent bankCardGetEvent;
        BankCardDto bankCardDto;
        Message<BankCardDto> response;

        public void givenBankCardGetEventIsProvided() {
            bankCardGetEvent = ResourceByIdGetEvent
                    .builder()
                    .topic(ResourceByIdGetEvent.BANK_CARD_GET_EVENT)
                    .id(BANK_CARD_ID)
                    .build();
        }

        public void givenBankCardServiceReturnsBankCardDto() {
            bankCardDto = new BankCardDto();
            bankCardDto.setId(BANK_CARD_ID);
            when(bankCardService.getBankCardById(any(UUID.class))).thenReturn(bankCardDto);
        }

        public void whenConsumeCreateMessageIsCalled() {
            when(bankCardGetEventMessage.getPayload()).thenReturn(bankCardGetEvent);
            response = bankCardGetEventHandler.consumeEvent(bankCardGetEventMessage);
        }

        public void thenVerifyExpectedJsonResponseIsReturned() {
            assertNotNull(response);
            BankCardDto bankCardDtoResponse = response.getPayload();
            assertEquals(BANK_CARD_ID, bankCardDtoResponse.getId());
        }
    }
}
