package pl.training.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Niestandardowa adnotacja walidacyjna sprawdzająca zakres wartości.
 * Integruje się z Bean Validation API (JSR-303/380) w Spring.
 * 
 * @Constraint(validatedBy = RangeValidator.class) - kluczowa adnotacja:
 * - Oznacza tę adnotację jako constraint walidacyjny
 * - Wskazuje klasę walidatora implementującą logikę
 * - Spring/Hibernate Validator automatycznie używa wskazanego walidatora
 * 
 * @Target(ElementType.FIELD) - gdzie można użyć adnotacji:
 * - FIELD - na polach klasy
 * - METHOD - na metodach (getterach)
 * - PARAMETER - na parametrach metod
 * - TYPE - na klasach (dla walidacji cross-field)
 * - CONSTRUCTOR - na konstruktorach
 * - TYPE_USE - na użyciach typu (Java 8+)
 * 
 * @Retention(RetentionPolicy.RUNTIME) - adnotacja dostępna w runtime
 * Wymagane dla Bean Validation, która działa przez refleksję
 */
@Constraint(validatedBy = RangeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {

    /**
     * Komunikat błędu walidacji.
     * 
     * {invalidRange} - placeholder dla internacjonalizacji:
     * - Spring szuka klucza "invalidRange" w ValidationMessages.properties
     * - Może być zlokalizowany (ValidationMessages_pl.properties, etc.)
     * - Może zawierać parametry: {min}, {max}, {value}
     * 
     * Przykład w ValidationMessages.properties:
     * invalidRange=Wartość musi być między {min} a {max}
     * 
     * Można też podać bezpośrednio:
     * message() default "Value must be between {minValue} and {maxValue}"
     */
    String message() default "{invalidRange}";

    /**
     * Grupy walidacji - mechanizm warunkowej walidacji.
     * 
     * Pozwala na różne zestawy reguł w różnych kontekstach:
     * - Default.class - domyślna grupa
     * - Create.class - walidacja przy tworzeniu
     * - Update.class - walidacja przy aktualizacji
     * 
     * Użycie: @Validated(Create.class) w kontrolerze
     */
    Class<?>[] groups() default {};

    /**
     * Payload - metadane dla klientów walidacji.
     * 
     * Rzadko używane, pozwala dołączyć dodatkowe informacje:
     * - Poziom ważności (Error, Warning)
     * - Kategorie błędów
     * - Własne metadane
     * 
     * Głównie dla zaawansowanych frameworków i narzędzi.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Parametry specyficzne dla tego walidatora.
     * Wartości domyślne można nadpisać przy użyciu:
     * 
     * @Range(minValue = 0, maxValue = 100)
     * private Double percentage;
     */
    double minValue() default 100;

    double maxValue() default 10_000;

}
