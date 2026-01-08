package pl.training.payments.adapters.persistence.mongo;

import org.springframework.stereotype.Repository;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.payments.application.CardRepository;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

import java.util.Optional;

@Repository
public class MongoCardRepositoryAdapter implements CardRepository {

    private final MongoCardRepository repository;
    private final MongoCardRepositoryMapper mapper;

    public MongoCardRepositoryAdapter(final MongoCardRepository repository, final MongoCardRepositoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Card save(final Card card) {
        var cardEntity = mapper.toDocument(card);
        var savedCardEntity = repository.save(cardEntity);
        return mapper.toDomain(savedCardEntity);
    }

    @Override
    public ResultPage<Card> findAll(PageSpec pageSpec) {
        var pageRequest = mapper.toDocument(pageSpec);
        var cardEntityPage = repository.findAll(pageRequest);
        return mapper.toDomain(cardEntityPage);
    }

    @Override
    public Optional<Card> findByNumber(CardNumber cardNumber) {
        var number = mapper.toDocument(cardNumber);
        return repository.findByNumber(number)
                .map(mapper::toDomain);
    }

}
