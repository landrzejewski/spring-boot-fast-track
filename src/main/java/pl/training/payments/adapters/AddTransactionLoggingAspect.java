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

/**
 * Aspekt Spring AOP do kompleksowego logowania operacji transakcyjnych.
 * Demonstruje wszystkie typy porad AOP: @Before, @After, @AfterReturning, @AfterThrowing.
 * 
 * @Profile("dev") - aspekt aktywny tylko w profilu deweloperskim
 * @Order(1) - wykonuje się jako pierwszy (przed TimerAspect z order=2)
 * @Aspect - deklaracja aspektu
 * @Component - rejestracja w kontenerze Spring
 */
@Profile("dev")
@Order(1)
@Aspect
@Component
public final class AddTransactionLoggingAspect {

    private static final Logger LOGGER = Logger.getLogger(AddTransactionLoggingAspect.class.getName());

    /**
     * @Pointcut - definiuje nazwany, reużywalny punkt przecięcia.
     * Zamiast powtarzać wyrażenie w każdej poradzie, definiujemy je raz.
     * 
     * Przykłady różnych typów pointcut (pokazane w komentarzach):
     * 
     * 1. Wzorzec execution z wildcards:
     *    "execution(* pl.training.payments.app*.*TransactionUseCase.han*(..))"
     *    - * = dowolny typ zwracany
     *    - app* = pakiety zaczynające się od "app"
     *    - *TransactionUseCase = klasy kończące się na "TransactionUseCase"
     *    - han* = metody zaczynające się od "han"
     *    - (..) = dowolna liczba parametrów
     * 
     * 2. Dokładna sygnatura metody:
     *    "execution(TransactionId AddTransactionUseCase.handle(CardNumber, Money, TransactionType))"
     * 
     * 3. Aktualnie używany - dopasowanie po adnotacji:
     *    "@annotation(pl.training.common.aop.Loggable)"
     *    - Dopasowuje wszystkie metody oznaczone @Loggable
     * 
     * 4. Dopasowanie po nazwie beana:
     *    "bean(addTransactionUseCase)"
     *    - Dopasowuje wszystkie metody w beanie o danej nazwie
     */
    // @Pointcut("execution(* pl.training.payments.app*.*TransactionUseCase.han*(..))")
    // @Pointcut("execution(pl.training.payments.domain.TransactionId pl.training.payments.application.AddTransactionUseCase.handle(pl.training.payments.domain.CardNumber, pl.training.payments.domain.Money, pl.training.payments.domain.TransactionType))")
    @Pointcut("@annotation(pl.training.common.aop.Loggable)")
    // @Pointcut("bean(addTransactionUseCase)")
    void process() {
    }

    /**
     * @Before - wykonuje się PRZED metodą docelową
     * 
     * Wyrażenie "process() && args(cardNumber, amount, type)":
     * - process() - używa zdefiniowanego wyżej pointcut
     * - && - operator AND łączący warunki
     * - args(...) - przechwytuje argumenty metody i wiąże je z parametrami porady
     * 
     * Kolejność parametrów w args() musi odpowiadać kolejności w metodzie docelowej!
     * Nazwy mogą być inne, ale typy muszą się zgadzać.
     * 
     * argNames (zakomentowany) - opcjonalny atrybut do jawnego mapowania nazw,
     * przydatny gdy kompilacja usuwa nazwy parametrów.
     */
    @Before(value = "process() && args(cardNumber, amount ,type)"/*, argNames = "joinPoint,amount,cardNumber,type"*/)
    public void beforeAddTransaction(JoinPoint joinPoint, Money amount, CardNumber cardNumber, TransactionType type) {
        // Alternatywny sposób dostępu do argumentów przez JoinPoint:
        // var cardNumber = (CardNumber) joinPoint.getArgs()[0];
        LOGGER.info("----------------------------- Transaction start -----------------------------");
        LOGGER.info("cardNumber: " + cardNumber);
        LOGGER.info("amount: " + amount);
    }

    /**
     * @AfterReturning - wykonuje się PO POMYŚLNYM zakończeniu metody
     * 
     * Parametr "returning" wiąże wartość zwróconą z parametrem metody.
     * Wykonuje się tylko gdy metoda zakończyła się bez wyjątku.
     * 
     * Użyteczne do:
     * - Logowania sukcesu
     * - Audytu
     * - Cachowania wyniku
     * - Wysyłania notyfikacji o sukcesie
     */
    @AfterReturning(value = "process()", returning = "transactionId")
    public void onAddTransactionSuccess(TransactionId transactionId) {
        LOGGER.info("Transaction with id: %s successfully".formatted(transactionId.value()));
    }

    /**
     * @AfterThrowing - wykonuje się gdy metoda rzuci wyjątek
     * 
     * Parametr "throwing" wiąże rzucony wyjątek z parametrem metody.
     * Typ parametru określa, które wyjątki są przechwytywane.
     * 
     * NIE łapie wyjątku - tylko go obserwuje!
     * Wyjątek jest nadal propagowany.
     * 
     * Użyteczne do:
     * - Logowania błędów
     * - Alertów o błędach
     * - Czyszczenia zasobów
     * - Statystyk błędów
     */
    @AfterThrowing(value = "process()", throwing = "cardNotFoundException")
    public void onAddTransactionFailure(CardNotFoundException  cardNotFoundException) {
        LOGGER.info("Transaction failed: %s".formatted(cardNotFoundException.getClass().getSimpleName()));
    }

    /**
     * @After - wykonuje się ZAWSZE po metodzie (jak finally w try-catch)
     * 
     * Wykonuje się niezależnie od sukcesu czy wyjątku.
     * Idealne do czyszczenia zasobów.
     * 
     * Kolejność wykonywania porad:
     * 1. @Before
     * 2. [metoda docelowa]
     * 3. @AfterReturning LUB @AfterThrowing
     * 4. @After (zawsze)
     */
    @After("process()")
    public void afterAddTransaction() {
        LOGGER.info("------------------------------ Transaction end ------------------------------\n");
    }

}
