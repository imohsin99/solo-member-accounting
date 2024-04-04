package com.solofunds.memberaccounting.service.eventhandler.loanRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.loanRequest.GetFilteredAndSortedLoanRequestEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loanRequest.LoanRequestCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.loanRequestEvents.GetAllFundingProposalByLoanRequestIdEvent;
import com.solofunds.memberaccounting.model.CreateLoanRequestDto;
import com.solofunds.memberaccounting.model.FundingProposalDto;
import com.solofunds.memberaccounting.model.LoanRequestDto;
import com.solofunds.memberaccounting.model.LoanRequestStatus;
import com.solofunds.memberaccounting.model.SortBy;
import com.solofunds.memberaccounting.model.SortDirection;
import com.solofunds.memberaccounting.service.eventhandler.loanrequest.LoanRequestEventHandler;
import com.solofunds.memberaccounting.service.service.LoanRequestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LoanRequestEventHandlerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    @Mock
    LoanRequestService loanRequestService;

    @InjectMocks
    LoanRequestEventHandler loanRequestEventHandler;

    Fixture fixture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanRequestEventHandler = new LoanRequestEventHandler(loanRequestService, objectMapper);
        fixture = new Fixture();
    }

    @Test
    void testLoanRequestCreateEventHandlerSuccess() throws Exception {
        fixture.givenLoanRequestCreateEventIsProvided();
        fixture.givenCreateMessageStringIsProvided();
        fixture.givenLoanRequestServiceReturnsLoanRequestDto();

        fixture.whenConsumeCreateMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturned();
    }

    @Test
    void testLoanRequestCreateEventHandlerSuccessRuntimeException() throws Exception {
        fixture.givenLoanRequestCreateEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();

        fixture.whenConsumeCreateMessageVerifyExceptionIsThrown();
    }

    @Test
    void testGetFilteredAndSortedLoanRequestEventHandlerSuccess() throws Exception {
        fixture.givenGetFilteredAndSortedLoanRequestEventIsProvided();
        fixture.givenGetFilteredAndSortedLoanRequestEventMessageStringIsProvided();
        fixture.givenLoanRequestServiceReturnsFilteredAndSortedLoanRequestDto();

        fixture.whenConsumeGetFilteredLoanRequestMessageIsCalled();

        fixture.thenVerifyExpectedFilteredJsonResponseIsReturned();
    }

    @Test
    void testGetFilteredAndSortedLoanRequestEventHandlerSuccessRuntimeException() throws Exception {
        fixture.givenGetFilteredAndSortedLoanRequestEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();

        fixture.whenconsumeGetFilteredLoanRequestMessageVerifyExceptionIsThrown();
    }

    @Test
    void testGetAllFundingProposalByLoanRequestIdEventHandlerSuccess() throws Exception {
        fixture.givenGetAllFundingProposalByLoanRequestIdEventIsProvided();
        fixture.givenGetAllFundingProposalByLoanRequestIdEventMessageStringIsProvided();
        fixture.givenLoanRequestServiceReturnsFundingProposalDto();

        fixture.whenConsumeGetAllFundingProposalByLoanRequestIdMessageIsCalled();

        fixture.thenVerifyExpectedJsonResponseIsReturnedForFundingProposal();
    }

    @Test
    void testGetAllFundingProposalByLoanRequestIdEventHandlerSuccessRuntimeException() throws Exception {
        fixture.givenGetAllFundingProposalByLoanRequestIdEventIsProvided();
        fixture.givenMalformedMessageStringIsProvided();

        fixture.whenConsumeGetAllFundingProposalByLoanRequestIdMessageVerifyExceptionIsThrown();
    }


    private class Fixture{
        UUID LOAN_REQUEST_ID = UUID.randomUUID();
        SortDirection DIRECTION = SortDirection.ASC;
        SortBy SORT_BY = SortBy.SOLO_SCORE;
        Boolean SLP_ELIGIBLE = true;
        LoanRequestStatus STATUS = LoanRequestStatus.ACTIVE;
        LoanRequestCreateEvent loanRequestCreateEvent;
        GetFilteredAndSortedLoanRequestEvent getFilteredAndSortedLoanRequestEvent;
        GetAllFundingProposalByLoanRequestIdEvent getAllFundingProposalByLoanRequestIdEvent;
        Message<String> getRequest;
        Message<String> createRequest;
        Message<String> getFilteredLoanRequest;
        Message<String> getAllFundingProposalByLoanRequest;
        LoanRequestDto loanRequestDto;
        FundingProposalDto fundingProposalDto;
        String response;

        public void givenLoanRequestCreateEventIsProvided() {
            CreateLoanRequestDto createLoanRequestDto = new CreateLoanRequestDto();
            loanRequestCreateEvent=LoanRequestCreateEvent
                    .builder()
                    .createLoanRequest(createLoanRequestDto)
                    .build();
        }
        public void givenCreateMessageStringIsProvided() throws Exception {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString(loanRequestCreateEvent);
            createRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void givenLoanRequestServiceReturnsLoanRequestDto() {
            loanRequestDto = new LoanRequestDto();
            loanRequestDto.setLoanRequestId(LOAN_REQUEST_ID);
            when(loanRequestService.createLoanRequest(any(CreateLoanRequestDto.class))).thenReturn(loanRequestDto);
        }

        public void whenConsumeCreateMessageIsCalled() {
            response = loanRequestEventHandler.consumeCreateMessage(createRequest);
        }

        public void thenVerifyExpectedJsonResponseIsReturned() throws JsonProcessingException {
            assertNotNull(response);
            LoanRequestDto loanRequestDtoResponse = objectMapper.readValue(response, LoanRequestDto.class);
            assertEquals(LOAN_REQUEST_ID, loanRequestDtoResponse.getLoanRequestId());
        }

        public void givenGetFilteredAndSortedLoanRequestEventIsProvided() {
            getFilteredAndSortedLoanRequestEvent = GetFilteredAndSortedLoanRequestEvent
                    .builder()
                    .status(STATUS)
                    .sort(DIRECTION)
                    .sortBy(SORT_BY)
                    .slpEligible(SLP_ELIGIBLE)
                    .build();
        }

        public void givenGetFilteredAndSortedLoanRequestEventMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString(getFilteredAndSortedLoanRequestEvent);
            getFilteredLoanRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void givenLoanRequestServiceReturnsFilteredAndSortedLoanRequestDto() {
            loanRequestDto=new LoanRequestDto();
            loanRequestDto.setLoanRequestId(LOAN_REQUEST_ID);
            when(loanRequestService.getFilteredAndSortedEntities(STATUS, DIRECTION, SORT_BY,SLP_ELIGIBLE)).thenReturn(List.of(loanRequestDto));
        }

        public void whenConsumeGetFilteredLoanRequestMessageIsCalled() {
            response = loanRequestEventHandler.consumeGetFilteredLoanRequestMessage(getFilteredLoanRequest);
        }
        public void thenVerifyExpectedFilteredJsonResponseIsReturned() throws JsonProcessingException {
            assertNotNull(response);
            List<LoanRequestDto> loanRequestDtoResponse = objectMapper.readValue(response, new TypeReference<>(){});
            assertEquals(LOAN_REQUEST_ID, loanRequestDtoResponse.get(0).getLoanRequestId());
        }

        public void givenGetAllFundingProposalByLoanRequestIdEventIsProvided() {
            getAllFundingProposalByLoanRequestIdEvent = GetAllFundingProposalByLoanRequestIdEvent
                    .builder()
                    .id(LOAN_REQUEST_ID)
                    .build();
        }

        public void givenGetAllFundingProposalByLoanRequestIdEventMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString(getAllFundingProposalByLoanRequestIdEvent);
            getAllFundingProposalByLoanRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void givenLoanRequestServiceReturnsFundingProposalDto() {
            fundingProposalDto = new FundingProposalDto();
            fundingProposalDto.setLoanRequestId(LOAN_REQUEST_ID);
            when(loanRequestService.getAllFundingProposalByLoanRequestId(LOAN_REQUEST_ID)).thenReturn(List.of(fundingProposalDto));
        }

        public void whenConsumeGetAllFundingProposalByLoanRequestIdMessageIsCalled() {
            response = loanRequestEventHandler.consumeGetAllFundingProposalByLoanRequestIdMessage(getAllFundingProposalByLoanRequest);
        }

        public void thenVerifyExpectedJsonResponseIsReturnedForFundingProposal() throws JsonProcessingException {
            assertNotNull(response);
            List<FundingProposalDto> fundingProposalDtoList = objectMapper.readValue(response, new TypeReference<>(){});
            assertEquals(LOAN_REQUEST_ID, fundingProposalDtoList.get(0).getLoanRequestId());
        }

        public void givenMalformedMessageStringIsProvided() throws JsonProcessingException {
            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
            String json = objectMapper.writeValueAsString("test");
            getRequest = (Message<String>) simpleMessageConverter.toMessage(json, null);
        }

        public void whenConsumeCreateMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> loanRequestEventHandler.consumeCreateMessage(getRequest));
        }
        public void whenconsumeGetFilteredLoanRequestMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> loanRequestEventHandler.consumeGetFilteredLoanRequestMessage(getRequest));
        }
        public void whenConsumeGetAllFundingProposalByLoanRequestIdMessageVerifyExceptionIsThrown() {
            Assertions.assertThrows(Exception.class, () -> loanRequestEventHandler.consumeGetAllFundingProposalByLoanRequestIdMessage(getRequest));
        }
    }
}
