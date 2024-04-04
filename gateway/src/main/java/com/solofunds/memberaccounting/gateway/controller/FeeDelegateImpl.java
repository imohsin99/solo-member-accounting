package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.FeesApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.fee.FeeForCollectionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.fee.FeeForLoanCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.model.CreateFeeDto;
import com.solofunds.memberaccounting.model.FeeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class FeeDelegateImpl implements FeesApiDelegate {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    private EventFactory eventFactory;

    /**
     * Retrieves fees associated with a specific loan ID.
     *
     * @param id The ID of the loan.
     * @return A ResponseEntity containing a list of FeeDto objects representing the fees.
     * @throws RuntimeException if an error occurs during the retrieval process.
     */
    @Override
    public ResponseEntity<List<FeeDto>> getFeeByLoanId(UUID id) {
        try {
            ResourceByIdGetEvent event = eventFactory.buildFeeForLoanGetEvent(id);
            List<FeeDto> feeDTOList =  objectMapper.readValue(event.publishAndWait(), new TypeReference<>() {});
            return ResponseEntity.ok(feeDTOList);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a fee associated with a specific collection ID.
     *
     * @param id The ID of the collection.
     * @param createFeeDto The CreateFeeDto object representing the fee to be created.
     * @return A ResponseEntity containing the created FeeDto object.
     * @throws RuntimeException if an error occurs during the creation process.
     */
    @Override
    public ResponseEntity<FeeDto> createFeeForCollections(UUID id, CreateFeeDto createFeeDto) {
        try {
            FeeForCollectionCreateEvent event = eventFactory.buildFeeForCollectionCreateEvent(id, createFeeDto);
            FeeDto fee = objectMapper.readValue(event.publishAndWait(), FeeDto.class);
            return ResponseEntity.ok(fee);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a fee associated with a specific loan ID.
     *
     * @param id The ID of the loan.
     * @param createFeeDto The CreateFeeDto object representing the fee to be created.
     * @return A ResponseEntity containing the created FeeDto object.
     * @throws RuntimeException if an error occurs during the creation process.
     */
    @Override
    public ResponseEntity<FeeDto> createFeeForLoan(UUID id, CreateFeeDto createFeeDto) {
        try {
            FeeForLoanCreateEvent event = eventFactory.buildFeeForLoanCreateEvent(id, createFeeDto);
            FeeDto fee = objectMapper.readValue(event.publishAndWait(), FeeDto.class);
            return ResponseEntity.ok(fee);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves fees associated with a specific collection ID.
     *
     * @param id The ID of the collection.
     * @return A ResponseEntity containing a list of FeeDto objects representing the fees.
     * @throws RuntimeException if an error occurs during the retrieval process.
     */
    @Override
    public ResponseEntity<List<FeeDto>> getFeeByCollectionsId(UUID id) {
        try {
            ResourceByIdGetEvent event = eventFactory.buildFeeForCollectionGetEvent(id);
            List<FeeDto> feeDTOList = objectMapper.readValue(event.publishAndWait(), new TypeReference<>() {});
            return ResponseEntity.ok(feeDTOList);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
