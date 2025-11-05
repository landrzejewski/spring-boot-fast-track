package pl.training.payments;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.training.payments.adapters.HashMapCardRepository;
import pl.training.payments.application.*;

@Configuration
public class PaymentsConfiguration {

    // @Scope("prototype")
    @Bean // (initMethod = "", destroyMethod = "", name = {"addCardUseCase", "addCard"})
    public AddCardUseCase addCardUseCase(/*@Qualifier("sequentialCardNumberGenerator")*/ CardNumberGenerator cardNumberGenerator,
                                         CardRepository cardRepository, DateTimeProvider dateTimeProvider) {
        return new AddCardUseCase(cardNumberGenerator, cardRepository, dateTimeProvider);
    }

    @Bean
    public AddTransactionUseCase  addTransactionUseCase(DateTimeProvider dateTimeProvider, TransactionEventPublisher transactionEventPublisher,
                                                        CardRepository cardRepository) {
        return new AddTransactionUseCase(dateTimeProvider, transactionEventPublisher, cardRepository);
    }

    @Bean
    public GetCardsUseCase getCardsUseCase(CardRepository cardRepository) {
        return new GetCardsUseCase(cardRepository);
    }

    @Bean
    public GetCardUseCase getCardUseCase() {
        return new GetCardUseCase(cardRepository());
    }

    @Bean
    public CardRepository cardRepository() {
        return new HashMapCardRepository();
    }

}
