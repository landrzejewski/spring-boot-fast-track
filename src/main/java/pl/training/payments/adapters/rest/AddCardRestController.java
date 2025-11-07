package pl.training.payments.adapters.rest;

import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.training.common.web.LocationUri;
import pl.training.payments.application.AddCardUseCase;
import pl.training.payments.domain.Card;

import java.time.LocalDate;
import java.util.Currency;

/**
 * Kontroler REST do tworzenia nowych kart płatniczych.
 * 
 * @RestController - kluczowa adnotacja Spring Web łącząca:
 * - @Controller - rejestruje klasę jako kontroler Spring MVC
 * - @ResponseBody - automatyczna serializacja wartości zwracanych do JSON/XML
 * 
 * Spring automatycznie:
 * - Mapuje żądania HTTP na metody kontrolera
 * - Deserializuje JSON/XML z request body na obiekty Java
 * - Serializuje obiekty Java na JSON/XML w response body
 * - Obsługuje content negotiation (Accept/Content-Type headers)
 * - Integruje z Jackson dla JSON lub JAXB dla XML
 */
@RestController
public class AddCardRestController {

    private final AddCardUseCase addCardUseCase;

    public AddCardRestController(final AddCardUseCase addCardUseCase) {
        this.addCardUseCase = addCardUseCase;
    }

    /**
     * @PostMapping - mapuje żądania HTTP POST na tę metodę.
     * Parametr określa ścieżkę URL: POST /api/cards
     * 
     * Inne mapowania HTTP:
     * - @GetMapping - dla GET (pobieranie zasobów)
     * - @PutMapping - dla PUT (aktualizacja całego zasobu)
     * - @PatchMapping - dla PATCH (częściowa aktualizacja)
     * - @DeleteMapping - dla DELETE (usuwanie zasobów)
     * 
     * @Validated - włącza walidację Bean Validation (JSR-303/380).
     * Sprawdza adnotacje walidacyjne na polach obiektu AddCardRequest.
     * Jeśli walidacja nie przejdzie, Spring zwróci 400 Bad Request.
     * 
     * @RequestBody - wiąże treść żądania HTTP z parametrem metody:
     * - Automatyczna deserializacja JSON na AddCardRequest
     * - Używa HttpMessageConverter (domyślnie Jackson dla JSON)
     * - Obsługuje różne Content-Type (json, xml, itp.)
     * 
     * ResponseEntity<T> - elastyczna kontrola nad odpowiedzią HTTP:
     * - Status code, headers, body
     * - Builder pattern dla czytelności
     * - Type-safe alternatywa dla @ResponseStatus
     */
    @PostMapping("api/cards")
    ResponseEntity<AddCardResponse> addCard(@Validated @RequestBody final AddCardRequest addCardRequest) {
        // Wywołanie warstwy aplikacji (use case)
        var card = addCardUseCase.handle(addCardRequest.currency());
        
        // Przygotowanie URI do nowo utworzonego zasobu
        var cardNumber = card.getNumber().value();
        var locationUri = LocationUri.fromRequest(cardNumber);
        
        // Zwrot 201 Created z nagłówkiem Location i treścią odpowiedzi
        // Location header wskazuje URL nowego zasobu (REST best practice)
        return ResponseEntity.created(locationUri)
                .body(AddCardResponse.from(card));
    }

}

/**
 * Record jako DTO (Data Transfer Object) dla żądania tworzenia karty.
 * 
 * Java Records (od Java 14):
 * - Automatyczne final fields, konstruktor, getters, equals, hashCode, toString
 * - Idealny dla immutable DTO
 * - Zwięzła składnia zamiast boilerplate kodu
 * 
 * @Pattern - adnotacja Bean Validation sprawdzająca wyrażenie regularne:
 * - "[A-Z]{3}" - dokładnie 3 duże litery (np. USD, EUR, PLN)
 * - Walidacja wykonywana przez @Validated w kontrolerze
 * - Błąd walidacji = 400 Bad Request z opisem błędu
 */
record AddCardRequest(@Pattern(regexp = "[A-Z]{3}") String currencyCode) {

    /**
     * Metoda pomocnicza konwertująca kod waluty na obiekt Currency.
     * Przykład enkapsulacji logiki konwersji w DTO.
     * Currency.getInstance() rzuca IllegalArgumentException dla nieprawidłowego kodu.
     */
    Currency currency() {
        return Currency.getInstance(currencyCode);
    }

}

/**
 * Record jako DTO dla odpowiedzi z danymi utworzonej karty.
 * 
 * Separacja modelu domenowego od API:
 * - Card (domena) nie jest bezpośrednio eksponowany przez API
 * - AddCardResponse zawiera tylko wybrane dane dla klienta
 * - Unikamy wycieków abstrakcji i coupling między warstwami
 * 
 * Spring automatycznie serializuje record do JSON:
 * {
 *   "number": "1234567890123456",
 *   "expiration": "2025-12-31"
 * }
 */
record AddCardResponse(String number, LocalDate expiration) {

    /**
     * Factory method - wzorzec do tworzenia DTO z obiektu domenowego.
     * Statyczna metoda fabrykująca zapewnia:
     * - Czytelny kod w kontrolerze
     * - Enkapsulację logiki mapowania
     * - Łatwość testowania
     * - Jeden punkt zmian przy modyfikacji mapowania
     */
    static AddCardResponse from(final Card card) {
        return new AddCardResponse(card.getNumber().value(), card.getExpiration());
    }

}