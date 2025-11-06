package pl.training.payments.application;

import pl.training.common.aop.Atomic;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

//@Transactional
@Atomic
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
