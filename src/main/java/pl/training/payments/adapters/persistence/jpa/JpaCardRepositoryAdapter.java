package pl.training.payments.adapters.persistence.jpa;

import org.springframework.context.annotation.Primary;
import pl.training.common.component.Adapter;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.payments.application.CardRepository;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

import java.util.Optional;

@Primary
@Adapter
class JpaCardRepositoryAdapter implements CardRepository {

    private final JpaCardRepository cardRepository;
    private final JpaCardRepositoryMapper mapper;

    public JpaCardRepositoryAdapter(final JpaCardRepository cardRepository, final JpaCardRepositoryMapper mapper) {
        this.cardRepository = cardRepository;
        this.mapper = mapper;
    }

    @Override
    public Card save(final Card card) {
        var cardEntity = mapper.toEntity(card);
        var savedCardEntity = cardRepository.save(cardEntity);
        return mapper.toDomain(savedCardEntity);
    }

    @Override
    public ResultPage<Card> findAll(PageSpec pageSpec) {
        var pageRequest = mapper.toEntity(pageSpec);
        var cardEntityPage = cardRepository.findAll(pageRequest);
        return mapper.toDomain(cardEntityPage);
    }

    @Override
    public Optional<Card> findByNumber(CardNumber cardNumber) {
        var number = mapper.toEntity(cardNumber);
        return cardRepository.findByNumber(number)
                .map(mapper::toDomain);
    }

}
