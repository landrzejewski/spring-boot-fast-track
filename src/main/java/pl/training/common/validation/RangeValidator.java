package pl.training.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementacja niestandardowego walidatora dla adnotacji @Range.
 * Część mechanizmu Bean Validation (JSR-303/380) w Spring.
 * 
 * ConstraintValidator<A, T> - interfejs do tworzenia własnych walidatorów:
 * - A (Range) - typ adnotacji walidacyjnej
 * - T (Double) - typ walidowanej wartości
 * 
 * Spring automatycznie:
 * 1. Wykrywa klasy implementujące ConstraintValidator
 * 2. Rejestruje je w ValidationFactory
 * 3. Używa gdy napotka odpowiednią adnotację (@Range)
 * 4. Tworzy nową instancję walidatora dla każdego użycia
 * 
 * Walidatory są bezstanowe i thread-safe (nowa instancja per walidacja).
 */
public final class RangeValidator implements ConstraintValidator<Range, Double> {

    private double minValue;
    private double maxValue;

    /**
     * Metoda inicjalizująca wywoływana raz przed walidacją.
     * Pobiera parametry z adnotacji @Range.
     * 
     * Tu odczytujemy wartości minValue i maxValue zdefiniowane
     * w adnotacji na polu/parametrze, np.:
     * @Range(minValue = 0.0, maxValue = 100.0)
     * 
     * @param constraintAnnotation instancja adnotacji z wartościami
     */
    @Override
    public void initialize(final Range constraintAnnotation) {
        this.minValue = constraintAnnotation.minValue();
        this.maxValue = constraintAnnotation.maxValue();
    }

    /**
     * Główna logika walidacji wywoływana dla każdej wartości.
     * 
     * @param value wartość do walidacji (może być null!)
     * @param context kontekst walidacji umożliwiający:
     *                - Dostęp do walidowanego obiektu
     *                - Modyfikację komunikatu błędu
     *                - Wyłączenie domyślnego komunikatu
     *                - Dodanie własnych komunikatów z parametrami
     * 
     * @return true jeśli wartość jest poprawna, false w przeciwnym razie
     * 
     * UWAGA: Metoda powinna obsługiwać null gracefully.
     * Jeśli null jest niepoprawny, użyj @NotNull dodatkowo.
     * 
     * Spring/Hibernate Validator automatycznie:
     * - Wywołuje tę metodę podczas walidacji
     * - Zbiera błędy walidacji
     * - Rzuca MethodArgumentNotValidException dla @Valid/@Validated
     */
    @Override
    public boolean isValid(final Double value, final ConstraintValidatorContext context) {
        // null zazwyczaj pomijamy - to zadanie @NotNull
        if (value == null) {
            return true;
        }
        return value >= minValue && value <= maxValue;
    }

}
