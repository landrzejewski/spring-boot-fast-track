package pl.training.payments;

import pl.training.payments.adapters.*;
import pl.training.payments.application.*;

public class PaymentsConfiguration {

    private final CardRepository cardRepository = new HashMapCardRepository();
    private final CardNumberGenerator cardNumberGenerator = new SequentialCardNumberGenerator(16);
    // private final CardNumberGenerator cardNumberGenerator = new RandomCardNumberGenerator(16);
    private final DateTimeProvider dateTimeProvider = new SystemDateTimeProvider();
    private final TransactionEventPublisher transactionEventPublisher = new ConsoleTransactionEventPublisher();

    public AddCardUseCase addCardUseCase() {
        return new AddCardUseCase(cardNumberGenerator, dateTimeProvider, cardRepository);
    }

    public GetCardsUseCase getCardsUseCase() {
        return new GetCardsUseCase(cardRepository);
    }

    public GetCardUseCase getCardUseCase() {
        return new GetCardUseCase(cardRepository);
    }

    public AddTransactionUseCase addTransactionUseCase() {
        return new AddTransactionUseCase(dateTimeProvider, transactionEventPublisher, cardRepository);
        // return new AddTransactionLoggingProxy(dateTimeProvider, transactionEventPublisher, cardRepository);
    }

}
