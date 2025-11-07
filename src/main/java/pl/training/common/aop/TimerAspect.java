package pl.training.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import pl.training.common.aop.Timer.UnitType;

import java.util.logging.Logger;

/**
 * Aspekt Spring AOP mierzący czas wykonania metod oznaczonych @Timer.
 * Przydatny do monitorowania wydajności i optymalizacji kodu.
 * 
 * @Profile("dev") - warunkowa aktywacja aspektu tylko dla profilu "dev".
 * Spring Profile umożliwia różne konfiguracje dla różnych środowisk:
 * - dev: środowisko deweloperskie z dodatkowymi logami i monitorowaniem
 * - prod: środowisko produkcyjne zoptymalizowane pod kątem wydajności
 * - test: środowisko testowe
 * 
 * Profile aktywuje się przez:
 * - application.yml: spring.profiles.active=dev
 * - parametr JVM: -Dspring.profiles.active=dev
 * - zmienna środowiskowa: SPRING_PROFILES_ACTIVE=dev
 * 
 * @Aspect - deklaruje klasę jako aspekt
 * @Component - rejestruje w kontenerze Spring
 * 
 * Implements Ordered - interfejs Spring do kontroli kolejności wykonywania aspektów.
 * Alternatywą jest adnotacja @Order(2) pokazana w komentarzu.
 * Niższy numer = wyższy priorytet (wykonuje się wcześniej).
 */
// @Order(2)
@Profile("dev")
@Aspect
@Component
public class TimerAspect implements Ordered {

    private static final Logger LOGGER = Logger.getLogger(TimerAspect.class.getName());

    /**
     * @Around - porada mierząca czas wykonania metody
     * 
     * Pointcut "@annotation(timer)":
     * - Dopasowuje metody z adnotacją @Timer
     * - Wiąże adnotację z parametrem 'timer' dając dostęp do jej atrybutów
     * 
     * Przepływ wykonania:
     * 1. Zapisuje czas startu
     * 2. Wykonuje oryginalną metodę
     * 3. Zapisuje czas końca
     * 4. Oblicza i loguje czas wykonania
     * 5. Zwraca wynik oryginalnej metody
     * 
     * @param joinPoint punkt złączenia z informacjami o metodzie
     * @param timer adnotacja z konfiguracją (jednostka czasu)
     */
    @Around("@annotation(timer)")
    public Object measure(ProceedingJoinPoint joinPoint, Timer timer) throws Throwable {
        // Pobierz jednostkę czasu z adnotacji (MS lub NANOS)
        var units = timer.unitType();
        
        // Zapisz czas rozpoczęcia
        var startTime = getTime(units);
        
        // Wykonaj oryginalną metodę
        var result = joinPoint.proceed();
        
        // Zapisz czas zakończenia
        var endTime = getTime(units);
        
        // Oblicz całkowity czas wykonania
        var totalTime = endTime - startTime;
        
        // Zaloguj wynik z pełną sygnaturą metody (klasa, metoda, parametry)
        LOGGER.info("Method %s executes in %d %s".formatted(joinPoint.getSignature(), totalTime, units.name().toLowerCase()));
        
        return result;
    }

    /**
     * Pomocnicza metoda pobierająca aktualny czas w odpowiedniej jednostce.
     * 
     * System.currentTimeMillis() - czas w milisekundach od 1970-01-01 (epoch time)
     * System.nanoTime() - bardziej precyzyjny pomiar czasu, idealny do mierzenia różnic
     * 
     * UWAGA: nanoTime() nie reprezentuje rzeczywistego czasu, tylko względny punkt odniesienia
     */
    private long getTime(UnitType unitType) {
        return unitType == UnitType.MS ? System.currentTimeMillis() : System.nanoTime();
    }

    /**
     * Implementacja interfejsu Ordered.
     * Określa kolejność wykonywania tego aspektu względem innych.
     * 
     * Kolejność jest ważna gdy wiele aspektów dotyczy tej samej metody:
     * - Aspekty z niższym order wykonują się pierwsze przy "wejściu"
     * - Aspekty z wyższym order wykonują się pierwsze przy "wyjściu"
     * 
     * Przykład dla order 1, 2, 3:
     * Wejście: 1 -> 2 -> 3 -> [metoda] -> 3 -> 2 -> 1 :Wyjście
     */
    @Override
    public int getOrder() {
        return 2;
    }

}
