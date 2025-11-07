package pl.training.payments.adapters.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.training.common.validation.Range;
import pl.training.common.web.ExceptionResponse;
import pl.training.payments.application.AddTransactionUseCase;
import pl.training.payments.application.CardNotFoundException;
import pl.training.payments.domain.CardNumber;
import pl.training.payments.domain.Money;
import pl.training.payments.domain.TransactionType;

import static pl.training.payments.domain.TransactionType.INFLOW;
import static pl.training.payments.domain.TransactionType.PAYMENT;

/**
 * Kontroler REST do dodawania transakcji na karcie płatniczej.
 * 
 * @RestController - kontroler REST zwracający dane (nie widoki)
 * 
 * Importy statyczne (import static) dla TransactionType:
 * - Zwiększa czytelność kodu (INFLOW zamiast TransactionType.INFLOW)
 * - Zalecane dla często używanych stałych
 * - Uważać na konflikty nazw
 */
@RestController
final class AddCardTransactionRestController {

    private final AddTransactionUseCase addTransactionUseCase;

    AddCardTransactionRestController(final AddTransactionUseCase addTransactionUseCase) {
        this.addTransactionUseCase = addTransactionUseCase;
    }

    /**
     * Endpoint do dodawania transakcji na karcie.
     * POST /api/cards/{number}/transactions
     * 
     * @PostMapping - mapuje żądania POST na podaną ścieżkę
     * 
     * Ścieżka "api/cards/{number:\\d{16,19}}/transactions":
     * - {number} - zmienna ścieżki (path variable)
     * - :\\d{16,19} - regex walidujący numer karty (16-19 cyfr)
     * - /transactions - zasób podrzędny (sub-resource) karty
     * 
     * RESTful URL design:
     * - Karty to zasób główny (/cards)
     * - Transakcje to podzasób konkretnej karty
     * - POST tworzy nową transakcję na karcie
     * 
     * @PathVariable - wiąże {number} z parametrem metody
     * Walidacja regex w URL działa jako pierwsza linia obrony
     * 
     * @Validated - włącza Bean Validation dla obiektu z @RequestBody
     * Sprawdza adnotacje: @Range, @Pattern, @NotNull na polach
     * 
     * ResponseEntity<Void> - odpowiedź bez treści (body)
     * Status 204 No Content dla pomyślnie utworzonych zasobów bez zwracania danych
     */
    @PostMapping("api/cards/{number:\\d{16,19}}/transactions")
    ResponseEntity<Void> addCardTransaction(
            @PathVariable final String number,
            @Validated @RequestBody final AddCardTransactionRequest addCardTransactionRequest) {
        var cardNumber = new CardNumber(number);
        var amount = addCardTransactionRequest.money();
        var transactionType = addCardTransactionRequest.transactionType();
        
        // Wywołanie use case - może rzucić wyjątki biznesowe
        addTransactionUseCase.handle(cardNumber, amount, transactionType);
        
        // 204 No Content - sukces bez zwracania danych
        // Alternatywa: 201 Created z Location header
        return ResponseEntity.noContent().build();
    }

   /**
    * Zakomentowana lokalna obsługa wyjątku.
    * 
    * @ExceptionHandler - obsługuje wyjątki w tym kontrolerze
    * Zakomentowane, bo używamy globalnej obsługi w PaymentsRestExceptionHandler
    * 
    * Lokalna obsługa wyjątków:
    * - Pros: Specyficzna dla kontrolera logika
    * - Cons: Duplikacja kodu między kontrolerami
    * 
    * Lepsze rozwiązanie: @ControllerAdvice dla całego modułu
    */
   /* @ExceptionHandler(CardNotFoundException.class)
    ResponseEntity<ExceptionResponse> onCardNotFoundException(final CardNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse("Card not found"));
    }*/

}

/**
 * DTO dla żądania dodania transakcji.
 * Java Record - immutable data carrier z automatycznymi getters, equals, hashCode, toString.
 * 
 * Walidacja pól za pomocą Bean Validation:
 * 
 * @Range(minValue = 1, maxValue = 100) - własny walidator zakresu
 * - Kwota musi być między 1 a 100
 * - Alternatywa: @Min(1) @Max(100) z javax.validation
 * 
 * @Pattern(regexp = "[A-Z]{3}") - walidacja regex
 * - Dokładnie 3 wielkie litery (kody walut ISO 4217)
 * - Przykłady: USD, EUR, PLN
 * 
 * @NotNull - pole wymagane
 * - null spowoduje 400 Bad Request
 * - Dla String lepiej @NotBlank (sprawdza też pusty string)
 */
record AddCardTransactionRequest(@Range(minValue = 1, maxValue = 100) Double amount,
                                 @Pattern(regexp = "[A-Z]{3}") String currencyCode,
                                 @NotNull String type) {

    /**
     * Factory method tworzący Value Object Money.
     * Enkapsulacja logiki tworzenia w DTO.
     */
    Money money() {
        return new Money(amount, currencyCode);
    }

    /**
     * Mapowanie String na enum TransactionType.
     * 
     * Switch expression (Java 14+):
     * - Kompaktowa składnia bez break
     * - Wymusza obsługę wszystkich przypadków
     * - -> zamiast : (expression, nie statement)
     * 
     * Rzuca IllegalStateException dla nieznanych typów.
     * Alternatywa: użyć enum bezpośrednio w DTO z @JsonValue.
     */
    TransactionType transactionType() {
        return switch (type) {
            case "IN" -> INFLOW;
            case "OUT" -> PAYMENT;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

}

