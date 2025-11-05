package pl.training.payments.adapters;

import pl.training.payments.application.AddTransactionUseCase;
import pl.training.payments.application.CardRepository;
import pl.training.payments.application.DateTimeProvider;
import pl.training.payments.application.TransactionEventPublisher;
import pl.training.payments.domain.CardNumber;
import pl.training.payments.domain.Money;
import pl.training.payments.domain.TransactionId;
import pl.training.payments.domain.TransactionType;

import java.util.logging.Logger;

public final class AddTransactionLoggingProxy extends AddTransactionUseCase {

    private static final Logger LOGGER = Logger.getLogger(AddTransactionLoggingProxy.class.getName());

    public AddTransactionLoggingProxy(final DateTimeProvider dateTimeProvider,
                                      final TransactionEventPublisher transactionEventPublisher,
                                      final CardRepository cardRepository) {
        super(dateTimeProvider, transactionEventPublisher, cardRepository);
    }

    @Override
    public TransactionId handle(CardNumber cardNumber, Money value, TransactionType transactionType) {
        LOGGER.info("----------------------------- Transaction start -----------------------------");
        try {
            var transactionId = super.handle(cardNumber, value, transactionType);
            LOGGER.info("Transaction on card %s successfully completed".formatted(cardNumber.value()));
            return transactionId;
        } catch (RuntimeException runtimeException) {
            LOGGER.info("Transaction on card %s failed with exception: %s"
                    .formatted(cardNumber.value(), runtimeException.getClass().getSimpleName()));
            throw runtimeException;
        } finally {
            LOGGER.info("------------------------------ Transaction end ------------------------------\n");
        }
    }

}
