package pl.training.common.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.logging.Logger;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Globalny handler wyjątków dla wszystkich kontrolerów REST.
 * Zapewnia spójną obsługę błędów w całej aplikacji.
 * 
 * @ControllerAdvice - globalna obsługa wyjątków w Spring MVC:
 * - Centralizuje logikę obsługi błędów
 * - Unika duplikacji kodu w kontrolerach
 * - Zapewnia spójne odpowiedzi błędów
 * 
 * Parametr annotations = RestController.class:
 * - Ogranicza zakres tylko do klas z @RestController
 * - Nie dotyczy @Controller (które zwracają widoki)
 * - Można też użyć: basePackages, assignableTypes
 * 
 * Kolejność obsługi wyjątków:
 * 1. Najbardziej specyficzny handler (np. CardNotFoundException)
 * 2. Mniej specyficzny (np. RuntimeException)
 * 3. Najbardziej ogólny (Exception.class)
 * 
 * Import statyczny dla czytelności:
 * - HttpStatus.BAD_REQUEST zamiast pełnej ścieżki
 * - Collectors.joining dla stream operations
 */
@ControllerAdvice(annotations = RestController.class)
public final class GlobalRestExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(GlobalRestExceptionHandler.class.getName());
    private static final String KEY_VALUE_SEPARATOR = " - ";
    private static final String DELIMITER = ", ";

    private final RestExceptionResponseBuilder responseBuilder;

    public GlobalRestExceptionHandler(final RestExceptionResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    /**
     * Uniwersalny handler dla wszystkich nieobsłużonych wyjątków.
     * Ostatnia linia obrony - łapie wszystko co nie zostało obsłużone wcześniej.
     * 
     * @ExceptionHandler(Exception.class) - obsługuje Exception i wszystkie podklasy
     * 
     * Zwraca 500 Internal Server Error dla nieoczekiwanych błędów.
     * W produkcji należy:
     * - Logować pełny stack trace (tu tylko info)
     * - Nie ujawniać szczegółów technicznych klientowi
     * - Wysłać alerty do zespołu (np. przez Sentry, ELK)
     * 
     * Locale - automatycznie wstrzykiwany przez Spring na podstawie:
     * - Nagłówka Accept-Language
     * - Sesji użytkownika
     * - Domyślnego locale serwera
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> onException(final Exception exception, final Locale locale) {
        LOGGER.info("Exception occurred: " + exception);
        return responseBuilder.build(exception, INTERNAL_SERVER_ERROR, locale);
    }

    /**
     * Handler dla błędów walidacji Bean Validation.
     * 
     * MethodArgumentNotValidException - rzucany gdy:
     * - @Valid/@Validated na parametrze kontrolera
     * - Walidacja nie przeszła (np. @NotNull, @Size, @Pattern)
     * - Dotyczy @RequestBody, @RequestParam, @PathVariable
     * 
     * Zwraca 400 Bad Request z listą błędów walidacji.
     * 
     * Spring automatycznie:
     * - Uruchamia walidatory przed wywołaniem metody kontrolera
     * - Zbiera wszystkie błędy w BindingResult
     * - Rzuca wyjątek jeśli są błędy
     * 
     * Ten handler formatuje błędy w czytelny sposób dla klienta API.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> onMethodArgumentNotValid(final MethodArgumentNotValidException exception, final Locale locale) {
        var description = responseBuilder.getLocalizedMessage(exception, locale, getValidationErrors(exception));
        return responseBuilder.build(description, BAD_REQUEST);
    }

    /**
     * Ekstraktuje i formatuje błędy walidacji.
     * 
     * BindingResult zawiera:
     * - FieldError - błędy konkretnych pól
     * - ObjectError - błędy całego obiektu (cross-field)
     * - GlobalError - błędy globalne
     * 
     * Stream API do przetwarzania:
     * 1. getFieldErrors() - lista błędów pól
     * 2. map() - formatuje każdy błąd jako "pole - komunikat"
     * 3. joining() - łączy w jeden string z separatorem
     * 
     * Przykładowy wynik:
     * "amount - musi być większe niż 0, currencyCode - nieprawidłowy format"
     * 
     * Alternatywy:
     * - Zwróć strukturę JSON z błędami per pole
     * - Użyć kodu błędu zamiast komunikatu
     * - Dodać więcej kontekstu (wartość, oczekiwany format)
     */
    private String getValidationErrors(final MethodArgumentNotValidException exception) {
        return exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + KEY_VALUE_SEPARATOR + fieldError.getDefaultMessage())
                .collect(joining(DELIMITER));
    }

}
