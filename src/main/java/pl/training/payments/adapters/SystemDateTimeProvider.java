package pl.training.payments.adapters;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.training.payments.application.DateTimeProvider;

import java.time.ZonedDateTime;

// Adnotacje poniżej to aliasy dla adnotacji @Component, pełnią rolę stereotypu (dodatkowa informacja dla innych programistów)
// @Controller
// @Service
// @Repository

// Scope wpływa na sposób zarządzania komponentem - decyduje o tym, kiedy jest on tworzony i niszczony przez kontener
// SINGLETON - instancja tworzona raz (przy starcie aplikacji) i reużywana (default)
// PROTOTYPE - instancja tworzona na życzenie (za każdym razem)

// @Scope("prototype")
@Service("timeProvider") // nadanie nazwy beana (domyślnie jest ona tworzona na poostawie nazwy kalsy)
public final class SystemDateTimeProvider implements DateTimeProvider {

    @Override
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now();
    }

    // Wymagania dla metod związanych z cyklem życia
    // - brak argumentów
    // - brak resultatu
    // - brak wyjątków typu Exception

    // Metoda do inicjalizaji - wołana po wstrzyknięciu wszystkich zależności

    @PostConstruct
    public void init() {
        System.out.println("Initializing timeProvider");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Destroying timeProvider");
    }

}
