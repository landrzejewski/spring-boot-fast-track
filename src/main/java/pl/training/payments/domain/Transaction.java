package pl.training.payments.domain;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Currency;

public record Transaction(TransactionId id, ZonedDateTime timestamp, Money value, TransactionType type) {

    public boolean hasCurrency(final Currency currency) {
        return value.currency()
                .equals(currency);
    }

    public boolean isBefore(final LocalDate date) {
        return timestamp.toLocalDate()
                .isBefore(date);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id.value() +
                ", timestamp=" + timestamp +
                ", value=" + value +
                ", type=" + type +
                '}';
    }

}
