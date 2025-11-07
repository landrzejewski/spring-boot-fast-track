package pl.training.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static pl.training.common.aop.AopHelpers.findAnnotation;

/**
 * Aspekt Spring AOP implementujący programowe zarządzanie transakcjami.
 * Alternatywa dla deklaratywnego @Transactional Spring.
 * 
 * @Aspect - oznacza klasę jako aspekt AOP (Aspect-Oriented Programming).
 * Spring AOP używa proxy do przechwytywania wywołań metod i aplikowania logiki aspektów.
 * 
 * @Component - rejestruje aspekt jako bean Spring, dzięki czemu jest automatycznie
 * wykrywany i zarządzany przez kontener IoC.
 * 
 * Aspekty w Spring działają poprzez:
 * 1. Tworzenie proxy wokół docelowych beanów
 * 2. Przechwytywanie wywołań metod pasujących do punktów przecięcia (pointcuts)
 * 3. Wykonywanie dodatkowej logiki przed/po/wokół oryginalnej metody
 */
@Aspect
@Component
public final class AtomicAspect {

    /**
     * PlatformTransactionManager - główna abstrakcja Spring do zarządzania transakcjami.
     * Może obsługiwać różne implementacje: JPA, JDBC, MongoDB, itp.
     * Spring automatycznie wstrzykuje odpowiednią implementację na podstawie konfiguracji.
     */
    private final PlatformTransactionManager platformTransactionManager;

    public AtomicAspect(final PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    /**
     * @Around - typ porady (advice) wykonującej się wokół metody docelowej.
     * Daje pełną kontrolę nad wykonaniem metody - może ją wywołać, pominąć lub zmodyfikować wynik.
     * 
     * Wyrażenie pointcut składa się z dwóch części połączonych operatorem OR (||):
     * 
     * 1. "@annotation(pl.training.common.aop.Atomic)" - dopasowuje metody oznaczone @Atomic
     * 2. "within(@pl.training.common.aop.Atomic *)" - dopasowuje wszystkie metody w klasach oznaczonych @Atomic
     * 
     * ProceedingJoinPoint - reprezentuje punkt złączenia (join point) w kodzie,
     * gdzie aspekt może być zastosowany. Umożliwia:
     * - Wywołanie oryginalnej metody przez proceed()
     * - Dostęp do argumentów metody
     * - Modyfikację zwracanej wartości
     * - Obsługę wyjątków
     */
    @Around("@annotation(pl.training.common.aop.Atomic) || within(@pl.training.common.aop.Atomic *)")
    public Object runWithTransaction(final ProceedingJoinPoint joinPoint) throws Throwable {
        // Pobierz adnotację @Atomic z metody lub klasy
        var annotation = findAnnotation(joinPoint, Atomic.class);
        
        // Skonfiguruj parametry transakcji na podstawie adnotacji
        var transactionDefinition = transactionDefinition(annotation);
        
        // Rozpocznij nową transakcję lub dołącz do istniejącej
        var transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        
        try {
            // Wykonaj oryginalną metodę w kontekście transakcji
            var result = joinPoint.proceed();
            
            // Jeśli wszystko poszło dobrze, zatwierdź transakcję
            platformTransactionManager.commit(transactionStatus);
            
            return result;
        } catch (Throwable throwable) {
            // W przypadku wyjątku, oznacz transakcję do wycofania
            transactionStatus.setRollbackOnly();
            // Propaguj wyjątek dalej
            throw throwable;
        }
    }

    /**
     * Tworzy definicję transakcji na podstawie parametrów z adnotacji @Atomic.
     * 
     * DefaultTransactionDefinition pozwala konfigurować:
     * - Poziom izolacji (ISOLATION_DEFAULT, ISOLATION_READ_COMMITTED, itp.)
     * - Sposób propagacji (PROPAGATION_REQUIRED, PROPAGATION_REQUIRES_NEW, itp.)
     * - Timeout transakcji
     * - Czy transakcja jest tylko do odczytu
     */
    private TransactionDefinition transactionDefinition(final Atomic atomic) {
        var transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setTimeout(atomic.timeoutInMilliseconds());
        return transactionDefinition;
    }

}
