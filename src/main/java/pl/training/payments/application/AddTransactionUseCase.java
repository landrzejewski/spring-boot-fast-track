package pl.training.payments.application;

import pl.training.payments.domain.*;
import pl.training.payments.domain.Money;

import java.util.function.Consumer;

public class AddTransactionUseCase {

    private final DateTimeProvider dateTimeProvider;
    private final TransactionEventPublisher transactionEventPublisher;
    private final CardRepository cardRepository;

    public AddTransactionUseCase(final DateTimeProvider dateTimeProvider,
                                 final TransactionEventPublisher transactionEventPublisher,
                                 final CardRepository cardRepository) {
        this.dateTimeProvider = dateTimeProvider;
        this.transactionEventPublisher = transactionEventPublisher;
        this.cardRepository = cardRepository;
    }

    public TransactionId handle(final CardNumber cardNumber, final Money value, final TransactionType transactionType) {
        var card = findCard(cardNumber);
        var transaction = createTransaction(value, transactionType);
        var cardEventListener = createCardEventListener();
        addTransactionToCard(card, transaction, cardEventListener);
        return transaction.id();
    }

    private Card findCard(final CardNumber cardNumber) {
        return cardRepository.findByNumber(cardNumber)
                .orElseThrow(CardNotFoundException::new);
    }

    private Transaction createTransaction(final Money value, final TransactionType transactionType) {
        return new Transaction(new TransactionId(), dateTimeProvider.getZonedDateTime(), value, transactionType);
    }

    private Consumer<TransactionRegistered> createCardEventListener() {
        return event -> {
            var cardNumber = event.cardNumber().value();
            var transactionId = event.transaction().id().value().toString();
            var transactionType = event.transaction().type().name();
            var applicationEvent = new TransactionAdded(cardNumber, transactionId, transactionType);
            transactionEventPublisher.publish(applicationEvent);
        };
    }

    private void addTransactionToCard(Card card, Transaction transaction, Consumer<TransactionRegistered> cardEventListener) {
        card.addEventListener(cardEventListener);
        card.registerTransaction(transaction);
        card.removeEventListener(cardEventListener);
        cardRepository.save(card);
    }

}

/*public class AddTransactionUseCase {

    private static final Logger LOGGER = Logger.getLogger(AddTransactionUseCase.class.getName());

    private final DateTimeProvider dateTimeProvider = new SystemDateTimeProvider();
    private final TransactionEventPublisher transactionEventPublisher = new ConsoleTransactionEventPublisher();
    private final CardRepository cardRepository = new HashMapCardRepository();

    public TransactionId handle(final CardNumber cardNumber, final Money value, final TransactionType transactionType) {
        LOGGER.info("----------------------------- Transaction start -----------------------------");
        try {
            var card = findCard(cardNumber);
            var transaction = createTransaction(value, transactionType);
            var cardEventListener = createCardEventListener();
            addTransactionToCard(card, transaction, cardEventListener);
            LOGGER.info("Transaction on card %s successfully completed".formatted(cardNumber.value()));
            return transaction.id();
        } catch (RuntimeException exception) {
            LOGGER.info("Transaction on card %s failed with exception: %s".formatted(cardNumber.value(), exception.getClass().getSimpleName()));
            throw exception;
        }
        finally {
            LOGGER.info("------------------------------ Transaction end ------------------------------\n");
        }
    }

    private Card findCard(final CardNumber cardNumber) {
        return cardRepository.findByNumber(cardNumber)
                .orElseThrow(CardNotFoundException::new);
    }

    private Transaction createTransaction(final Money value, final TransactionType transactionType) {
        return new Transaction(new TransactionId(), dateTimeProvider.getZonedDateTime(), value, transactionType);
    }

    private Consumer<TransactionRegistered> createCardEventListener() {
        return event -> {
            var cardNumber = event.cardNumber().value();
            var transactionId = event.transaction().id().value().toString();
            var transactionType = event.transaction().type().name();
            var applicationEvent = new TransactionAdded(cardNumber, transactionId, transactionType);
            transactionEventPublisher.publish(applicationEvent);
        };
    }

    private void addTransactionToCard(Card card, Transaction transaction, Consumer<TransactionRegistered> cardEventListener) {
        card.addEventListener(cardEventListener);
        card.registerTransaction(transaction);
        card.removeEventListener(cardEventListener);
        cardRepository.save(card);
    }

}
*/