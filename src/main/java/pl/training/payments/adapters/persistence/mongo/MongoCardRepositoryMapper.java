package pl.training.payments.adapters.persistence.mongo;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.common.component.Mapper;
import pl.training.payments.adapters.persistence.TransactionJsonMapper;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardId;
import pl.training.payments.domain.CardNumber;
import pl.training.payments.domain.Transaction;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Mapper
record MongoCardRepositoryMapper(TransactionJsonMapper jsonMapper) {

    private final static TypeReference<List<Transaction>> TRANSACTION_LIST_TYPE = new TypeReference<>() {
    };

    CardDocument toDocument(final Card card) {
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

    String toDocument(final CardNumber cardNumber) {
        return cardNumber.value();
    }

    PageRequest toDocument(final PageSpec pageSpec) {
        return PageRequest.of(pageSpec.index(), pageSpec.size());
    }

    Card toDomain(final CardDocument cardDocument) {
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

    ResultPage<Card> toDomain(final Page<CardDocument> page) {
        return new ResultPage<>(
                page.stream().map(this::toDomain).toList(),
                new PageSpec(page.getNumber(), page.getSize()),
                page.getTotalPages()
        );
    }

}
