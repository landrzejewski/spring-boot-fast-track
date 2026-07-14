package pl.training.payments.adapters.persistence;

import org.springframework.stereotype.Component;
import pl.training.payments.domain.Transaction;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class TransactionJsonMapper {

    private final static ObjectMapper JSON_MAPPER = new ObjectMapper();

    public String write(final List<Transaction> transactions) {
        try {
            return JSON_MAPPER.writeValueAsString(transactions);
        } catch (JacksonException exception) {
            throw new RuntimeException(exception);
        }
    }

    public <T> T read(final String json, TypeReference<T> type) {
        try {
            return JSON_MAPPER.readValue(json, type);
        } catch (JacksonException exception) {
            throw new RuntimeException(exception);
        }
    }

}