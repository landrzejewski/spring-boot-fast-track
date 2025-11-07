package pl.training.payments.adapters.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.training.payments.application.GetCardUseCase;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;
import pl.training.payments.domain.Transaction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Kontroler REST do pobierania szczegółów karty płatniczej.
 * 
 * @RestController - kontroler zwracający dane (JSON) zamiast widoków
 * 
 * @RequestMapping("api/cards") - bazowa ścieżka dla wszystkich endpointów w klasie.
 * Wszystkie mapowania w tej klasie będą poprzedzone "api/cards".
 * Można tu też określić:
 * - produces - format odpowiedzi (np. APPLICATION_JSON_VALUE)
 * - consumes - akceptowany format żądania
 * - headers - wymagane nagłówki
 * - params - wymagane parametry
 * 
 * Klasa jest final - dobra praktyka dla klas nie przeznaczonych do dziedziczenia.
 * Zwiększa bezpieczeństwo i wydajność (JVM może lepiej optymalizować).
 */
@RestController
@RequestMapping("api/cards")
final class GetCardRestController {

    private final GetCardUseCase getCardUseCase;

    GetCardRestController(final GetCardUseCase getCardUseCase) {
        this.getCardUseCase = getCardUseCase;
    }

    /**
     * @GetMapping - mapuje żądania GET na tę metodę.
     * 
     * Ścieżka "{number:\\d{16,19}}" zawiera:
     * - {number} - zmienna ścieżki (path variable)
     * - :\\d{16,19} - wyrażenie regularne walidujące:
     *   - \\d - cyfra (podwójny backslash w Javie)
     *   - {16,19} - od 16 do 19 cyfr (standardowa długość numeru karty)
     * 
     * Pełna ścieżka: GET /api/cards/{number} gdzie number to 16-19 cyfr
     * 
     * @PathVariable - wiąże wartość z URL ze zmienną metody:
     * - /api/cards/1234567890123456 -> number = "1234567890123456"
     * - Nazwa zmiennej musi pasować do {number} w ścieżce
     * - Można użyć @PathVariable("customName") dla innych nazw
     * 
     * @Validated - włącza walidację, choć tu regex w URL już waliduje format
     * 
     * ResponseEntity.ok() - skrót dla status 200 OK z treścią
     */
    @GetMapping("{number:\\d{16,19}}")
    ResponseEntity<GetCardResponse> getCard(@Validated @PathVariable final String number) {
        // Konwersja String na Value Object CardNumber
        var cardNumber = new CardNumber(number);
        
        // Wywołanie warstwy aplikacji
        var card = getCardUseCase.handle(cardNumber);
        
        // Zwrot 200 OK z DTO utworzonym z obiektu domenowego
        return ResponseEntity.ok(GetCardResponse.from(card));
    }

}

record GetCardResponse(String number, LocalDate expiration, Double balance, String currencyCode,
                       List<CardTransactionResponse> transactions) {

    static GetCardResponse from(Card card) {
        return new GetCardResponse(
                card.getNumber().value(),
                card.getExpiration(),
                card.getBalance().amount().doubleValue(),
                card.getCurrency().getCurrencyCode(),
                card.getTransactions().stream().map(CardTransactionResponse::from).toList()
        );
    }

}

record CardTransactionResponse(Instant timestamp, Double value, String type) {

    /**
     * Mapowanie z modelu domenowego na DTO z użyciem Java 17+ features.
     * 
     * Switch expression (Java 14+):
     * - Zwięższe niż tradycyjny switch
     * - Exhaustive - kompilator sprawdza wszystkie przypadki enum
     * - Arrow syntax (->) bez break
     * 
     * Konwersje typów:
     * - ZonedDateTime -> Instant (standard ISO-8601 w JSON)
     * - BigDecimal -> Double (prostsze dla API, ale traci precyzję!)
     * - Enum -> String (czytelne dla klientów API)
     */
    static CardTransactionResponse from(Transaction transaction) {
        return new CardTransactionResponse(
                transaction.timestamp().toInstant(),
                transaction.value().amount().doubleValue(),
                switch (transaction.type()) {
                    case INFLOW -> "IN";
                    case PAYMENT -> "OUT";
                }
        );
    }

}
