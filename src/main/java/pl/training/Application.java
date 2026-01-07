package pl.training;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.training.common.PageSpec;
import pl.training.payments.application.*;
import pl.training.payments.domain.Money;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static pl.training.payments.domain.TransactionType.INFLOW;
import static pl.training.payments.domain.TransactionType.PAYMENT;

@SpringBootApplication
public class Application implements ApplicationRunner {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
    private static final Currency CURRENCY = Currency.getInstance("PLN");

    private final AddCardUseCase addCardUseCase;
    private final AddTransactionUseCase addTransactionUseCase;
    private final GetCardsUseCase getCardsUseCase;
    private final GetCardUseCase getCardUseCase;
    private final List<CardNumberGenerator> cardNumberGenerators;
    private final Map<String, CardNumberGenerator> cardNumberGeneratorMap;

    public Application(AddCardUseCase addCardUseCase, AddTransactionUseCase addTransactionUseCase, GetCardsUseCase getCardsUseCase,
                       GetCardUseCase getCardUseCase, List<CardNumberGenerator> cardNumberGenerators,
                       Map<String, CardNumberGenerator> cardNumberGeneratorMap, DateTimeProvider dt1, DateTimeProvider dt2) {

        System.out.println(dt1);
        this.addCardUseCase = addCardUseCase;
        this.addTransactionUseCase = addTransactionUseCase;
        this.getCardsUseCase = getCardsUseCase;
        this.getCardUseCase = getCardUseCase;
        this.cardNumberGenerators = cardNumberGenerators;
        this.cardNumberGeneratorMap = cardNumberGeneratorMap;
    }

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var cardNumber = addCardUseCase.handle(CURRENCY).getNumber();

        addTransactionUseCase.handle(cardNumber, new Money(200.0, CURRENCY), INFLOW);
        addTransactionUseCase.handle(cardNumber, new Money(100.0, CURRENCY), PAYMENT);

        getCardsUseCase.handle(new PageSpec(0, 10))
                .content()
                .forEach(card -> LOGGER.info(card.toString()));

        getCardUseCase.handle(cardNumber)
                .getTransactions()
                .forEach(t -> LOGGER.info(t.toString()));
    }

}
