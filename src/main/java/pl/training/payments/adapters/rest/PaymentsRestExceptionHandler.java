package pl.training.payments.adapters.rest;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.training.common.web.ExceptionResponse;
import pl.training.common.web.RestExceptionResponseBuilder;
import pl.training.payments.application.CardNotFoundException;
import pl.training.payments.domain.InsufficientBalanceException;

import java.util.Locale;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Centralna obsługa wyjątków dla kontrolerów REST modułu płatności.
 * 
 * @ControllerAdvice - kluczowa adnotacja Spring MVC do globalnej obsługi wyjątków:
 * - Centralizuje logikę obsługi błędów
 * - Działa jak aspekt AOP dla kontrolerów
 * - Może przechwytywać wyjątki z wielu kontrolerów
 * 
 * Parametr basePackages ogranicza zakres do konkretnego pakietu.
 * Alternatywy:
 * - basePackageClasses - przez klasę referencyjną
 * - assignableTypes - konkretne typy kontrolerów
 * - annotations - kontrolery z określoną adnotacją
 * 
 * @Order(HIGHEST_PRECEDENCE) - najwyższy priorytet wykonania.
 * Ważne gdy istnieje wiele @ControllerAdvice:
 * - Niższe wartości = wyższy priorytet
 * - HIGHEST_PRECEDENCE = Integer.MIN_VALUE
 * - Ten handler obsłuży wyjątki przed bardziej ogólnymi handlerami
 */
@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice(basePackages = "pl.training.payments.adapters.rest")
final class PaymentsRestExceptionHandler {

    private final RestExceptionResponseBuilder exceptionResponseBuilder;

    PaymentsRestExceptionHandler(final RestExceptionResponseBuilder exceptionResponseBuilder) {
        this.exceptionResponseBuilder = exceptionResponseBuilder;
    }

    /**
     * @ExceptionHandler - metoda obsługująca konkretny typ wyjątku.
     * 
     * Gdy kontroler rzuci CardNotFoundException:
     * 1. Spring przechwyci wyjątek
     * 2. Znajdzie pasującą metodę @ExceptionHandler
     * 3. Wywoła ją zamiast domyślnej obsługi błędów
     * 4. Zwróci ResponseEntity jako odpowiedź HTTP
     * 
     * Parametr Locale - Spring automatycznie wstrzykuje na podstawie:
     * - Nagłówka Accept-Language
     * - Sesji użytkownika
     * - Domyślnego locale aplikacji
     * 
     * Używany do internacjonalizacji komunikatów błędów.
     */
    @ExceptionHandler(CardNotFoundException.class)
    ResponseEntity<ExceptionResponse> onCardNotFoundException(final CardNotFoundException exception, final Locale locale) {
        // Builder tworzy ustandaryzowaną odpowiedź błędu z kodem 404
        return exceptionResponseBuilder.build(exception, NOT_FOUND, locale);
    }

    /**
     * Obsługa wyjątku niewystarczających środków.
     * 
     * HttpStatus.BAD_REQUEST (400) - błąd po stronie klienta.
     * Klient próbował wykonać operację z nieprawidłowymi danymi.
     * 
     * Inne typowe statusy dla wyjątków:
     * - 401 UNAUTHORIZED - brak autentykacji
     * - 403 FORBIDDEN - brak autoryzacji
     * - 404 NOT_FOUND - zasób nie istnieje
     * - 409 CONFLICT - konflikt stanu zasobu
     * - 422 UNPROCESSABLE_ENTITY - nieprawidłowe dane biznesowe
     * - 500 INTERNAL_SERVER_ERROR - błąd serwera
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    ResponseEntity<ExceptionResponse> onInsufficientBalanceException(final InsufficientBalanceException exception, final Locale locale) {
        return exceptionResponseBuilder.build(exception, BAD_REQUEST, locale);
    }

}
