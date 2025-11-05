package pl.training.payments.application;

import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

import java.util.Optional;

public interface CardRepository {

    Card save(Card card);

    ResultPage<Card> findAll(PageSpec pageSpec);

    Optional<Card> findByNumber(CardNumber cardNumber);

}
