package pl.training.payments.adapters;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import pl.training.payments.application.CardNotFoundException;

import java.util.logging.Logger;

@Aspect
@Component
public final class AddTransactionLoggingAspect {

    private static final Logger LOGGER = Logger.getLogger(AddTransactionLoggingAspect.class.getName());

    @Before("bean(addTransactionUseCase)")
    public void beforeAddTransaction() {
        LOGGER.info("----------------------------- Transaction start -----------------------------");
    }

    @AfterReturning("bean(addTransactionUseCase)")
    public void onAddTransactionSuccess() {
        LOGGER.info("Transaction on card %s successfully completed".formatted(""));
    }
    @AfterThrowing(value = "bean(addTransactionUseCase)", throwing = "cardNotFoundException")
    public void onAddTransactionFailure(CardNotFoundException cardNotFoundException) {
        LOGGER.info("Transaction failed: %s".formatted(cardNotFoundException.getClass().getSimpleName()));
    }

    @After("bean(addTransactionUseCase)")
    public void afterAddTransaction() {
        LOGGER.info("------------------------------ Transaction end ------------------------------\n");
    }

}
