package pl.training.payments.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.training.common.Generator;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardId;

import java.time.LocalDate;
import java.util.Currency;

@Component
public class AddCardUseCase {

    private static final int EXPIRATION_TIME_IN_YEARS = 1;

    private final CardNumberGenerator cardNumberGenerator;
    private final CardRepository cardRepository;
    // @Autowired  // zła praktyka
    private DateTimeProvider dateTimeProvider;

    // @Autowired oznacza konieczność wstrzyknięcia zależności, jeżeli istnieje tylko jeden konstruktor z parametrami,
    // to Spring uznaje go jako domyślny i z jego użyciem tworzy instancję
    // (w tym przypadku @Autowired nie jest wymagane, ale jest wskazane - jeżli nie użyjemy
    // @Autowire to Spring wybierze konstruktor bezargumentowy)
   @Autowired
   public AddCardUseCase(// @Qualifier("sequentialCardNumberGenerator") final CardNumberGenerator cardNumberGenerator,  // kwalifikacja z użyciem nazwy i adnotacji @Qualifier
                         // @Generator("seq") final CardNumberGenerator cardNumberGenerator,                            // kwalifikacja z użyciem nazwy i adnotacji niestandardowej
                         // final CardNumberGenerator sequentialCardNumberGenerator,                                    // kwalifikacja przez nazwę argumentu/beana
                         final CardNumberGenerator cardNumberGenerator,                                                 // kwalifikacja przez wybór implementacji adnotacją @Primary
                         final CardRepository cardRepository) {
        this.cardNumberGenerator = cardNumberGenerator;
        this.cardRepository = cardRepository;
    }

    @Autowired
    public void configureDateTimeProvider(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    public Card handle(final Currency currency) {
        var card = createCard(currency);
        return cardRepository.save(card);
    }

    private Card createCard(final Currency currency) {
        return new Card(new CardId(), cardNumberGenerator.getNext(), calculateExpirationDate(), currency);
    }

    private LocalDate calculateExpirationDate() {
        return dateTimeProvider.getZonedDateTime()
                .plusYears(EXPIRATION_TIME_IN_YEARS)
                .toLocalDate();
    }

}
