package com.solofunds.memberaccounting.service.eventhandler.bankCard;

import com.solofunds.memberaccounting.messaging.messenger.event.bankCard.BankCardCreateEvent;
import com.solofunds.memberaccounting.model.BankCardDto;
import com.solofunds.memberaccounting.model.CreateBankCardDto;
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

public class BankCardCreateEventHandlerTest {

    @Mock
    BankCardService bankCardService;
    @Mock
    Message<BankCardCreateEvent> bankCardCreateEventMessage;
    @InjectMocks
    BankCardCreateEventHandler bankCardCreateEventHandler;
    Fixture fixture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bankCardCreateEventHandler = new BankCardCreateEventHandler(bankCardService);
        fixture = new Fixture();
    }

    @Test
    void testBankCardCreateEventHandlerSuccess() {
        fixture.givenBankCardCreateEventIsProvided();
        fixture.givenBankCardServiceReturnsBankCardDto();
        fixture.whenConsumeCreateMessageIsCalled();
        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    private class Fixture{
        UUID BANK_CARD_ID = UUID.randomUUID();
        BankCardCreateEvent bankCardCreateEvent;
        CreateBankCardDto createBankCardDto;
        BankCardDto bankCardDto;
        Message<BankCardDto> response;

        public void givenBankCardCreateEventIsProvided() {
            createBankCardDto = new CreateBankCardDto();
            bankCardCreateEvent=BankCardCreateEvent
                    .builder()
                    .createBankCardDto(createBankCardDto)
                    .build();
        }

        public void givenBankCardServiceReturnsBankCardDto() {
            bankCardDto = new BankCardDto();
            bankCardDto.setId(BANK_CARD_ID);
            when(bankCardService.createBankCard(any(CreateBankCardDto.class))).thenReturn(bankCardDto);
        }

        public void whenConsumeCreateMessageIsCalled() {
            when(bankCardCreateEventMessage.getPayload()).thenReturn(bankCardCreateEvent);
            response = bankCardCreateEventHandler.consumeEvent(bankCardCreateEventMessage);
        }

        public void thenVerifyExpectedJsonResponseIsReturned() {
            assertNotNull(response);
            BankCardDto bankCardDtoResponse = response.getPayload();
            assertEquals(BANK_CARD_ID, bankCardDtoResponse.getId());
        }
    }
}
