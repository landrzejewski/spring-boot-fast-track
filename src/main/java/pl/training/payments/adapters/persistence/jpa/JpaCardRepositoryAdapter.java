package pl.training.payments.adapters.persistence.jpa;

import org.springframework.stereotype.Repository;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.payments.application.CardRepository;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

import java.util.Optional;

@Repository
public class JpaCardRepositoryAdapter implements CardRepository {

    private final JpaCardRepository repository;
    private final JpaCardRepositoryMapper mapper;

    public JpaCardRepositoryAdapter(JpaCardRepository repository, JpaCardRepositoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Card save(Card card) {
        var cardEntity = mapper.toEntity(card);
        var savedCardEntity = repository.save(cardEntity);
        return mapper.toDomain(savedCardEntity);
    }

    @Override
    public ResultPage<Card> findAll(PageSpec pageSpec) {
        var pageRequest = mapper.toEntity(pageSpec);
        var cardEntityPage = repository.findAll(pageRequest);
        return mapper.toDomain(cardEntityPage);
    }

    @Override
    public Optional<Card> findByNumber(CardNumber cardNumber) {
        var number = mapper.toEntity(cardNumber);
        return repository.findByNumber(number)
                .map(mapper::toDomain);
    }

}
