package pl.training.payments.application;

import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardId;

import java.time.LocalDate;
import java.util.Currency;

public class AddCardUseCase {

    private static final int EXPIRATION_TIME_IN_YEARS = 1;

    private final CardNumberGenerator cardNumberGenerator;
    private final DateTimeProvider dateTimeProvider;
    private CardRepository cardRepository;

    public AddCardUseCase(final CardNumberGenerator cardNumberGenerator, final DateTimeProvider dateTimeProvider) {
        this.cardNumberGenerator = cardNumberGenerator;
        this.dateTimeProvider = dateTimeProvider;
    }

    public void setCardRepository(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void init() {
        System.out.println("Initializing AddCardUseCase");
    }

    public void destroy() {
        System.out.println("Destroying AddCardUseCase");
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
