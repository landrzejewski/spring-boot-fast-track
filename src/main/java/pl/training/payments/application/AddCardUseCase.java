package pl.training.payments.application;

import org.springframework.stereotype.Component;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardId;

import java.time.LocalDate;
import java.util.Currency;

@Component
public class AddCardUseCase {

    private static final int EXPIRATION_TIME_IN_YEARS = 1;

    private final CardNumberGenerator cardNumberGenerator;
    private final DateTimeProvider dateTimeProvider;
    private final CardRepository cardRepository;

    public AddCardUseCase(final CardNumberGenerator cardNumberGenerator,
                          final DateTimeProvider dateTimeProvider,
                          final CardRepository cardRepository) {
        this.cardNumberGenerator = cardNumberGenerator;
        this.dateTimeProvider = dateTimeProvider;
        this.cardRepository = cardRepository;
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
