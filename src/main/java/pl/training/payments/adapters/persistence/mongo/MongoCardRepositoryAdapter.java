package pl.training.payments.adapters.persistence.mongo;

import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.common.component.Adapter;
import pl.training.payments.application.CardRepository;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY)
@Primary
@Adapter
class MongoCardRepositoryAdapter implements CardRepository {

    private final MongoCardRepository cardRepository;
    private final MongoCardRepositoryMapper mapper;

    public MongoCardRepositoryAdapter(final MongoCardRepository cardRepository, final MongoCardRepositoryMapper mapper) {
        this.cardRepository = cardRepository;
        this.mapper = mapper;
    }

    @Override
    public Card save(final Card card) {
        var cardEntity = mapper.toDocument(card);
        var savedCardEntity = cardRepository.save(cardEntity);
        return mapper.toDomain(savedCardEntity);
    }

    @Override
    public ResultPage<Card> findAll(PageSpec pageSpec) {
        var pageRequest = mapper.toDocument(pageSpec);
        var cardEntityPage = cardRepository.findAll(pageRequest);
        return mapper.toDomain(cardEntityPage);
    }

    @Override
    public Optional<Card> findByNumber(CardNumber cardNumber) {
        var number = mapper.toDocument(cardNumber);
        return cardRepository.findByNumber(number)
                .map(mapper::toDomain);
    }

}
