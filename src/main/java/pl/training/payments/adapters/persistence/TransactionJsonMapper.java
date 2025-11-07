package pl.training.payments.adapters.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import pl.training.payments.domain.Transaction;

import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Mapper do serializacji/deserializacji transakcji do/z formatu JSON.
 * Używany do przechowywania listy transakcji jako JSON w bazie danych.
 * 
 * @Component - rejestracja jako Spring bean
 * 
 * Alternatywa do relacyjnego modelu z osobną tabelą transakcji:
 * - Prostsze w implementacji (brak relacji)
 * - Szybsze odczytywanie całej historii
 * - Brak możliwości zapytań SQL po transakcjach
 * - Trudniejsza analiza danych
 * 
 * W Spring Boot Jackson jest domyślnym parserem JSON, ale tu używamy
 * własnej instancji ObjectMapper z customową konfiguracją.
 */
@Component
public class TransactionJsonMapper {

    /**
     * Statyczna instancja ObjectMapper - thread-safe po konfiguracji.
     * 
     * Konfiguracja:
     * 
     * .registerModule(new JavaTimeModule()) - obsługa Java Time API:
     * - Serializuje LocalDateTime, ZonedDateTime, Instant do ISO-8601
     * - Bez tego modułu Java 8 time types nie działają w Jackson
     * - Spring Boot automatycznie dodaje ten moduł do swojego ObjectMapper
     * 
     * .configure(FAIL_ON_UNKNOWN_PROPERTIES, false) - elastyczna deserializacja:
     * - Ignoruje nieznane pola w JSON (nie rzuca wyjątku)
     * - Przydatne przy ewolucji API - stare dane nadal działają
     * - Alternatywa: @JsonIgnoreProperties(ignoreUnknown = true)
     * 
     * Inne przydatne opcje:
     * - SerializationFeature.WRITE_DATES_AS_TIMESTAMPS - daty jako timestamp
     * - DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY - elastyczne parsowanie
     * - setPropertyNamingStrategy() - snake_case, camelCase, etc.
     */
    private final static ObjectMapper JSON_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Serializuje listę transakcji do JSON string.
     * 
     * writeValueAsString() - konwertuje obiekt Java na JSON:
     * - Używa refleksji do odczytu pól/getterów
     * - Respektuje adnotacje Jackson (@JsonProperty, @JsonIgnore)
     * - Obsługuje rekurencyjne struktury
     * 
     * JsonProcessingException - checked exception Jacksona
     * Opakowujemy w RuntimeException bo:
     * - Upraszcza API (brak checked exceptions)
     * - Błąd serializacji to zazwyczaj bug, nie błąd biznesowy
     * - Spring i tak zamieni to na 500 Internal Server Error
     */
    public String write(final List<Transaction> transactions) {
        try {
            return JSON_MAPPER.writeValueAsString(transactions);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Deserializuje JSON do obiektu określonego typu.
     * 
     * @param <T> - typ generyczny zwracanego obiektu
     * @param json - string JSON do parsowania
     * @param type - TypeReference dla zachowania informacji o typach generycznych
     * 
     * TypeReference - rozwiązuje problem type erasure w Javie:
     * - Bez tego: readValue(json, List.class) zwraca List<LinkedHashMap>
     * - Z TypeReference: readValue(json, new TypeReference<List<Transaction>>(){})
     *   zachowuje pełną informację o typie
     * 
     * Przykład użycia:
     * List<Transaction> transactions = read(json, new TypeReference<List<Transaction>>(){});
     */
    public <T> T read(final String json, TypeReference<T> type) {
        try {
            return JSON_MAPPER.readValue(json, type);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

}