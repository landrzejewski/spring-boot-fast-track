package pl.training.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Aspekt Spring AOP implementujący mechanizm automatycznego ponawiania wywołań.
 * Przydatny dla operacji, które mogą tymczasowo zawieść (np. połączenia sieciowe).
 * 
 * @Aspect - deklaruje klasę jako aspekt AOP
 * @Component - rejestruje aspekt w kontenerze Spring
 * 
 * Wzorzec Retry jest szczególnie użyteczny dla:
 * - Wywołań REST API (timeout, chwilowa niedostępność)
 * - Operacji bazodanowych (deadlock, connection timeout)
 * - Operacji I/O (dostęp do plików, zasobów sieciowych)
 * 
 * W produkcji rozważ użycie Spring Retry lub Resilience4j
 * dla bardziej zaawansowanych funkcji (exponential backoff, circuit breaker).
 */
@Aspect
@Component
public class RetryAspect {

    private static final Logger LOGGER = Logger.getLogger(RetryAspect.class.getName());

    /**
     * @Around - porada wykonywana wokół metody docelowej
     * 
     * Pointcut "@annotation(retry)":
     * - Dopasowuje metody oznaczone @Retry
     * - Parametr 'retry' automatycznie wiąże się z adnotacją
     * - Daje dostęp do konfiguracji (liczba prób)
     * 
     * Logika retry:
     * 1. Próbuje wykonać metodę
     * 2. Jeśli wystąpi wyjątek, loguje i próbuje ponownie
     * 3. Powtarza do wyczerpania liczby prób
     * 4. Rzuca ostatni wyjątek jeśli wszystkie próby zawiodły
     * 
     * UWAGA: Obecna implementacja nie ma opóźnienia między próbami.
     * W produkcji warto dodać:
     * - Thread.sleep() lub scheduled delay
     * - Exponential backoff (rosnące opóźnienia)
     * - Jitter (losowe opóźnienie dla uniknięcia thundering herd)
     * - Selektywne retry tylko dla określonych wyjątków
     */
    @Around("@annotation(retry)")
    public Object tryExecute(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        var attempt = 0;
        Throwable throwable;
        
        // Pętla retry z do-while gwarantuje przynajmniej jedną próbę
        do {
            attempt++;
            try {
                // Próba wykonania oryginalnej metody
                return joinPoint.proceed();
            } catch (Throwable currentThrowable) {
                // Zapisz wyjątek i zaloguj niepowodzenie
                throwable = currentThrowable;
                LOGGER.info("Execution of method \"%s\" failed with exception: %s (attempt: %d)"
                        .formatted(joinPoint.getSignature(), throwable.getClass().getSimpleName(), attempt));
                
                // Tu można dodać:
                // - if (attempt < retry.attempts()) Thread.sleep(retry.delay());
                // - Sprawdzenie czy wyjątek kwalifikuje się do retry
                // - Circuit breaker logic
            }
        } while (attempt < retry.attempts());
        
        // Rzuć ostatni wyjątek po wyczerpaniu prób
        throw throwable;
    }

}
