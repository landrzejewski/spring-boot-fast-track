package pl.training.payments.application;

import org.springframework.transaction.annotation.Transactional;
import pl.training.common.aop.Loggable;
import pl.training.payments.domain.*;

import java.util.function.Consumer;

@Transactional
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

    @Loggable
    public TransactionId handle(final CardNumber cardNumber, final Money value, final TransactionType transactionType) {
        var card = findCard(cardNumber);
        var transaction = createTransaction(value, transactionType);
        var cardEventListener = createCardEventListener();
        card.addEventListener(cardEventListener);
        card.registerTransaction(transaction);
        card.removeEventListener(cardEventListener);
        cardRepository.save(card);
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

}
