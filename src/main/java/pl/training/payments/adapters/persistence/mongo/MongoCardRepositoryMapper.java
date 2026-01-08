package pl.training.payments.adapters.persistence.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.payments.adapters.persistence.TransactionJsonMapper;
import pl.training.payments.adapters.persistence.jpa.CardEntity;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardId;
import pl.training.payments.domain.CardNumber;
import pl.training.payments.domain.Transaction;
import tools.jackson.core.type.TypeReference;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Component
public record MongoCardRepositoryMapper(TransactionJsonMapper jsonMapper) {

    private final static TypeReference<List<Transaction>> TRANSACTION_LIST_TYPE = new TypeReference<>() {
    };

    public CardDocument toDocument(final Card card) {
        var cardDocument = new CardDocument();
        cardDocument.setId(toDocument(card.getId()));
        cardDocument.setNumber(toDocument(card.getNumber()));
        cardDocument.setExpiration(card.getExpiration());
        cardDocument.setCurrencyCode(toDocument(card.getCurrency()));
        cardDocument.setTransactions(jsonMapper.write(card.getTransactions()));
        return cardDocument;
    }

    private String toDocument(final CardId cardId) {
        return cardId.value().toString();
    }

    private String toDocument(final Currency currency) {
        return currency.getCurrencyCode();
    }

    public String toDocument(final CardNumber cardNumber) {
        return cardNumber.value();
    }

    public PageRequest toDocument(final PageSpec pageSpec) {
        return PageRequest.of(pageSpec.index(), pageSpec.size());
    }

    public Card toDomain(final CardDocument cardDocument) {
        var cardId = toDomain(cardDocument.getId());
        var cardNumber = new CardNumber(cardDocument.getNumber());
        var currency = Currency.getInstance(cardDocument.getCurrencyCode());
        var expiration = cardDocument.getExpiration();

        var card = new Card(cardId, cardNumber, expiration, currency);
        if (cardDocument.getTransactions() != null) {
            jsonMapper.read(cardDocument.getTransactions(), TRANSACTION_LIST_TYPE).forEach(card::registerTransaction);
        }
        return card;
    }

    private CardId toDomain(String id) {
        return new CardId(UUID.fromString(id));
    }

    public ResultPage<Card> toDomain(final Page<CardDocument> page) {
        return new ResultPage<>(
                page.stream().map(this::toDomain).toList(),
                new PageSpec(page.getNumber(), page.getSize()),
                page.getTotalPages()
        );
    }

}

