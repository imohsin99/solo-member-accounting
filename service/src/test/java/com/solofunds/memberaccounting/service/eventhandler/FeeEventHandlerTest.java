package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.fee.FeeForCollectionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.fee.FeeForLoanCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateFeeDto;
import com.solofunds.memberaccounting.model.FeeDto;
import com.solofunds.memberaccounting.service.service.FeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class FeeEventHandlerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    @Mock
    MessagePublisher mockMessagePublisher;

    @Mock
    FeeService feeService;

    @InjectMocks
    FeeEventHandler feeEventHandler;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        feeEventHandler = new FeeEventHandler(feeService, objectMapper);
        fixture = new Fixture();
    }

    @Test
    void consumeGetByLoanIdMessage() throws JsonProcessingException {
        fixture.givenFeeGetByLoanIdEventIsProvided();
        fixture.givenGetMessageStringIsProvided();
        fixture.givenFeeServiceReturnsGetFeeDTO();

        fixture.whenConsumeGetMessageByLoanIdIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Test
    void consumeGetMessage_Exception() throws Exception {
        fixture.givenFeeGetByLoanIdEventIsProvided();
        fixture.givenMalformedGetMessageStringIsProvided();

        fixture.whenConsumeGetMessageVerifyExceptionIsThrown();
    }

    @Test
    void consumeGetByCollectionIdMessage() throws JsonProcessingException {
        fixture.givenFeeGetByCollectionIdEventIsProvided();
        fixture.givenGetMessageStringIsProvidedByCollectionId();
        fixture.givenFeeServiceReturnsGetFeeDTOByCollectionId();

        fixture.whenConsumeGetMessageByCollectionIdIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Test
    void consumeGetMessageByCollectionId_Exception() throws Exception {
        fixture.givenFeeGetByCollectionIdEventIsProvided();
        fixture.givenMalformedGetMessageStringIsProvided();

        fixture.whenConsumeGetMessageByCollectionIdVerifyExceptionIsThrown();
    }

    @Test
    void consumeCreateByLoanIdMessage() throws JsonProcessingException {
        fixture.givenFeeCreateByLoanIdEventIsProvided();
        fixture.givenCreateMessageStringIsProvidedByLoanId();
        fixture.givenFeeServiceReturnsCreateFeeDTOByLoanId();

        fixture.whenConsumeCreateMessageByLoanIdIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturnedForCreateEvent();
    }

    @Test
    void consumeCreateByLoanIdMessage_Exception() throws Exception {
        fixture.givenFeeCreateByLoanIdEventIsProvided();
        fixture.givenMalformedCreateMessageStringIsProvided();

        fixture.whenConsumeCreateMessageByLoanIdVerifyExceptionIsThrown();
    }

    @Test
    void consumeCreateByCollectionIdMessage() throws JsonProcessingException {
        fixture.givenFeeCreateByCollectionIdEventIsProvided();
        fixture.givenCreateMessageStringIsProvidedByCollectionId();
        fixture.givenFeeServiceReturnsCreateFeeDTOByCollectionId();

        fixture.whenConsumeCreateMessageByCollectionIdIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturnedForCreateEvent();
    }

    @Test
    void consumeCreateByCollectionIdMessage_Exception() throws Exception {
        fixture.givenFeeCreateByCollectionIdEventIsProvided();
        fixture.givenMalformedCreateMessageStringIsProvided();

        fixture.whenConsumeCreateMessageByCollectionIdVerifyExceptionIsThrown();
    }

    private class Fixture{

        ResourceByIdGetEvent feeForLoanGetEvent;

        ResourceByIdGetEvent feeForCollectionGetEvent;

        FeeForCollectionCreateEvent feeForCollectionCreateEvent;

        FeeForLoanCreateEvent feeForLoanCreateEvent;

        FeeDto feeDTO;

        Message<String> createRequest;

        Message<String> getRequest;

        String response;

        UUID LOAN_ID = UUID.randomUUID();

        UUID FEE_ID = UUID.randomUUID();

        UUID COLLECTION_ID = UUID.randomUUID();

        public void givenFeeGetByLoanIdEventIsProvided() {
            feeForLoanGetEvent = ResourceByIdGetEvent
                    .builder()
                    .publisher(mockMessagePublisher)
                    .topic(ResourceByIdGetEvent.FEE_FOR_LOAN_GET_TOPIC)
                    .id(LOAN_ID)
                    .build();
        }

        public void givenGetMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString(feeForLoanGetEvent);
            getRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }

        public void givenFeeServiceReturnsGetFeeDTO() {
            feeDTO = new FeeDto();
            feeDTO.setId(FEE_ID);
            when(feeService.getAllFeesForLoanId(any(UUID.class))).thenReturn(List.of(feeDTO));
        }

        public void whenConsumeGetMessageByLoanIdIsCalled() {
            response = feeEventHandler.consumeGetByLoanIdMessage(getRequest);
        }

        public void thenVerifyExpectedJsonResponseIsReturned() throws JsonProcessingException {
            assertNotNull(response);
            List<FeeDto> feeDTOList = objectMapper.readValue(response, new TypeReference<>(){} );
            assertEquals(FEE_ID, feeDTOList.get(0).getId());
        }

        public void givenMalformedGetMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString("test");
            getRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }

        public void whenConsumeGetMessageVerifyExceptionIsThrown() {
            assertThrows(Exception.class, () -> feeEventHandler.consumeGetByLoanIdMessage(getRequest));
        }

        public void givenFeeGetByCollectionIdEventIsProvided() {
            feeForCollectionGetEvent= ResourceByIdGetEvent
                    .builder()
                    .publisher(mockMessagePublisher)
                    .topic(ResourceByIdGetEvent.FEE_FOR_COLLECTION_GET_TOPIC)
                    .id(COLLECTION_ID)
                    .build();
        }

        public void givenFeeServiceReturnsGetFeeDTOByCollectionId() {
            feeDTO = new FeeDto();
            feeDTO.setId(FEE_ID);
            when(feeService.getAllFeesForCollectionId(eq(COLLECTION_ID))).thenReturn(List.of(feeDTO));
        }

        public void givenGetMessageStringIsProvidedByCollectionId() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString(feeForCollectionGetEvent);
            getRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }

        public void whenConsumeGetMessageByCollectionIdIsCalled() {
            response = feeEventHandler.consumeGetByCollectionIdMessage(getRequest);
        }

        public void whenConsumeGetMessageByCollectionIdVerifyExceptionIsThrown() {
            assertThrows(Exception.class, () -> feeEventHandler.consumeGetByCollectionIdMessage(getRequest));
        }

        public void givenFeeCreateByLoanIdEventIsProvided() {
            CreateFeeDto fee = new CreateFeeDto();
            feeForLoanCreateEvent = FeeForLoanCreateEvent
                    .builder()
                    .id(LOAN_ID)
                    .createFeeDto(fee)
                    .build();
        }

        public void givenFeeServiceReturnsCreateFeeDTOByLoanId() {
            feeDTO = new FeeDto();
            feeDTO.setId(FEE_ID);
            when(feeService.createFeeForLoanId(eq(LOAN_ID), any(CreateFeeDto.class))).thenReturn(feeDTO);
        }

        public void givenCreateMessageStringIsProvidedByLoanId() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString(feeForLoanCreateEvent);
            createRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }

        public void whenConsumeCreateMessageByLoanIdIsCalled() {
            response = feeEventHandler.consumeCreateByLoanIdMessage(createRequest);
        }

        public void thenVerifyExpectedJsonResponseIsReturnedForCreateEvent() throws JsonProcessingException {
            assertNotNull(response);
            FeeDto feeDTO = objectMapper.readValue(response, FeeDto.class);
            assertEquals(FEE_ID, feeDTO.getId());
        }

        
        public void givenMalformedCreateMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString("test");
            createRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }

        public void whenConsumeCreateMessageByLoanIdVerifyExceptionIsThrown() {
            assertThrows(Exception.class, () -> feeEventHandler.consumeCreateByLoanIdMessage(createRequest));
        }

        public void givenFeeCreateByCollectionIdEventIsProvided() {
            CreateFeeDto fee = new CreateFeeDto();
            feeForCollectionCreateEvent= FeeForCollectionCreateEvent
                    .builder()
                    .id(COLLECTION_ID)
                    .createFeeDto(fee)
                    .build();
        }

        public void givenCreateMessageStringIsProvidedByCollectionId() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String getJson = objectMapper.writeValueAsString(feeForCollectionCreateEvent);
            createRequest = (Message<String>) simpleMessageConverter.toMessage(getJson, null);
        }

        public void givenFeeServiceReturnsCreateFeeDTOByCollectionId() {
            feeDTO = new FeeDto();
            feeDTO.setId(FEE_ID);
            when(feeService.createFeeForCollectionId(eq(COLLECTION_ID), any(CreateFeeDto.class))).thenReturn(feeDTO);
        }

        public void whenConsumeCreateMessageByCollectionIdIsCalled() {
            response = feeEventHandler.consumeCreateByCollectionIdMessage(createRequest);
        }

        public void whenConsumeCreateMessageByCollectionIdVerifyExceptionIsThrown() {
            assertThrows(Exception.class, () -> feeEventHandler.consumeCreateByCollectionIdMessage(createRequest));
        }
    }
}