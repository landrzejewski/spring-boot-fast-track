package pl.training.payments.domain;

import org.junit.jupiter.api.Test;
import pl.training.payments.CardTestFixtures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.training.payments.CardTestFixtures.*;
import static pl.training.payments.domain.TransactionType.INFLOW;
import static pl.training.payments.domain.TransactionType.PAYMENT;

class CardTest {

    private final Card card = CardTestFixtures.validCard();

    @Test
    void given_valid_inflow_transaction_when_register_should_increase_card_balance() {
        var inflow = new Transaction(TEST_CARD_TRANSACTION_ID, TEST_DATE_TIME, TEST_MONEY, INFLOW);
        card.registerTransaction(inflow);
        assertEquals(inflow.value(), card.getBalance());
    }

    @Test
    void given_card_with_insufficient_balance_when_try_register_payment_transaction_should_throw_exception() {
        var payment = new Transaction(TEST_CARD_TRANSACTION_ID, TEST_DATE_TIME, TEST_MONEY, PAYMENT);
        assertThrows(InsufficientBalanceException.class, () -> card.registerTransaction(payment));
    }

}
