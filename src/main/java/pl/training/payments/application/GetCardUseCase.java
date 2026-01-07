package pl.training.payments.application;

import org.springframework.stereotype.Service;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

@Service
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
