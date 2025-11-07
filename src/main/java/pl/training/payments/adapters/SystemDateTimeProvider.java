package pl.training.payments.adapters;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.training.payments.application.DateTimeProvider;

import java.time.ZonedDateTime;

/**
 * Adnotacje stereotypów Spring - wszystkie rozszerzają @Component:
 * 
 * @Controller - kontrolery MVC zwracające widoki (HTML)
 * @RestController - kontrolery REST zwracające dane (JSON/XML)
 * @Service - warstwa logiki biznesowej/serwisów
 * @Repository - warstwa dostępu do danych (DAO)
 * 
 * Różnice między stereotypami:
 * - Semantyczne - wyrażają intencję i rolę klasy
 * - @Repository dodatkowo tłumaczy wyjątki persystencji na DataAccessException
 * - Mogą być różnie traktowane przez AOP (np. @Transactional na @Service)
 * - Ułatwiają wyszukiwanie i organizację kodu
 */

/**
 * @Scope - określa strategię tworzenia instancji beana:
 * 
 * SINGLETON (domyślny):
 * - Jedna instancja na cały kontekst aplikacji
 * - Tworzona przy starcie aplikacji (eager) lub pierwszym użyciu (lazy)
 * - Thread-safe - współdzielona między wątkami
 * - Najwydajniejsza dla bezstanowych serwisów
 * 
 * PROTOTYPE:
 * - Nowa instancja przy każdym wstrzyknięciu lub getBean()
 * - Brak zarządzania cyklem życia przez Spring (tylko tworzenie)
 * - Użyteczne dla komponentów ze stanem
 * 
 * Inne scope (w aplikacjach webowych):
 * - REQUEST - instancja na żądanie HTTP
 * - SESSION - instancja na sesję użytkownika
 * - APPLICATION - instancja na ServletContext
 * - WEBSOCKET - instancja na sesję WebSocket
 */

/**
 * Provider systemowego czasu z obsługą cyklu życia beana.
 * 
 * @Service - stereotyp dla komponentów logiki biznesowej
 * Parametr "timeProvider" - jawna nazwa beana (zamiast domyślnej "systemDateTimeProvider")
 * Przydatne gdy:
 * - Mamy wiele implementacji tego samego interfejsu
 * - Chcemy używać @Qualifier("timeProvider")
 * - Integrujemy z zewnętrznym kodem oczekującym konkretnej nazwy
 */
// @Scope("prototype")
@Service("timeProvider")
public final class SystemDateTimeProvider implements DateTimeProvider {

    @Override
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now();
    }

    /**
     * Metody cyklu życia beana Spring - wymagania:
     * - Brak argumentów (void method())
     * - Typ zwracany void
     * - Nie mogą deklarować checked exceptions
     * - Mogą być private (Spring używa refleksji)
     */

    /**
     * @PostConstruct - metoda wywoływana po utworzeniu beana i wstrzyknięciu zależności.
     * 
     * Kolejność inicjalizacji beana:
     * 1. Konstruktor
     * 2. Wstrzykiwanie zależności (@Autowired, @Value)
     * 3. @PostConstruct
     * 4. Bean gotowy do użycia
     * 
     * Użycie:
     * - Inicjalizacja zasobów (połączenia, cache)
     * - Walidacja konfiguracji
     * - Rejestracja w zewnętrznych serwisach
     * - Logowanie startu komponentu
     * 
     * Alternatywy:
     * - InitializingBean.afterPropertiesSet()
     * - @Bean(initMethod = "init")
     * - ApplicationListener<ContextRefreshedEvent>
     */
    @PostConstruct
    public void init() {
        System.out.println("Initializing timeProvider");
    }

    /**
     * @PreDestroy - metoda wywoływana przed zniszczeniem beana.
     * 
     * Wywoływana gdy:
     * - Aplikacja się zamyka (graceful shutdown)
     * - Kontekst Spring jest zamykany
     * - Bean prototype jest jawnie niszczony
     * 
     * NIE jest wywoływana gdy:
     * - JVM nagle się wyłącza (kill -9)
     * - Bean ma scope prototype (Spring nie zarządza destrukcją)
     * 
     * Użycie:
     * - Zamykanie połączeń (DB, files, sockets)
     * - Zwalnianie zasobów
     * - Wyrejestrowanie z zewnętrznych serwisów
     * - Zapis stanu przed zamknięciem
     * 
     * Alternatywy:
     * - DisposableBean.destroy()
     * - @Bean(destroyMethod = "cleanup")
     * - Try-with-resources dla zasobów
     */
    @PreDestroy
    public void destroy() {
        System.out.println("Destroying timeProvider");
    }

}
