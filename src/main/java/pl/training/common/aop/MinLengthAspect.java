package pl.training.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static pl.training.common.aop.AopHelpers.applyArgumentOperator;

/**
 * Aspekt Spring AOP do walidacji długości parametrów String oznaczonych @MinLength.
 * Implementuje walidację na poziomie parametrów metod.
 * 
 * @Aspect - deklaruje klasę jako aspekt AOP
 * @Component - rejestruje aspekt w kontenerze Spring
 */
@Aspect
@Component
public class MinLengthAspect {

    /**
     * @Before - typ porady wykonującej się PRZED wywołaniem metody docelowej.
     * Jeśli porada zgłosi wyjątek, metoda docelowa nie zostanie wykonana.
     * 
     * Wyrażenie pointcut "execution(* *(@pl.training.common.aop.MinLength (*)))":
     * Złożona składnia AspectJ wyjaśniona:
     * 
     * - "execution" - dopasowuje punkty wykonania metod
     * - Pierwsza "*" - dowolny typ zwracany
     * - Druga "*" - dowolna nazwa metody w dowolnej klasie
     * - "(@pl.training.common.aop.MinLength (*))" - metoda musi mieć parametr:
     *   - oznaczony adnotacją @MinLength
     *   - typ parametru to "*" (dowolny, ale w praktyce String)
     * 
     * Przykład dopasowania:
     * public void saveUser(@MinLength(5) String username) - TAK
     * public void saveUser(String username) - NIE (brak @MinLength)
     * public void saveUser(@MinLength(5) Integer id) - TAK (ale walidacja się nie powiedzie)
     * 
     * JoinPoint - w przeciwieństwie do ProceedingJoinPoint używanego w @Around,
     * ten typ nie pozwala kontrolować wykonania metody (tylko odczyt informacji).
     */
    @Before("execution(* *(@pl.training.common.aop.MinLength (*)))")
    public void validate(JoinPoint joinPoint) throws NoSuchMethodException {
        /**
         * applyArgumentOperator - pomocnicza metoda z AopHelpers, która:
         * 1. Znajduje parametry oznaczone @MinLength
         * 2. Pobiera wartości tych parametrów
         * 3. Aplikuje podaną funkcję walidacyjną
         * 
         * Lambda (String argument, MinLength minLength) wykonuje:
         * - Sprawdzenie długości stringa
         * - Rzucenie wyjątku jeśli za krótki
         * - Wyjątek przerwie wykonanie i zostanie propagowany do wywołującego
         * 
         * Ten mechanizm jest alternatywą dla:
         * - Bean Validation (@Size, @NotBlank)
         * - Ręcznej walidacji w każdej metodzie
         * - Spring @Validated na parametrach metod
         */
        applyArgumentOperator(joinPoint, MinLength.class, (String argument, MinLength minLength) -> {
            if (argument.length() < minLength.value()) {
                throw new IllegalArgumentException("Value is too short, minimum length is: " + minLength.value());
            }
        });
    }

}
