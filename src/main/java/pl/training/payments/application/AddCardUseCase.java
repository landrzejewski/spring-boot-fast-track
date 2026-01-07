package pl.training.payments.application;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.training.common.component.Generator;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardId;

import java.time.LocalDate;
import java.util.Currency;

@Service
public class AddCardUseCase {

    private static final int EXPIRATION_TIME_IN_YEARS = 1;

    private final CardNumberGenerator cardNumberGenerator;
    private final DateTimeProvider dateTimeProvider;
    private CardRepository cardRepository;

    public AddCardUseCase(
            // @Qualifier("random") CardNumberGenerator cardNumberGenerator,
            // @Generator("random") CardNumberGenerator cardNumberGenerator,
            // CardNumberGenerator random,
            final CardNumberGenerator cardNumberGenerator,
            final DateTimeProvider dateTimeProvider) {
        this.cardNumberGenerator = cardNumberGenerator;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Autowired
    public void setCardRepository(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @PostConstruct
    public void init() {
        System.out.println("Initializing AddCardUseCase");
    }

    @PreDestroy
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
