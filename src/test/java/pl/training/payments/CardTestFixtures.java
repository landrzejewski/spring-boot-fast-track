package pl.training.payments;

import pl.training.payments.domain.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Currency;

public class CardTestFixtures {

    public static final ZonedDateTime TEST_DATE_TIME = ZonedDateTime.now();
    public static final Currency TEST_CURRENCY = Currency.getInstance("PLN");
    public static final Money TEST_MONEY = new Money(100.0, TEST_CURRENCY.getCurrencyCode());
    public static final CardId TEST_CARD_ID = new CardId();
    public static final CardNumber TEST_CARD_NUMBER = new CardNumber("4237251412344005");
    public static final LocalDate TEST_EXPIRATION_DATE = LocalDate.now().plusYears(1);
    public static final TransactionId TEST_CARD_TRANSACTION_ID = new TransactionId();

    public static Card validCard() {
        return new Card(TEST_CARD_ID, TEST_CARD_NUMBER, TEST_EXPIRATION_DATE, TEST_CURRENCY);
    }

}
