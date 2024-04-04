package com.solofunds.memberaccounting.service.service;

import com.solofunds.memberaccounting.model.CreatePaymentOrderDto;
import com.solofunds.memberaccounting.model.PaymentOrderDto;
import com.solofunds.memberaccounting.service.entities.MemberTransactions;
import com.solofunds.memberaccounting.service.entities.PaymentOrder;
import com.solofunds.memberaccounting.service.enums.Direction;
import com.solofunds.memberaccounting.service.mappers.PaymentOrderMapper;
import com.solofunds.memberaccounting.service.repositories.MemberTransactionsRepository;
import com.solofunds.memberaccounting.service.repositories.PaymentOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PaymentOrderServiceTest {

    @Mock
    PaymentOrder mockPaymentOrder;

    @Mock
    PaymentOrderRepository paymentOrderRepository;

    @Mock
    MemberTransactionsRepository memberTransactionsRepository;

    @InjectMocks
    PaymentOrderService paymentOrderService;

    @Test
    void testCreatePaymentOrderSuccess() throws Exception {
        fixture.givenValidPaymentOrderDtoIsMocked();
        fixture.givenCreatePaymentOrderDtoIsProvided();
        fixture.givenMemberTransactionIsProvided();
        fixture.givenMemberTransactionsRepositoryFindByIdIsCalled();
        fixture.givenSavePaymentOrderRepositoryIsCalled();
        fixture.whenCreatePaymentOrderServiceIsCalled();
        fixture.thenAssertSavedPaymentOrderDtoIsNotNull();
    }

    @Test
    void testPaymentOrderMapperSuccess() {
        fixture.givenCreatePaymentOrderDtoIsProvided();
        fixture.whenMapperToEntityIsCalled();
        fixture.thenAssertValuesMappedCorrectly();
    }

    @Test
    void getPaymentOrdersByWalletAccountIdTest() {
        fixture.givenFindAllByWalletAccountIdIsCalled();
        fixture.whenGetPaymentOrdersReturnsIsCalled();
        fixture.thenAssertGetPaymentOrdersReturnsExpectedResult();
    }

    Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
    }

    @Nested
    private class Fixture {
        final UUID MEMBER_TRANSACTION_ID = UUID.randomUUID();
        final UUID LEDGER_TRANSACTION_ID = UUID.randomUUID();
        final UUID WALLET_ACCOUNT_ID = UUID.randomUUID();

        CreatePaymentOrderDto createPaymentOrderDto;
        PaymentOrderDto paymentOrderDto;

        PaymentOrder paymentOrder;
        PaymentOrderDto savedPaymentOrderDto;
        MemberTransactions memberTransactions;
        List<PaymentOrder> paymentOrders = new ArrayList<>();
        List<PaymentOrderDto> paymentOrderDtos = new ArrayList<>();
        void givenValidPaymentOrderDtoIsMocked() { }

        void givenCreatePaymentOrderDtoIsProvided() {
            createPaymentOrderDto = new CreatePaymentOrderDto();
            createPaymentOrderDto.setMemberTransactionId(MEMBER_TRANSACTION_ID);
            createPaymentOrderDto.setLedgerTransactionId(LEDGER_TRANSACTION_ID);
        }

        void givenMemberTransactionIsProvided() {
            memberTransactions = new MemberTransactions();
            memberTransactions.setAmount(BigDecimal.ONE);
            memberTransactions.setDirection(Direction.CREDIT);
        }

        void givenSavePaymentOrderRepositoryIsCalled() {
            when(paymentOrderRepository.save(any(PaymentOrder.class))).thenReturn(mockPaymentOrder);
        }

        void givenMemberTransactionsRepositoryFindByIdIsCalled() {
            when(memberTransactionsRepository.findById(MEMBER_TRANSACTION_ID)).thenReturn(Optional.of(memberTransactions));
        }

        void whenCreatePaymentOrderServiceIsCalled() throws Exception{
            savedPaymentOrderDto = paymentOrderService.createPaymentOrder(createPaymentOrderDto);
        }

        void whenMapperToEntityIsCalled() {
            paymentOrder = PaymentOrderMapper.MAPPER.toEntity(createPaymentOrderDto);
        }

        void givenFindAllByWalletAccountIdIsCalled() {
            paymentOrders.add(mockPaymentOrder);
            when(paymentOrderRepository.findByOriginatingWalletIdOrReceivingWalletId(any(UUID.class), any(UUID.class))).thenReturn(paymentOrders);
        }

        void whenGetPaymentOrdersReturnsIsCalled() {
            paymentOrderDtos = paymentOrderService.getPaymentOrdersByWalletAccountId(WALLET_ACCOUNT_ID);
        }

        void thenAssertSavedPaymentOrderDtoIsNotNull() {
            assertNotNull(savedPaymentOrderDto);
        }

        void thenAssertValuesMappedCorrectly() {
            assertEquals(LEDGER_TRANSACTION_ID, paymentOrder.getLedgerTransactionId());
        }

        void thenAssertGetPaymentOrdersReturnsExpectedResult() {
            assertEquals(1, paymentOrderDtos.size());
        }
    }
}
