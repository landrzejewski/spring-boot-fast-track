package pl.training.payments.adapters.persistence.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import pl.training.payments.domain.Transaction;

import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Component
class TransactionJsonMapper {

    private final static ObjectMapper JSON_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    public String write(final List<Transaction> transactions) {
        try {
            return JSON_MAPPER.writeValueAsString(transactions);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    public <T> T read(final String json, TypeReference<T> type) {
        try {
            return JSON_MAPPER.readValue(json, type);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

}