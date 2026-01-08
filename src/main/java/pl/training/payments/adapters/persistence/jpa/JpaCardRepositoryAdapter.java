package pl.training.payments.adapters.persistence.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.payments.application.CardRepository;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

import java.util.Optional;

@Primary
@Repository
public class JpaCardRepositoryAdapter implements CardRepository {

    private final JpaCardRepository repository;

    public JpaCardRepositoryAdapter(JpaCardRepository repository) {
        this.repository = repository;
    }

    @Override
    public Card save(Card card) {

        return null;
    }

    @Override
    public ResultPage<Card> findAll(PageSpec pageSpec) {
        return null;
    }

    @Override
    public Optional<Card> findByNumber(CardNumber cardNumber) {
        return Optional.empty();
    }

}
