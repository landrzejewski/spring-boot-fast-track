package pl.training.payments.application;

import pl.training.common.aop.Atomic;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardId;

import java.time.LocalDate;
import java.util.Currency;

//@Transactional
@Atomic
public class AddCardUseCase {

    private static final int EXPIRATION_TIME_IN_YEARS = 1;

    private final CardNumberGenerator cardNumberGenerator;
    private final CardRepository cardRepository;
    private final DateTimeProvider dateTimeProvider;

    public AddCardUseCase(final CardNumberGenerator cardNumberGenerator,
                          final CardRepository cardRepository,
                          final DateTimeProvider dateTimeProvider) {
        this.cardNumberGenerator = cardNumberGenerator;
        this.cardRepository = cardRepository;
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
