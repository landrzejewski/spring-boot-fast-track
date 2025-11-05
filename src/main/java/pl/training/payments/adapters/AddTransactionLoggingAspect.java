package pl.training.payments.adapters;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pl.training.payments.application.CardNotFoundException;
import pl.training.payments.domain.CardNumber;
import pl.training.payments.domain.Money;
import pl.training.payments.domain.TransactionId;
import pl.training.payments.domain.TransactionType;

import java.util.logging.Logger;

@Profile("dev")
@Order(1)
@Aspect
@Component
public final class AddTransactionLoggingAspect {

    private static final Logger LOGGER = Logger.getLogger(AddTransactionLoggingAspect.class.getName());

    // @Pointcut("execution(* pl.training.payments.app*.*TransactionUseCase.han*(..))")
    // @Pointcut("execution(pl.training.payments.domain.TransactionId pl.training.payments.application.AddTransactionUseCase.handle(pl.training.payments.domain.CardNumber, pl.training.payments.domain.Money, pl.training.payments.domain.TransactionType))")
    @Pointcut("@annotation(pl.training.common.aop.Loggable)")
    // @Pointcut("bean(addTransactionUseCase)")
    void process() {
    }

    @Before(value = "process() && args(cardNumber, amount ,type)"/*, argNames = "joinPoint,amount,cardNumber,type"*/)
    public void beforeAddTransaction(JoinPoint joinPoint, Money amount, CardNumber cardNumber, TransactionType type) {
        // var cardNumber = (CardNumber) joinPoint.getArgs()[0];
        LOGGER.info("----------------------------- Transaction start -----------------------------");
        LOGGER.info("cardNumber: " + cardNumber);
        LOGGER.info("amount: " + amount);
    }

    @AfterReturning(value = "process()", returning = "transactionId")
    public void onAddTransactionSuccess(TransactionId transactionId) {
        LOGGER.info("Transaction with id: %s successfully".formatted(transactionId.value()));
    }

    @AfterThrowing(value = "process()", throwing = "cardNotFoundException")
    public void onAddTransactionFailure(CardNotFoundException  cardNotFoundException) {
        LOGGER.info("Transaction failed: %s".formatted(cardNotFoundException.getClass().getSimpleName()));
    }

    @After("process()")
    public void afterAddTransaction() {
        LOGGER.info("------------------------------ Transaction end ------------------------------\n");
    }

}
