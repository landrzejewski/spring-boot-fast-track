package pl.training.payments.application;

import org.springframework.stereotype.Service;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.payments.domain.Card;

@Service
public class GetCardsUseCase {

    private final CardRepository cardRepository;

    public GetCardsUseCase(final CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public ResultPage<Card> handle(final PageSpec pageSpec) {
        return cardRepository.findAll(pageSpec);
    }

}
