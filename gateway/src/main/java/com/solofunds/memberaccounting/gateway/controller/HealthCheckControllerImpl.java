package com.solofunds.memberaccounting.gateway.controller;

import com.solofunds.memberaccounting.api.SolobankApiDelegate;
import com.solofunds.memberaccounting.messaging.messenger.event.EventFactory;
import com.solofunds.memberaccounting.messaging.messenger.event.healthCheck.HealthCheckEvent;
import com.solofunds.memberaccounting.model.HealthCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HealthCheckControllerImpl implements SolobankApiDelegate {


    @Autowired
    private EventFactory eventFactory;

    @Override
    public ResponseEntity<HealthCheck> healthCheck() {
        HealthCheck hcRet = new HealthCheck();
        try {
            HealthCheckEvent healthCheckEvent = eventFactory.buildHealthCheckEvent();
            String res = healthCheckEvent.publishAndWait();
            hcRet.setHealthCheckValue("Healthcheck response: " + res);
            return ResponseEntity.ok(hcRet);
        } catch (Exception e) {
            String err = "Exception encountered during healthcheck: ";
            log.error(err, e);
            hcRet.setHealthCheckValue(err + e);
            return new ResponseEntity<>(hcRet, HttpStatus.BAD_REQUEST);
        }
    }



}
