package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.walletAccount.WalletAccountCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.walletAccount.WalletAccountGetAllByMemberIdEvent;
import com.solofunds.memberaccounting.model.CreateWalletAccountDto;
import com.solofunds.memberaccounting.model.WalletAccountDto;
import com.solofunds.memberaccounting.service.service.WalletAccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WalletAccountHandlerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    @Autowired
    EventFactory eventFactory;

    @Mock
    WalletAccountService walletAccountService;

    @InjectMocks
    WalletAccountHandler walletAccountHandler;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        walletAccountHandler = new WalletAccountHandler(objectMapper, walletAccountService);
        fixture = new Fixture();
    }

    @Test
    void testCreateWalletAccountEventHandlerSuccess() throws Exception {
        fixture.givenCreateWalletAccountCreateEventIsProvided();
        fixture.givenMessageStringIsProvided();
        fixture.givenWalletAccountServiceReturnsWalletAccountDto();

        fixture.whenConsumeMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Test
    void testFundingProposalUpdateEventHandlerRuntimeException() throws Exception {
        fixture.givenCreateWalletAccountCreateEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();

        fixture.whenConsumeMessageVerifyExceptionIsThrown();
    }

    @Test
    void testGetWalletAccountEventHandlerSuccess() throws Exception {
        fixture.givenWalletAccountGetEventIsProvided();
        fixture.givenGetMessageStringIsProvided();
        fixture.givenWalletAccountServiceReturnsGetWalletAccount();

        fixture.whenConsumeGetMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Test
    void testGetWalletAccountEventHandlerSuccessRuntimeException() throws Exception {
        fixture.givenWalletAccountGetEventIsProvided();
        fixture.givenMalformedGetMessageStringIsProvided();

        fixture.whenConsumeGetMessageVerifyExceptionIsThrown();
    }

    @Test
    void testGetAllWalletAccountByMemberIdEventHandlerSuccess() throws Exception {
        fixture.givenWalletAccountGetAllByMemberIdEventIsProvided();
        fixture.givenGetAllByMemberIdMessageStringIsProvided();
        fixture.givenWalletAccountServiceReturnsGetAllByMemberIdWalletAccount();

        fixture.whenConsumeGetAllByMemberIdMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturnedForGetAllByMemberId();
    }

    @Test
    void testGetAllWalletAccountByMemberIdEventHandlerSuccessRuntimeException() throws Exception {
        fixture.givenWalletAccountGetAllByMemberIdEventIsProvided();
        fixture.givenMalformedGetAllByMemberIdMessageStringIsProvided();

        fixture.whenConsumeGetAllByMemberIdMessageVerifyExceptionIsThrown();
    }

    private class Fixture {

        UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

        UUID SOLO_MEMBER_ID = UUID.randomUUID();

        WalletAccountCreateEvent walletAccountCreateEvent;

        ResourceByIdGetEvent walletAccountGetEvent;

        WalletAccountGetAllByMemberIdEvent walletAccountGetAllByMemberIdEvent;

        Message<String> createRequest;

        Message<String> getAllByMemberIdRequest;

        Message<String> getRequest;

        WalletAccountDto walletAccountDto;

        String response;

        public void givenCreateWalletAccountCreateEventIsProvided() {
            CreateWalletAccountDto createWalletAccountDto = new CreateWalletAccountDto();
            walletAccountCreateEvent = eventFactory.buildWalletAccountCreateEvent(createWalletAccountDto);
        }

        public void givenMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString(walletAccountCreateEvent);
            createRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void givenWalletAccountServiceReturnsWalletAccountDto() {
            walletAccountDto = new WalletAccountDto();
            walletAccountDto.setWalletAccountId(WALLET_ACCOUNT_ID);
            when(walletAccountService.createWalletAccount(any(CreateWalletAccountDto.class))).thenReturn(walletAccountDto);
        }

        public void whenConsumeMessageIsCalled() {
            response = walletAccountHandler.consumeCreateMessage(createRequest);
        }

        public void thenVerifyExpectedJsonResponseIsReturned() throws JsonProcessingException {
            assertNotNull(response);
            WalletAccountDto walletAccountDtoResponse = objectMapper.readValue(response, WalletAccountDto.class);
            assertEquals(WALLET_ACCOUNT_ID, walletAccountDtoResponse.getWalletAccountId());
        }

        public void givenMalformedMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString("test");
            createRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void whenConsumeMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> walletAccountHandler.consumeCreateMessage(createRequest));
        }

        public void givenWalletAccountGetEventIsProvided() {
            walletAccountGetEvent = eventFactory.buildWalletAccountGetEvent(WALLET_ACCOUNT_ID);
        }

        public void givenGetMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString(walletAccountGetEvent);
            getRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void givenWalletAccountServiceReturnsGetWalletAccount() {
            walletAccountDto=new WalletAccountDto();
            walletAccountDto.setWalletAccountId(WALLET_ACCOUNT_ID);
            when(walletAccountService.getWalletAccountById(WALLET_ACCOUNT_ID)).thenReturn(walletAccountDto);
        }

        public void whenConsumeGetMessageIsCalled() {
            response = walletAccountHandler.consumeGetMessage(getRequest);
        }

        public void givenMalformedGetMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString("test");
            getRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void whenConsumeGetMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> walletAccountHandler.consumeGetMessage(getRequest));
        }

        public void givenWalletAccountGetAllByMemberIdEventIsProvided() {
            walletAccountGetAllByMemberIdEvent = eventFactory.buildWalletAccountGetAllByMemberIdEvent(SOLO_MEMBER_ID);
        }

        public void givenGetAllByMemberIdMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString(walletAccountGetAllByMemberIdEvent);
            getAllByMemberIdRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void givenWalletAccountServiceReturnsGetAllByMemberIdWalletAccount() {
            walletAccountDto=new WalletAccountDto();
            walletAccountDto.setWalletAccountId(WALLET_ACCOUNT_ID);
            when(walletAccountService.getAllWalletAccountByMemberId(SOLO_MEMBER_ID)).thenReturn(List.of(walletAccountDto));
        }

        public void whenConsumeGetAllByMemberIdMessageIsCalled() {
            response = walletAccountHandler.consumeGetAllByMemberIdMessage(getAllByMemberIdRequest);
        }

        public void thenVerifyExpectedJsonResponseIsReturnedForGetAllByMemberId() throws JsonProcessingException {
            assertNotNull(response);
            List<WalletAccountDto> walletAccountDtoList = objectMapper.readValue(response, new TypeReference<>(){});
            assertEquals(WALLET_ACCOUNT_ID, walletAccountDtoList.get(0).getWalletAccountId());
        }

        public void givenMalformedGetAllByMemberIdMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString("test");
            getAllByMemberIdRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void whenConsumeGetAllByMemberIdMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> walletAccountHandler.consumeGetAllByMemberIdMessage(getAllByMemberIdRequest));
        }
    }
}
