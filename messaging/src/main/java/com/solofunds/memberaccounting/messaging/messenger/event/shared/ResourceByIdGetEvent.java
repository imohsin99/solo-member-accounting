package com.solofunds.memberaccounting.messaging.messenger.event.shared;

import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class ResourceByIdGetEvent extends Event implements Serializable {

    public static final String ACCOUNT_STATEMENT_GET_TOPIC = "accountStatement.get.requested";
    public static final String BALANCE_BY_WALLET_ACCOUNT_ID_GET_TOPIC = "solo.queue.wallet.balance.requested";
    public static final String COLLECTION_GET_TOPIC = "collection.get.requested";
    public static final String FEE_FOR_COLLECTION_GET_TOPIC = "feeForCollection.get.requested";
    public static final String FEE_FOR_LOAN_GET_TOPIC = "feesForLoan.get.requested";
    public static final String FUNDING_PROPOSAL_GET_TOPIC = "fundingProposal.get.requested";
    public static final String LOAN_GET_TOPIC = "loan.get.requested";
    public static final String LOAN_REQUEST_GET_TOPIC = "loanRequest.get.requested";
    public static final String MEMBER_TRANSACTION_GET_TOPIC = "memberTransaction.get.requested";
    public static final String PAYMENT_ORDER_BY_WALLET_ACCOUNT_GET_EVENT = "paymentOrder.get.requested";
    public static final String WALLET_ACCOUNT_GET_TOPIC = "walletAccount.get.requested";
    public static final String INTERCHANGE_NODE_GET_EVENT = "interchangeNode.get.requested";
    public static final String BANK_CARD_GET_EVENT = "bankCard.get.requested";

    private UUID id;
    private String topic;

    @Override
    public String getDestination() {
        return topic;
    }
}