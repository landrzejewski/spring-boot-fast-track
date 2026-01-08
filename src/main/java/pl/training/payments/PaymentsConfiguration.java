package pl.training.payments;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pl.training.payments.adapters.RandomCardNumberGenerator;
import pl.training.payments.adapters.SequentialCardNumberGenerator;
import pl.training.payments.application.*;

// @Profile("dev")
@Configuration
public class PaymentsConfiguration {

    // @Scope("prototype")
    @Bean(name = {"addCardUseCase", "addCard"}, initMethod = "init", destroyMethod = "destroy")
    public AddCardUseCase addCardUseCase(/*@Qualifier("randomCardNumberGenerator")*/ CardNumberGenerator cardNumberGenerator, DateTimeProvider dateTimeProvider, CardRepository cardRepository) {
        // var addCardUseCase = new AddCardUseCase(randomCardNumberGenerator(16), dateTimeProvider);
        var addCardUseCase = new AddCardUseCase(cardNumberGenerator, dateTimeProvider);
        addCardUseCase.setCardRepository(cardRepository);
        return addCardUseCase;
    }

    @Bean
    public AddTransactionUseCase  addTransactionUseCase(DateTimeProvider timeProvider, TransactionEventPublisher transactionEventPublisher, CardRepository cardRepository) {
        return new AddTransactionUseCase(timeProvider, transactionEventPublisher, cardRepository);
    }

    @Bean
    public GetCardsUseCase getCardsUseCase(CardRepository cardRepository) {
        return new GetCardsUseCase(cardRepository);
    }

    @Bean
    public GetCardUseCase getCardUseCase(CardRepository cardRepository) {
        return new GetCardUseCase(cardRepository);
    }

    @Primary
    @Bean
    public CardNumberGenerator randomCardNumberGenerator(@Value("${card-number-length}") int length) {
        return new RandomCardNumberGenerator(length);
    }

    // @Profile("dev")
    @Bean
    public CardNumberGenerator sequentialCardNumberGenerator(@Value("${card-number-length}") int length) {
        return  new SequentialCardNumberGenerator(length);
    }

}
