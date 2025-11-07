package pl.training.common.web;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Builder do tworzenia spójnych odpowiedzi błędów REST z obsługą i18n.
 * Centralizuje logikę tworzenia ExceptionResponse z lokalizacją komunikatów.
 * 
 * @Component - rejestracja jako Spring bean
 * Alternatywa: @Service dla semantyki serwisu pomocniczego
 * 
 * Wykorzystuje wzorzec Builder dla czytelnego API:
 * - build(String, HttpStatus) - proste błędy
 * - build(Exception, HttpStatus, Locale) - błędy z i18n
 * 
 * Integracja z Spring i18n poprzez MessageSource.
 */
@Component
public final class RestExceptionResponseBuilder {

    /**
     * MessageSource - główny mechanizm internacjonalizacji w Spring.
     * 
     * Spring automatycznie konfiguruje MessageSource na podstawie:
     * - spring.messages.basename w application.yml
     * - Plików .properties (exceptions.properties, exceptions_pl.properties)
     * 
     * Hierarchia wyszukiwania komunikatów:
     * 1. Dokładne dopasowanie locale (pl_PL)
     * 2. Język bez kraju (pl)
     * 3. Domyślny plik (exceptions.properties)
     * 4. Fallback DEFAULT_DESCRIPTION
     */
    private final MessageSource messageSource;

    private static final String DEFAULT_DESCRIPTION = "Unknown error";

    public RestExceptionResponseBuilder(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Buduje odpowiedź błędu z prostym opisem.
     * Używane gdy nie potrzebujemy i18n lub mamy już zlokalizowany komunikat.
     * 
     * ResponseEntity.status() - fluent API do budowania odpowiedzi:
     * - Ustawia kod statusu HTTP
     * - body() dodaje treść odpowiedzi
     * - Automatyczna serializacja ExceptionResponse do JSON
     */
    public ResponseEntity<ExceptionResponse> build(final String description, final HttpStatus status) {
        return ResponseEntity.status(status).body(new ExceptionResponse(description));
    }

    /**
     * Buduje odpowiedź błędu z lokalizowanym komunikatem.
     * 
     * Deleguje do getLocalizedMessage() aby pobrać komunikat,
     * następnie używa prostszej wersji build().
     * 
     * Przykład użycia:
     * build(new CardNotFoundException(), NOT_FOUND, Locale.ENGLISH)
     * 
     * Spring automatycznie wstrzykuje Locale na podstawie:
     * - Accept-Language header
     * - Sesji użytkownika
     * - Domyślnego locale aplikacji
     */
    public ResponseEntity<ExceptionResponse> build(final Exception exception, final HttpStatus status, final Locale locale) {
        return build(getLocalizedMessage(exception, locale), status);
    }

    /**
     * Pobiera zlokalizowany komunikat dla wyjątku.
     * 
     * MessageSource.getMessage() parametry:
     * 1. code - klucz komunikatu (tu: nazwa klasy wyjątku)
     * 2. args - parametry do interpolacji w komunikacie {0}, {1}
     * 3. defaultMessage - gdy nie znaleziono komunikatu
     * 4. locale - żądany język
     * 
     * Przykład:
     * - Wyjątek: CardNotFoundException
     * - Szuka klucza: "CardNotFoundException" w exceptions_pl.properties
     * - Znajduje: "Karta o numerze {0} nie została znaleziona"
     * - Z parametrem "1234" zwraca: "Karta o numerze 1234 nie została znaleziona"
     * 
     * Varargs params (String...) - elastyczna liczba parametrów
     */
    public String getLocalizedMessage(final Exception exception, final Locale locale, final String... params) {
        return messageSource.getMessage(exception.getClass().getSimpleName(), params, DEFAULT_DESCRIPTION, locale);
    }

}
