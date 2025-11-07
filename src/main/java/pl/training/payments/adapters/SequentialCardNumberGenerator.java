package pl.training.payments.adapters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import pl.training.common.component.Generator;
import pl.training.payments.application.CardNumberGenerator;
import pl.training.payments.domain.CardNumber;

/**
 * Generator sekwencyjnych numerów kart płatniczych.
 * Generuje kolejne numery w formacie 0000...0001, 0000...0002, itd.
 * 
 * @Primary - oznacza ten bean jako domyślny gdy istnieje wiele implementacji:
 * - Spring wybierze ten bean gdy autowiring po typie CardNumberGenerator
 * - Nadpisuje wybór gdy nie użyto @Qualifier
 * - Przydatne gdy mamy RandomCardNumberGenerator i SequentialCardNumberGenerator
 * 
 * @Generator("seq") - niestandardowy stereotyp (rozszerza @Component):
 * - Nadaje beanowi nazwę "seq" dla @Qualifier("seq")
 * - Semantycznie oznacza generator
 * - Zakomentowany @Component pokazuje alternatywę
 * 
 * Wybór implementacji w Spring:
 * 1. @Primary - domyślna implementacja
 * 2. @Qualifier("nazwa") - wybór po nazwie
 * 3. @ConditionalOnProperty - wybór przez konfigurację
 * 4. @Profile - wybór przez profil środowiska
 */
@Primary
//@Component
@Generator("seq")
public class SequentialCardNumberGenerator implements CardNumberGenerator {

    private final int length;

    /**
     * Licznik sekwencyjny - stan współdzielony między wywołaniami.
     * Nie jest final, bo musi być modyfikowany.
     * Dostęp synchronizowany w metodzie getNext().
     */
    private long counter;

    /**
     * Konstruktor z dependency injection wartości konfiguracyjnej.
     * 
     * @Value("${card-number-length}") - wstrzykuje wartość z application.yml
     * 
     * Walidacja w konstruktorze:
     * - Fail-fast - błąd przy tworzeniu beana, nie przy użyciu
     * - IllegalArgumentException zatrzyma start aplikacji
     * - Alternatywa: @Min(1) z Bean Validation na parametrze
     */
    public SequentialCardNumberGenerator(@Value("${card-number-length}") final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be greater than or equal to zero");
        }
        this.length = length;
    }

    /**
     * Generuje kolejny numer karty.
     * 
     * synchronized - bezpieczeństwo wątkowe:
     * - Bean jest singletonem współdzielonym między wątkami
     * - counter++ nie jest operacją atomową
     * - synchronized zapewnia exclusive access
     * 
     * Alternatywy dla lepszej wydajności:
     * - AtomicLong zamiast long + synchronized
     * - @Scope("prototype") - osobna instancja per wstrzyknięcie
     * - ThreadLocal<Long> dla licznika per wątek
     * 
     * String.format() z wypełnieniem zerami:
     * - %0Xd - liczba dziesiętna wypełniona zerami do X cyfr
     * - Przykład dla length=16 i counter=123: "0000000000000123"
     * 
     * Uwaga: W produkcji numery kart muszą spełniać algorytm Luhna!
     */
    @Override
    public synchronized CardNumber getNext() {
        var number = String.format("%0" + length + "d", ++counter);
        return new CardNumber(number);
    }

}
