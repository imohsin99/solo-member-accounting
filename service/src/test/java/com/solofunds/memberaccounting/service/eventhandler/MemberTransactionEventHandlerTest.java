package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.memberTransaction.MemberTransactionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateMemberTransactionDto;
import com.solofunds.memberaccounting.model.MemberTransactionDto;
import com.solofunds.memberaccounting.service.service.MemberTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;

import java.util.UUID;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
 
@SpringBootTest
class MemberTransactionEventHandlerTest {

    @Mock
    MessagePublisher mockMessagePublisher;

    @Mock
    MemberTransactionService memberTransactionService;
 
    @InjectMocks
    MemberTransactionEventHandler memberTransactionEventHandler;
 
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
 
    Fixture fixture;
 
    @BeforeEach
    void setUp() {
        memberTransactionEventHandler=new MemberTransactionEventHandler(objectMapper,memberTransactionService);
        fixture = new Fixture();
    }
 
    @Test
    void consumeCreateEvent() throws Exception {
        fixture.givenMemberTransactionCreateEventIsProvided();
        fixture.givenCreateMessageStringIsProvided();
        fixture.givenCollectionServiceReturnsCollectionDto();
 
        fixture.whenConsumeCreateMessageIsCalled();
 
        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }
 
    @Test
    void consumeCreateMessage_Exception() throws Exception {
        fixture.givenMemberTransactionCreateEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();
 
        fixture.whenConsumeMessageVerifyExceptionIsThrown();
    }
 
    @Test
    void consumeGetMessage() throws Exception {
        fixture.givenMemberTransactionGetEventIsProvided();
 
        fixture.givenGetMessageStringIsProvided();
        fixture.givenCollectionServiceReturnsGetCollectionDto();
 
        fixture.whenConsumeGetMessageIsCalled();
 
        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }
 
    @Test
    void consumeGetMessage_Exception() throws Exception {
        fixture.givenMemberTransactionGetEventIsProvided();
        fixture.givenMalformedGetMessageStringIsProvided();
 
        fixture.whenConsumeGetMessageVerifyExceptionIsThrown();
    }
 
         
    private class Fixture{
        MemberTransactionCreateEvent memberTransactionCreateEvent;
 
        UUID MEMBER_TRANSACTION_ID = UUID.randomUUID();
 
        Message<String> createRequest;
 
        Message<String> getRequest;
 
        MemberTransactionDto memberTransactionDTO;

        ResourceByIdGetEvent memberTransactionGetEvent;

        String response;
        void givenMemberTransactionCreateEventIsProvided(){
            CreateMemberTransactionDto createMemberTransactionDto = new CreateMemberTransactionDto();
            memberTransactionCreateEvent = MemberTransactionCreateEvent
                    .builder()
                    .createMemberTransactionDto(createMemberTransactionDto)
                    .build();
        }
 
        void givenMemberTransactionGetEventIsProvided(){
            ResourceByIdGetEvent memberTransactionRequest = new ResourceByIdGetEvent();
            memberTransactionGetEvent = ResourceByIdGetEvent
                    .builder()
                    .publisher(mockMessagePublisher)
                    .topic(ResourceByIdGetEvent.MEMBER_TRANSACTION_GET_TOPIC)
                    .id(MEMBER_TRANSACTION_ID)
                    .build();
        }
 
        void givenCreateMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String createJson = objectMapper.writeValueAsString(memberTransactionCreateEvent);
            createRequest = (Message<String>) simpleMessageConverter.toMessage(createJson, null);
        }
 
        void givenCollectionServiceReturnsCollectionDto() {
            memberTransactionDTO = new MemberTransactionDto();
            memberTransactionDTO.setId(MEMBER_TRANSACTION_ID);
            when(memberTransactionService.createMemberTransaction(any(CreateMemberTransactionDto.class))).thenReturn(memberTransactionDTO);
        }
 
        void whenConsumeCreateMessageIsCalled() {
            response = memberTransactionEventHandler.comsumeCreateMessage(createRequest);
        }
 
        void thenVerifyExpectedJsonResponseIsReturned() throws JsonProcessingException {
            assertNotNull(response);
            MemberTransactionDto memberTransactionResponse = objectMapper.readValue(response, MemberTransactionDto.class);
            assertEquals(MEMBER_TRANSACTION_ID, memberTransactionResponse.getId());
        }
 
        void givenMalformedMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String createJson = objectMapper.writeValueAsString("test");
            createRequest = (Message<String>) simpleMessageConverter.toMessage(createJson, null);
        }
 
        void whenConsumeMessageVerifyExceptionIsThrown() {
            assertThrows(Exception.class, () -> memberTransactionEventHandler.comsumeCreateMessage(createRequest));
        }
 
        void whenConsumeGetMessageVerifyExceptionIsThrown() {
            assertThrows(Exception.class, () -> memberTransactionEventHandler.comsumeGetMessage(getRequest));
        }
 
        public void whenConsumeGetMessageIsCalled() {
            response = memberTransactionEventHandler.comsumeGetMessage(getRequest);
        }
 
        public void givenGetMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString(memberTransactionGetEvent);
            getRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }
 
        public void givenCollectionServiceReturnsGetCollectionDto() {
            memberTransactionDTO = new MemberTransactionDto();
            memberTransactionDTO.setId(MEMBER_TRANSACTION_ID);
            when(memberTransactionService.getMemberTransactionById(eq(MEMBER_TRANSACTION_ID))).thenReturn(memberTransactionDTO);
        }
 
        public void givenMalformedGetMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString("test");
            getRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }
    }
}