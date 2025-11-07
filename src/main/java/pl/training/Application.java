package pl.training;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Główna klasa aplikacji Spring Boot.
 * 
 * @SpringBootApplication to meta-adnotacja, która łączy w sobie trzy kluczowe adnotacje:
 * 
 * 1. @Configuration - oznacza klasę jako źródło definicji beanów Spring
 * 2. @EnableAutoConfiguration - włącza automatyczną konfigurację Spring Boot
 *    na podstawie zależności znalezionych w classpath (np. jeśli spring-boot-starter-web
 *    jest w zależnościach, automatycznie konfiguruje Tomcat i Spring MVC)
 * 3. @ComponentScan - włącza skanowanie komponentów począwszy od pakietu,
 *    w którym znajduje się ta klasa (pl.training) i wszystkich podpakietów.
 *    Spring automatycznie znajdzie i zarejestruje klasy oznaczone jako:
 *    - @Component (ogólny komponent Spring)
 *    - @Service (warstwa serwisów/logiki biznesowej)
 *    - @Repository (warstwa dostępu do danych)
 *    - @Controller/@RestController (warstwa kontrolerów HTTP)
 *    - @Configuration (klasy konfiguracyjne)
 *    - oraz wszystkie inne adnotacje rozszerzające @Component
 * 
 * Dodatkowo @SpringBootApplication umożliwia:
 * - Wykluczenie konkretnych klas z automatycznej konfiguracji poprzez parametr exclude
 * - Określenie bazowych pakietów do skanowania poprzez scanBasePackages
 * - Określenie konkretnych klas bazowych poprzez scanBasePackageClasses
 */
@SpringBootApplication
public class Application {

    /**
     * Punkt wejścia aplikacji Spring Boot.
     * 
     * SpringApplication.run() wykonuje następujące kroki:
     * 1. Tworzy kontekst aplikacji Spring (ApplicationContext)
     * 2. Rejestruje shutdown hook do poprawnego zamknięcia aplikacji
     * 3. Ładuje wszystkie beany zdefiniowane w aplikacji
     * 4. Uruchamia wszystkie CommandLineRunner i ApplicationRunner beany
     * 5. Uruchamia wbudowany serwer aplikacyjny (domyślnie Tomcat)
     * 6. Nasłuchuje na porcie określonym w application.yml (domyślnie 8080)
     * 
     * @param args argumenty wiersza poleceń przekazywane do aplikacji
     */
    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
