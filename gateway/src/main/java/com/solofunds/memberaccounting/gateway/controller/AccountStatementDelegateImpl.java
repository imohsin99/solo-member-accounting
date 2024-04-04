package com.solofunds.memberaccounting.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.api.AccountStatementsApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.accountStatement.*;
import com.solofunds.memberaccounting.model.AccountStatementDto;
import com.solofunds.memberaccounting.model.CreateAccountStatementDto;
import com.solofunds.memberaccounting.model.Format;
import com.solofunds.memberaccounting.model.MemberDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class AccountStatementDelegateImpl implements AccountStatementsApiDelegate {
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Autowired
    EventFactory eventFactory;

    @Override
    public ResponseEntity<Object> getAccountStatementById(UUID id, Format format, MemberDataDto memberDataDTO) {
         if (Format.PDF.equals(format) && memberDataDTO == null) {
            throw new RuntimeException("Member data does not exists");
        }

        AccountStatementGetEvent event = eventFactory.buildAccountStatementGetEvent(id, memberDataDTO, format);
        AccountStatementDto statementDTO = event.publishAndReceive(AccountStatementDto.class);

        if(Format.CSV.equals(format)) {
            ByteArrayInputStream stream = new ByteArrayInputStream(statementDTO.getDocument());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=statement.csv");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/csv"))
                    .body(new InputStreamResource(stream));
        } else if(Format.PDF.equals(format))  {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=statement.pdf");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new ByteArrayInputStream(statementDTO.getDocument())));
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(statementDTO);
        }
    }

    @Override
    public ResponseEntity<AccountStatementDto> createAccountStatement(UUID walletAccountId, CreateAccountStatementDto createAccountStatementDTO) {
        try {
            AccountStatementCreateEvent event = eventFactory.buildAccountStatementCreateEvent(walletAccountId, createAccountStatementDTO);
            AccountStatementDto statementDTO = event.publishAndReceive(AccountStatementDto.class);
            return ResponseEntity.ok(statementDTO);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<AccountStatementDto> deleteAccountStatementById(UUID id) {
        try {
            AccountStatementDeleteEvent event = eventFactory.buildAccountStatementDeleteEvent(id);
            AccountStatementDto statementDTO = event.publishAndReceive(AccountStatementDto.class);
            return ResponseEntity.ok(statementDTO);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<AccountStatementDto>> getAllAccountStatements() {
        try {
            AccountStatementGetAllEvent event = eventFactory.buildAccountStatementGetAllEvent();
            Class<List<AccountStatementDto>> clazz = (Class) List.class;
            List<AccountStatementDto> statementDTOS = event.publishAndReceive(clazz);

            return ResponseEntity.ok(statementDTOS);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<AccountStatementDto>> getAllAccountStatementsByWalletAccountId(UUID id) {
        try {
            AccountStatementGetByWalletIdEvent event = eventFactory.buildAccountStatementGetByWalletIdEvent(id);
            Class<List<AccountStatementDto>> clazz = (Class) List.class;
            List<AccountStatementDto> statementDTOS = event.publishAndReceive(clazz);
            return ResponseEntity.ok(statementDTOS);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
