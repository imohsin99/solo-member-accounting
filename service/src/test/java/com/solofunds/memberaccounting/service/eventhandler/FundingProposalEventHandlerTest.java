package com.solofunds.memberaccounting.service.eventhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal.FundingProposalCreatedEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal.FundingProposalUpdateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loanRequest.LoanRequestCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateFundingProposalDto;
import com.solofunds.memberaccounting.model.CreateLoanRequestDto;
import com.solofunds.memberaccounting.model.FundingProposalDto;
import com.solofunds.memberaccounting.model.LoanRequestDto;
import com.solofunds.memberaccounting.model.UpdateFundingProposalDto;
import com.solofunds.memberaccounting.service.service.FundingProposalService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Nested;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class FundingProposalEventHandlerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    @Autowired
    EventFactory eventFactory;

    @Mock
    FundingProposalService fundingProposalService;

    @InjectMocks
    FundingProposalEventHandler fundingProposalEventHandler;

    @Test
    void testFundingProposalGetEventHandlerSuccess() throws Exception {
        fixture.givenFundingProposalGetEventIsProvided();
        fixture.givenGetMessageStringIsProvided();
        fixture.givenFundingProposalServiceReturnsGetFundingProposalDto();

        fixture.whenConsumeGetMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Test
    void testFundingProposalGetEventHandlerRuntimeException() throws Exception {
        fixture.givenFundingProposalGetEventIsProvided();
        fixture.givenMalformedGetMessageStringIsProvided();

        fixture.whenConsumeGetMessageVerifyExceptionIsThrown();
    }

    @Test
    void testFundingProposalCreateEventHandlerSuccess() throws Exception {
        fixture.givenFundingProposalCreateEventIsProvided();
        fixture.givenCreateMessageStringIsProvided();
        fixture.givenFundingProposalServiceReturnsCreateFundingProposalDto();

        fixture.whenConsumeCreateMessageIsCalled();

        fixture.thenVerifyExpectedJsonCreateResponseIsReturned();
    }

    @Test
    void testFundingProposalCreateEventHandlerRuntimeException() throws Exception {
        fixture.givenFundingProposalCreateEventIsProvided();
        fixture.givenMalformedCreateMessageStringIsProvided();

        fixture.whenConsumeCreateMessageVerifyExceptionIsThrown();
    }

    @Test
    void testFundingProposalUpdateEventHandlerSuccess() throws Exception {
        fixture.givenFundingProposalUpdateEventIsProvided();
        fixture.givenMessageStringIsProvided();
        fixture.givenFundingProposalServiceReturnsFundingProposalDto();

        fixture.whenConsumeMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Test
    void testFundingProposalUpdateEventHandlerRuntimeException() throws Exception {
        fixture.givenFundingProposalUpdateEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();

        fixture.whenConsumeMessageVerifyExceptionIsThrown();
    }

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fundingProposalEventHandler = new FundingProposalEventHandler(fundingProposalService, objectMapper);
        fixture = new Fixture();
    }

    @Nested
    private class Fixture {
        UUID FUNDING_PROPOSAL_ID = UUID.randomUUID();
        FundingProposalUpdateEvent fundingProposalUpdateEvent;
        FundingProposalCreatedEvent fundingProposalCreatedEvent;
        ResourceByIdGetEvent fundingProposalGetEvent;
        Message<String> fundingProposalUpdateRequest;
        FundingProposalDto fundingProposalDto;
        Message<String> getRequest;
        Message<String> createRequest;
        Message<String> updateRequest;
        String response;

        void givenFundingProposalGetEventIsProvided() {
            fundingProposalGetEvent = ResourceByIdGetEvent
                    .builder()
                    .id(FUNDING_PROPOSAL_ID)
                    .build();;
        }

        public void givenGetMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString(fundingProposalGetEvent);
            getRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }
        public void givenMalformedGetMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString("test");
            getRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        void givenFundingProposalServiceReturnsGetFundingProposalDto() {
            fundingProposalDto = new FundingProposalDto();
            fundingProposalDto.setFundingProposalId(FUNDING_PROPOSAL_ID);
            when(fundingProposalService.getFundingProposalById(FUNDING_PROPOSAL_ID)).thenReturn(fundingProposalDto);
        }

        public void whenConsumeGetMessageIsCalled() {
            response = fundingProposalEventHandler.consumeGetMessage(getRequest);
        }

        void whenConsumeGetMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> fundingProposalEventHandler.consumeGetMessage(getRequest));
        }

        void givenFundingProposalUpdateEventIsProvided() {
            UpdateFundingProposalDto requestDto = new UpdateFundingProposalDto();
            fundingProposalUpdateEvent = eventFactory.buildFundingProposalUpdateEvent(FUNDING_PROPOSAL_ID, requestDto);
        }

        void givenMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String fundingProposalUpdateEventJson = objectMapper.writeValueAsString(fundingProposalUpdateEvent);
            fundingProposalUpdateRequest = (Message<String>) simpleMessageConverter.toMessage(fundingProposalUpdateEventJson, null);
        }


        void givenFundingProposalServiceReturnsFundingProposalDto() {
            fundingProposalDto = new FundingProposalDto();
            fundingProposalDto.setFundingProposalId(FUNDING_PROPOSAL_ID);
            when(fundingProposalService.updateFundingProposalStatus(eq(FUNDING_PROPOSAL_ID), any(UpdateFundingProposalDto.class))).thenReturn(fundingProposalDto);
        }

        void whenConsumeMessageIsCalled() {
            response = fundingProposalEventHandler.consumeUpdateMessage(fundingProposalUpdateRequest);
        }

        void thenVerifyExpectedJsonResponseIsReturned() throws JsonProcessingException {
            assertNotNull(response);
            FundingProposalDto fundingProposalDtoResponse = objectMapper.readValue(response, FundingProposalDto.class);
            assertEquals(FUNDING_PROPOSAL_ID, fundingProposalDtoResponse.getFundingProposalId());
        }

        void givenMalformedMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String fundingProposalUpdateEventJson = objectMapper.writeValueAsString("test");
            fundingProposalUpdateRequest = (Message<String>) simpleMessageConverter.toMessage(fundingProposalUpdateEventJson, null);
        }

        void whenConsumeMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> fundingProposalEventHandler.consumeUpdateMessage(fundingProposalUpdateRequest));
        }

        public void givenFundingProposalCreateEventIsProvided() {
            CreateFundingProposalDto createLoanRequestDto = new CreateFundingProposalDto();
            fundingProposalCreatedEvent= FundingProposalCreatedEvent
                    .builder()
                    .createFundingProposalDto(createLoanRequestDto)
                    .build();
        }

        public void givenCreateMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString(fundingProposalCreatedEvent);
            createRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void givenFundingProposalServiceReturnsCreateFundingProposalDto(){
            fundingProposalDto = new FundingProposalDto();
            fundingProposalDto.setFundingProposalId(FUNDING_PROPOSAL_ID);
            when(fundingProposalService.createFundingProposal(any(CreateFundingProposalDto.class))).thenReturn(fundingProposalDto);
        }

        public void whenConsumeCreateMessageIsCalled() {
            response = fundingProposalEventHandler.consumeCreateMessage(createRequest);
        }

        public void thenVerifyExpectedJsonCreateResponseIsReturned() throws JsonProcessingException {
            assertNotNull(response);
            FundingProposalDto fundingProposalDtoResponse = objectMapper.readValue(response, FundingProposalDto.class);
            assertEquals(FUNDING_PROPOSAL_ID, fundingProposalDtoResponse.getFundingProposalId());
        }

        public void givenMalformedCreateMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString("test");
            createRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);

        }

        public void whenConsumeCreateMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> fundingProposalEventHandler.consumeCreateMessage(createRequest));
        }
    }
}
