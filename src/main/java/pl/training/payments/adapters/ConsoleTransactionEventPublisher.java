package pl.training.payments.adapters;

import org.springframework.stereotype.Component;
import pl.training.payments.application.TransactionAdded;
import pl.training.payments.application.TransactionEventPublisher;

import java.util.logging.Logger;

/**
 * Prosty publisher zdarzeń transakcyjnych wypisujący na konsolę.
 * Implementacja adaptera dla portu TransactionEventPublisher.
 * 
 * @Component - podstawowy stereotyp Spring dla komponentów:
 * - Automatyczne wykrywanie przez component scanning
 * - Rejestracja jako singleton bean w kontenerze IoC
 * - Wstrzykiwanie przez interfejs TransactionEventPublisher
 * 
 * W prawdziwej aplikacji można by użyć:
 * - @Service - semantycznie lepsza dla logiki biznesowej
 * - @EventListener/@TransactionalEventListener - dla Spring Events
 * - Spring Integration lub Apache Kafka dla zdarzeń asynchronicznych
 * 
 * Pattern: Hexagonal Architecture (Port & Adapter)
 * - Port: TransactionEventPublisher (interfejs w warstwie aplikacji)
 * - Adapter: ConsoleTransactionEventPublisher (implementacja techniczna)
 */
@Component
public final class ConsoleTransactionEventPublisher implements TransactionEventPublisher {

    private static final Logger LOGGER = Logger.getLogger(ConsoleTransactionEventPublisher.class.getName());

    /**
     * Publikuje zdarzenie o dodaniu transakcji.
     * 
     * W tej prostej implementacji tylko loguje do konsoli.
     * W produkcyjnych rozwiązaniach mogłoby:
     * - Wysłać zdarzenie do kolejki (RabbitMQ, Kafka)
     * - Wywołać webhooki
     * - Zapisać do event store
     * - Wysłać notyfikacje (email, SMS, push)
     * 
     * Spring oferuje własny mechanizm zdarzeń:
     * - ApplicationEventPublisher do publikowania
     * - @EventListener do nasłuchiwania
     * - @Async dla asynchronicznego przetwarzania
     * - @TransactionalEventListener dla zdarzeń transakcyjnych
     * 
     * formatted() - Java 15+ string formatting
     * Alternatywa: String.format() lub konkatenacja
     */
    @Override
    public void publish(final TransactionAdded transactionAdded) {
        LOGGER.info("Event: %s transaction added".formatted(transactionAdded.transactionType()));
    }

}
