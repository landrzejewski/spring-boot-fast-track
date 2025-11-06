package pl.training.payments.adapters.persistence.jpa;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.training.common.component.Mapper;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardId;
import pl.training.payments.domain.CardNumber;
import pl.training.payments.domain.Transaction;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Mapper
final class JpaCardRepositoryMapper {

    private final static TypeReference<List<Transaction>> TRANSACTION_LIST_TYPE = new TypeReference<>() {
    };

    private final TransactionJsonMapper jsonMapper;

    JpaCardRepositoryMapper(final TransactionJsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    CardEntity toEntity(final Card card) {
        var cardEntity = new CardEntity();
        cardEntity.setId(toEntity(card.getId()));
        cardEntity.setNumber(toEntity(card.getNumber()));
        cardEntity.setExpiration(card.getExpiration());
        cardEntity.setCurrencyCode(toEntity(card.getCurrency()));
        cardEntity.setTransactions(jsonMapper.write(card.getTransactions()));
        return cardEntity;
    }

    private String toEntity(final CardId cardId) {
        return cardId.value().toString();
    }

    private String toEntity(final Currency currency) {
        return currency.getCurrencyCode();
    }

    String toEntity(final CardNumber cardNumber) {
        return cardNumber.value();
    }

    PageRequest toEntity(final PageSpec pageSpec) {
        return PageRequest.of(pageSpec.index(), pageSpec.size());
    }

    Card toDomain(final CardEntity cardEntity) {
        var cardId = toDomain(cardEntity.getId());
        var cardNumber = new CardNumber(cardEntity.getNumber());
        var currency = Currency.getInstance(cardEntity.getCurrencyCode());
        var expiration = cardEntity.getExpiration();

        var card = new Card(cardId, cardNumber, expiration, currency);
        if (cardEntity.getTransactions() != null) {
            jsonMapper.read(cardEntity.getTransactions(), TRANSACTION_LIST_TYPE).forEach(card::registerTransaction);
        }
        return card;
    }

    private CardId toDomain(String id) {
        return new CardId(UUID.fromString(id));
    }

    ResultPage<Card> toDomain(final Page<CardEntity> page) {
        return new ResultPage<>(
                page.stream().map(this::toDomain).toList(),
                new PageSpec(page.getNumber(), page.getSize()),
                page.getTotalPages()
        );
    }

}
