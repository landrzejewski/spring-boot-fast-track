package pl.training.payments.adapters;

import org.springframework.beans.factory.annotation.Value;
import pl.training.common.component.Generator;
import pl.training.payments.application.CardNumberGenerator;
import pl.training.payments.domain.CardNumber;

import java.util.Random;

/**
 * Generator losowych numerów kart płatniczych.
 * Przykład komponentu Spring z dependency injection przez konstruktor.
 * 
 * @Generator("rnd") - niestandardowy stereotyp (rozszerza @Component)
 * z nazwą beana "rnd". Umożliwia:
 * - Wstrzykiwanie przez @Qualifier("rnd")
 * - Semantyczne oznaczenie roli klasy
 * - Automatyczne wykrywanie przez component scan
 * 
 * Zakomentowany @Component pokazuje alternatywę - można użyć
 * standardowego stereotypu, ale @Generator lepiej wyraża intencję.
 */
//@Component
@Generator("rnd")
public final class RandomCardNumberGenerator implements CardNumberGenerator {

    private final Random random = new Random();
    private final int length;

    /**
     * Konstruktor z dependency injection.
     * 
     * @Value("${card-number-length}") - wstrzykiwanie wartości z konfiguracji:
     * - Spring szuka właściwości "card-number-length" w:
     *   1. application.yml/properties
     *   2. Zmiennych środowiskowych (CARD_NUMBER_LENGTH)
     *   3. Parametrach JVM (-Dcard-number-length=16)
     *   4. Spring Cloud Config Server
     * 
     * - ${...} to Spring Expression Language (SpEL)
     * - Można podać wartość domyślną: ${card-number-length:16}
     * - Można użyć wyrażeń: ${random.int(10,20)}
     * - Type conversion automatyczny (String -> int)
     * 
     * Konstruktorowe wstrzykiwanie (preferowane):
     * - Immutability (final fields)
     * - Fail-fast (błąd przy tworzeniu beana)
     * - Łatwiejsze testowanie (nie wymaga Spring w testach)
     * - Jawne zależności
     * 
     * Alternatywy:
     * - @Autowired na polu (nie zalecane)
     * - @Autowired na setterze (dla opcjonalnych zależności)
     */
    public RandomCardNumberGenerator(@Value("${card-number-length}") final int length) {
        this.length = length;
    }

    /**
     * synchronized - bezpieczeństwo wątkowe.
     * 
     * Spring beany są domyślnie singletonami współdzielonymi między wątkami.
     * Random nie jest thread-safe, więc synchronizujemy dostęp.
     * 
     * Alternatywy:
     * - ThreadLocalRandom (lepsze dla wysokiej współbieżności)
     * - SecureRandom (dla kryptograficznie bezpiecznych liczb)
     * - @Scope("prototype") - nowa instancja per injection
     */
    @Override
    public synchronized CardNumber getNext() {
        var number = new StringBuilder();
        for (int index = 0; index < length; index++) {
            int digit = random.nextInt(10);
            number.append(digit);
        }
        return new CardNumber(number.toString());
    }

}
