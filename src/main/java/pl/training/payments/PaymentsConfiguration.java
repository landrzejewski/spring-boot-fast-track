package pl.training.payments;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.training.payments.adapters.persistence.HashMapCardRepository;
import pl.training.payments.application.*;

/**
 * Klasa konfiguracyjna modułu płatności.
 * Definiuje beany dla przypadków użycia (use cases) w architekturze heksagonalnej.
 * 
 * @Configuration - oznacza klasę jako źródło definicji beanów Spring
 * 
 * Zakomentowane adnotacje pokazują dodatkowe możliwości konfiguracji:
 * - @Profile("default") - aktywowałaby tę konfigurację tylko dla profilu "default"
 * - @Scope("prototype") - każde żądanie beana zwracałoby nową instancję (domyślnie singleton)
 */
// @Profile("default")
@Configuration
public class PaymentsConfiguration {

    /**
     * Definiuje bean dla przypadku użycia dodawania karty.
     * 
     * @Bean - metoda produkuje bean zarządzany przez kontener Spring
     * 
     * Parametry metody są automatycznie wstrzykiwane przez Spring (dependency injection).
     * Spring szuka beanów pasujących do typu parametru w kontekście aplikacji.
     * 
     * Zakomentowane opcje @Bean:
     * - initMethod - metoda wywoływana po utworzeniu beana
     * - destroyMethod - metoda wywoływana przed zniszczeniem beana
     * - name - alternatywne nazwy beana (domyślnie nazwa metody)
     * 
     * @Qualifier("sequentialCardNumberGenerator") - wskazywałby konkretną implementację
     * gdy istnieje wiele beanów typu CardNumberGenerator
     */
    // @Profile("default")
    // @Scope("prototype")
    @Bean // (initMethod = "", destroyMethod = "", name = {"addCardUseCase", "addCard"})
    public AddCardUseCase addCardUseCase(/*@Qualifier("sequentialCardNumberGenerator")*/ CardNumberGenerator cardNumberGenerator,
                                         CardRepository cardRepository, DateTimeProvider dateTimeProvider) {
        return new AddCardUseCase(cardNumberGenerator, cardRepository, dateTimeProvider);
    }

    /**
     * Bean dla przypadku użycia dodawania transakcji.
     * 
     * Spring automatycznie rozwiązuje zależności:
     * 1. Znajduje beany implementujące DateTimeProvider, TransactionEventPublisher, CardRepository
     * 2. Wstrzykuje je jako parametry konstruktora
     * 3. Tworzy instancję AddTransactionUseCase
     * 4. Rejestruje ją w kontekście jako singleton (domyślny scope)
     */
    @Bean
    public AddTransactionUseCase  addTransactionUseCase(DateTimeProvider dateTimeProvider, TransactionEventPublisher transactionEventPublisher,
                                                        CardRepository cardRepository) {
        return new AddTransactionUseCase(dateTimeProvider, transactionEventPublisher, cardRepository);
    }

    /**
     * Bean dla przypadku użycia pobierania listy kart.
     * Przykład prostego beana z jedną zależnością.
     */
    @Bean
    public GetCardsUseCase getCardsUseCase(CardRepository cardRepository) {
        return new GetCardsUseCase(cardRepository);
    }

    /**
     * Bean dla przypadku użycia pobierania pojedynczej karty.
     * 
     * Wszystkie te beany są singletonami - Spring tworzy tylko jedną instancję
     * i używa jej dla wszystkich wstrzyknięć, co jest wydajne dla bezstanowych serwisów.
     */
    @Bean
    public GetCardUseCase getCardUseCase(CardRepository cardRepository) {
        return new GetCardUseCase(cardRepository);
    }

}
