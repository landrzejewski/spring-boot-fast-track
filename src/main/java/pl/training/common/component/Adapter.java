package pl.training.common.component;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Niestandardowy stereotyp Spring oznaczający adapter w architekturze heksagonalnej.
 * Rozszerza @Component, więc klasy nim oznaczone są automatycznie wykrywane przez Spring.
 * 
 * Stereotypy w Spring to adnotacje meta-oznaczone @Component:
 * - @Service - logika biznesowa
 * - @Repository - warstwa dostępu do danych
 * - @Controller - kontrolery MVC
 * - @RestController - kontrolery REST
 * - @Configuration - klasy konfiguracyjne
 * 
 * Tworzenie własnych stereotypów daje:
 * - Semantyczne znaczenie w kodzie (Adapter vs Component)
 * - Możliwość dodania wspólnej konfiguracji
 * - Łatwiejsze wyszukiwanie/filtrowanie w IDE
 * - Punkty zaczepienia dla AOP (@Pointcut("@within(Adapter)"))
 * 
 * @Component na tej adnotacji sprawia, że Spring:
 * 1. Skanuje klasy oznaczone @Adapter
 * 2. Tworzy z nich beany Spring
 * 3. Zarządza ich cyklem życia
 * 4. Wstrzykuje zależności
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Adapter {

    /**
     * @AliasFor - mechanizm Spring do tworzenia aliasów atrybutów.
     * 
     * Tu tworzy alias dla atrybutu 'value' z @Component, co oznacza:
     * - @Adapter("myAdapter") jest równoważne @Component("myAdapter")
     * - Nazwa beana może być określona jak w @Component
     * 
     * @AliasFor umożliwia też:
     * - Aliasy w obrębie tej samej adnotacji
     * - Nadpisywanie domyślnych wartości
     * - Kompozycję adnotacji z dziedziczeniem atrybutów
     * 
     * Przykład użycia:
     * @Adapter("cardRepositoryAdapter")
     * class JpaCardRepositoryAdapter { ... }
     * 
     * Bean będzie zarejestrowany pod nazwą "cardRepositoryAdapter"
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}
