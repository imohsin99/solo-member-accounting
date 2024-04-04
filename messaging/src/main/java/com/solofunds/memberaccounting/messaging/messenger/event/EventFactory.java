package com.solofunds.memberaccounting.messaging.messenger.event;

import com.solofunds.memberaccounting.messaging.messenger.MessagePublisher;
import com.solofunds.memberaccounting.messaging.messenger.event.accountStatement.*;
import com.solofunds.memberaccounting.messaging.messenger.event.balance.CalculateStartingBalanceEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.bankCard.BankCardCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.collections.CollectionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.fee.FeeForCollectionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.fee.FeeForLoanCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal.FundingProposalCreatedEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.fundingProposal.FundingProposalUpdateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.healthCheck.HealthCheckEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.interchangeNode.InterchangeNodeCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerByLoanIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerByMemberIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.ledger.LedgerEntryCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanGetAllByMemberIdAndStatusEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loan.LoanPatchEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loanRequest.GetFilteredAndSortedLoanRequestEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.loanRequest.LoanRequestCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.memberTransaction.MemberTransactionCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.paymentOrder.PaymentOrderCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.shared.ResourceByIdGetEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.transaction.error.TransactionErrorCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.transaction.error.TransactionErrorGetByWalletIdEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.walletAccount.WalletAccountCreateEvent;
import com.solofunds.memberaccounting.messaging.messenger.event.walletAccount.WalletAccountGetAllByMemberIdEvent;
import com.solofunds.memberaccounting.messaging.messenger.loanRequestEvents.GetAllFundingProposalByLoanRequestIdEvent;
import com.solofunds.memberaccounting.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventFactory {

    @Autowired
    private MessagePublisher<Event> messagePublisher;

    public HealthCheckEvent buildHealthCheckEvent() {
        return HealthCheckEvent.builder()
                .healthCheckValue("test")
                .publisher(messagePublisher)
                .build();
    }

    public AccountStatementGetEvent buildAccountStatementGetEvent(UUID id, MemberDataDto memberDataDto, Format format) {
        return AccountStatementGetEvent.builder()
                .publisher(messagePublisher)
                .id(id)
                .memberDataDto(memberDataDto)
                .format(format)
                .build();
    }

    public AccountStatementCreateEvent buildAccountStatementCreateEvent(UUID walletAccountId, CreateAccountStatementDto createAccountStatementDto) {
        return AccountStatementCreateEvent.builder()
                .publisher(messagePublisher)
                .walletAccountId(walletAccountId)
                .createAccountStatementDto(createAccountStatementDto)
                .build();
    }

    public AccountStatementDeleteEvent buildAccountStatementDeleteEvent(UUID id) {
        return AccountStatementDeleteEvent.builder()
                .publisher(messagePublisher)
                .id(id)
                .build();
    }

    public AccountStatementGetAllEvent buildAccountStatementGetAllEvent() {
        return AccountStatementGetAllEvent.builder()
                .publisher(messagePublisher)
                .build();
    }

    public AccountStatementGetByWalletIdEvent buildAccountStatementGetByWalletIdEvent(UUID id) {
        return AccountStatementGetByWalletIdEvent.builder()
                .publisher(messagePublisher)
                .walletAccountId(id)
                .build();
    }

    public ResourceByIdGetEvent buildBalanceByWalletAccountIdGetEvent(UUID walletAccountId) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.BALANCE_BY_WALLET_ACCOUNT_ID_GET_TOPIC)
                .id(walletAccountId)
                .build();
    }

    public CalculateStartingBalanceEvent buildCalculateStartingBalanceEvent(CalculateStartingBalanceDto calculateStartingBalanceDto) {
        return CalculateStartingBalanceEvent.builder()
                .publisher(messagePublisher)
                .calculateStartingBalanceDto(calculateStartingBalanceDto)
                .build();
    }

    public CollectionCreateEvent buildCollectionCreateEvent(CreateCollectionsDto createCollectionsDto) {
        return CollectionCreateEvent.builder()
                .publisher(messagePublisher)
                .createCollectionsDTO(createCollectionsDto)
                .build();
    }

    public ResourceByIdGetEvent buildCollectionGetById(UUID id) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.COLLECTION_GET_TOPIC)
                .id(id)
                .build();
    }

    public FeeForCollectionCreateEvent buildFeeForCollectionCreateEvent(UUID id, CreateFeeDto createFeeDto) {
        return FeeForCollectionCreateEvent.builder()
                .publisher(messagePublisher)
                .id(id)
                .createFeeDto(createFeeDto)
                .build();
    }

    public ResourceByIdGetEvent buildFeeForCollectionGetEvent(UUID id) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.FEE_FOR_COLLECTION_GET_TOPIC)
                .id(id)
                .build();
    }

    public FeeForLoanCreateEvent buildFeeForLoanCreateEvent(UUID id, CreateFeeDto createFeeDto) {
        return FeeForLoanCreateEvent.builder()
                .publisher(messagePublisher)
                .id(id)
                .createFeeDto(createFeeDto)
                .build();
    }

    public ResourceByIdGetEvent buildFeeForLoanGetEvent(UUID loanId) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.FEE_FOR_LOAN_GET_TOPIC)
                .id(loanId)
                .build();
    }

    public FundingProposalCreatedEvent buildFundingProposalCreatedEvent(CreateFundingProposalDto createFundingProposalDto) {
        return FundingProposalCreatedEvent.builder()
                .publisher(messagePublisher)
                .createFundingProposalDto(createFundingProposalDto)
                .build();
    }

    public ResourceByIdGetEvent buildFundingProposalGetEvent(UUID id) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.FUNDING_PROPOSAL_GET_TOPIC)
                .id(id)
                .build();
    }

    public FundingProposalUpdateEvent buildFundingProposalUpdateEvent(UUID id, UpdateFundingProposalDto updateFundingProposalDto) {
        return FundingProposalUpdateEvent.builder()
                .publisher(messagePublisher)
                .id(id)
                .fundingProposalDto(updateFundingProposalDto)
                .build();
    }

    public GetAllFundingProposalByLoanRequestIdEvent buildGetAllFundingProposalByLoanRequestIdEvent(UUID id){
        return GetAllFundingProposalByLoanRequestIdEvent.builder()
                .publisher(messagePublisher)
                .id(id)
                .build();
    }

    public LoanCreateEvent buildLoanCreateEvent(CreateLoanDto createLoanDto) {
        return LoanCreateEvent.builder()
                .publisher(messagePublisher)
                .createLoanDto(createLoanDto)
                .build();
    }

    public LoanPatchEvent buildLoanPatchEvent(UUID id, UpdateLoanDto updateLoanDTO) {
        return LoanPatchEvent.builder()
                .publisher(messagePublisher)
                .id(id)
                .updateLoanDTO(updateLoanDTO)
                .build();
    }

    public ResourceByIdGetEvent buildGetLoanById(UUID loanId) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.LOAN_GET_TOPIC)
                .id(loanId)
                .build();
    }

    public LoanGetAllByMemberIdAndStatusEvent buildLoansByMemberAccountIdGetEvent(UUID memberAccountId,LoanStatus status ) {
        return LoanGetAllByMemberIdAndStatusEvent.builder()
                .publisher(messagePublisher)
                .id(memberAccountId)
                .status(status)
                .build();
    }

    public GetFilteredAndSortedLoanRequestEvent buildGetFilteredAndSortedLoanRequestEvent(LoanRequestStatus loanRequestStatus, SortDirection sortDirection, SortBy sortBy, Boolean slpEligible) {
        return GetFilteredAndSortedLoanRequestEvent.builder()
                .publisher(messagePublisher)
                .sort(sortDirection)
                .sortBy(sortBy)
                .slpEligible(slpEligible)
                .status(loanRequestStatus)
                .build();
    }

    public LoanRequestCreateEvent buildLoanRequestCreateEvent(CreateLoanRequestDto createLoanRequestDto) {
        return LoanRequestCreateEvent.builder()
                .publisher(messagePublisher)
                .createLoanRequest(createLoanRequestDto)
                .build();
    }

    public ResourceByIdGetEvent buildLoanRequestGetEvent(UUID id) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.LOAN_REQUEST_GET_TOPIC)
                .id(id)
                .build();
    }

    public MemberTransactionCreateEvent buildMemberTransactionCreateEvent(CreateMemberTransactionDto createMemberTransactionDto) {
        return MemberTransactionCreateEvent.builder()
                .publisher(messagePublisher)
                .createMemberTransactionDto(createMemberTransactionDto)
                .build();
    }

    public ResourceByIdGetEvent buildMemberTransactionGetEvent(UUID id) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.MEMBER_TRANSACTION_GET_TOPIC)
                .id(id)
                .build();
    }

    public PaymentOrderCreateEvent buildPaymentOrderCreateEvent(CreatePaymentOrderDto createPaymentOrderDto) {
        return PaymentOrderCreateEvent.builder()
                .publisher(messagePublisher)
                .createPaymentOrderDto(createPaymentOrderDto)
                .build();
    }

    public ResourceByIdGetEvent buildPaymentOrderByWalletAccountIdGetEvent(UUID walletAccountId) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.PAYMENT_ORDER_BY_WALLET_ACCOUNT_GET_EVENT)
                .id(walletAccountId)
                .build();
    }

    public WalletAccountCreateEvent buildWalletAccountCreateEvent(CreateWalletAccountDto createWalletAccountDto) {
        return WalletAccountCreateEvent.builder()
                .publisher(messagePublisher)
                .createWalletAccountDto(createWalletAccountDto)
                .build();
    }

    public WalletAccountGetAllByMemberIdEvent buildWalletAccountGetAllByMemberIdEvent(UUID memberId) {
        return WalletAccountGetAllByMemberIdEvent.builder()
                .publisher(messagePublisher)
                .memberId(memberId)
                .build();
    }

    public ResourceByIdGetEvent buildWalletAccountGetEvent(UUID id) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.WALLET_ACCOUNT_GET_TOPIC)
                .id(id)
                .build();
    }

    public TransactionErrorGetByWalletIdEvent buildTransactionErrorGetByWalletIdEvent(UUID id) {
        return TransactionErrorGetByWalletIdEvent.builder()
                .publisher(messagePublisher)
                .walletAccountId(id)
                .build();
    }

    public TransactionErrorCreateEvent buildTransactionErrorCreateEvent(TransactionErrorCodeDTO errorCodeDTO){
        return TransactionErrorCreateEvent.builder()
                .publisher(messagePublisher)
                .transactionErrorCodeCapture(errorCodeDTO)
                .build();
    }

    public LedgerByLoanIdGetEvent buildLedgerByLoanIdGetEvent(UUID loanId) {
        return LedgerByLoanIdGetEvent.builder().publisher(messagePublisher).loan_id(loanId).build();
    }


    public LedgerByMemberIdGetEvent buildLedgerByMemberIdGetEvent(UUID memberGuid) {
        return LedgerByMemberIdGetEvent.builder().publisher(messagePublisher).memberId(memberGuid).build();
    }


    public LedgerEntryCreateEvent buildLedgerEntryCreateEvent(UUID loanId, LedgerEntryDto ledgerEntryDto) {
        return LedgerEntryCreateEvent.builder().publisher(messagePublisher).loan_id(loanId).ledgerEntryToCreate(ledgerEntryDto).build();
    }

    public InterchangeNodeCreateEvent buildInterchangeNodeCreateEvent(CreateInterchangeNodeDto createInterchangeNodeDto) {
        return InterchangeNodeCreateEvent.builder()
                .publisher(messagePublisher)
                .createInterchangeNodeDto(createInterchangeNodeDto)
                .build();
    }

    public ResourceByIdGetEvent buildInterchangeNodeGetEvent(UUID interchangeNodeId) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.INTERCHANGE_NODE_GET_EVENT)
                .id(interchangeNodeId)
                .build();
    }

    public BankCardCreateEvent buildBankCardCreateEvent(CreateBankCardDto createBankCardDto) {
        return BankCardCreateEvent.builder()
                .publisher(messagePublisher)
                .createBankCardDto(createBankCardDto)
                .build();
    }

    public ResourceByIdGetEvent buildBankCardGetEvent(UUID bankCardId) {
        return ResourceByIdGetEvent.builder()
                .publisher(messagePublisher)
                .topic(ResourceByIdGetEvent.BANK_CARD_GET_EVENT)
                .id(bankCardId)
                .build();
    }
}
