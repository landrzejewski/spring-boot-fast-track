package pl.training;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.training.common.PageSpec;
import pl.training.payments.application.AddCardUseCase;
import pl.training.payments.application.AddTransactionUseCase;
import pl.training.payments.application.GetCardUseCase;
import pl.training.payments.application.GetCardsUseCase;
import pl.training.payments.domain.Money;

import java.util.Currency;
import java.util.logging.Logger;

import static pl.training.payments.domain.TransactionType.INFLOW;
import static pl.training.payments.domain.TransactionType.PAYMENT;

@SpringBootApplication
public class Application implements ApplicationRunner {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
    private static final Currency CURRENCY = Currency.getInstance("PLN");

    private final AddCardUseCase addCardUseCase;
    private final AddTransactionUseCase  addTransactionUseCase;
    private final GetCardsUseCase getCardsUseCase;
    private final GetCardUseCase  getCardUseCase;

    public Application(AddCardUseCase addCardUseCase, AddTransactionUseCase addTransactionUseCase, GetCardsUseCase getCardsUseCase, GetCardUseCase getCardUseCase) {
        this.addCardUseCase = addCardUseCase;
        this.addTransactionUseCase = addTransactionUseCase;
        this.getCardsUseCase = getCardsUseCase;
        this.getCardUseCase = getCardUseCase;
    }


    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
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
