package pl.training.payments.application;

import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.common.aop.Atomic;
import pl.training.payments.domain.Card;

//@Transactional
@Atomic
public class GetCardsUseCase {

    private final CardRepository cardRepository;

    public GetCardsUseCase(final CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public ResultPage<Card> handle(final PageSpec pageSpec) {
        return cardRepository.findAll(pageSpec);
    }

}
