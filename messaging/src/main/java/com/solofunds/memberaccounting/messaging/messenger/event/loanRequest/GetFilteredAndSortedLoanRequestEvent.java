package com.solofunds.memberaccounting.messaging.messenger.event.loanRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import com.solofunds.memberaccounting.model.LoanRequestStatus;
import com.solofunds.memberaccounting.model.SortBy;
import com.solofunds.memberaccounting.model.SortDirection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class GetFilteredAndSortedLoanRequestEvent extends Event {
    @JsonIgnore
    public static final String TOPIC = "filteredAndSortedLoanRequest.get.requested";

    private LoanRequestStatus status;
    private SortDirection sort;
    private SortBy sortBy;
    private Boolean slpEligible;

    @Override
    public String getDestination() {
        return TOPIC;
    }
}
