package pl.training.payments.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;

import static java.math.BigDecimal.ZERO;
import static pl.training.payments.domain.TransactionType.INFLOW;

public final class Card {

    private final CardId id;
    private final CardNumber number;
    private final LocalDate expiration;
    private final Currency currency;
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<Consumer<TransactionRegistered>> eventListeners = new ArrayList<>();

    private Money balance;

    public Card(final CardId id, final CardNumber number, final LocalDate expiration, final Currency currency) {
        this.id = id;
        this.number = number;
        this.expiration = expiration;
        this.currency = currency;
        this.balance = new Money(ZERO, currency);
    }

    public void registerTransaction(final Transaction transaction) {
        validate(transaction);
        commit(transaction);
        publishEvents(transaction);
    }

    private void validate(final Transaction transaction) {
        if (!hasValidTimestamp(transaction)) {
            throw new InvalidTransactionTimestampException();
        }
        if (!hasValidCurrency(transaction)) {
            throw new MismatchedCurrencyException();
        }
        if (!balanceIsSufficient(transaction)) {
            throw new InsufficientBalanceException();
        }
    }

    private boolean hasValidTimestamp(final Transaction transaction) {
        return transaction.isBefore(expiration);
    }

    private boolean hasValidCurrency(final Transaction transaction) {
        return transaction.hasCurrency(currency);
    }

    private boolean balanceIsSufficient(final Transaction transaction) {
        return transaction.type() == INFLOW || balance.isGreaterOrEqual(transaction.value());
    }

    private void commit(final Transaction transaction) {
        transactions.add(transaction);
        updateBalance(transaction);
    }

    private void updateBalance(final Transaction transaction) {
        var transactionValue = transaction.value();
        balance = switch (transaction.type()) {
            case INFLOW -> balance.add(transactionValue);
            case PAYMENT -> balance.subtract(transactionValue);
        };
    }

    private void publishEvents(final Transaction transaction) {
        var event = new TransactionRegistered(number, transaction);
        eventListeners.forEach(listener -> listener.accept(event));
    }

    public void addEventListener(final Consumer<TransactionRegistered> listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(final Consumer<TransactionRegistered> listener) {
        eventListeners.remove(listener);
    }

    public CardId getId() {
        return id;
    }

    public CardNumber getNumber() {
        return number;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id.value() +
                ", number=" + number.value() +
                ", expiration=" + expiration +
                ", currency=" + currency +
                ", balance=" + balance +
                '}';
    }

}
