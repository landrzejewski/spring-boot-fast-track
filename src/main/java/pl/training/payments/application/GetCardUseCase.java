package pl.training.payments.application;

import org.springframework.transaction.annotation.Transactional;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

@Transactional
public class GetCardUseCase {

    private final CardRepository cardRepository;

    public GetCardUseCase(final CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card handle(final CardNumber cardNumber) {
        return cardRepository.findByNumber(cardNumber)
                .orElseThrow(CardNotFoundException::new);
    }

}
