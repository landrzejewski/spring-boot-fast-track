package pl.training;

import org.springframework.context.annotation.Configuration;

/**
 * Główna klasa konfiguracyjna aplikacji.
 * 
 * @Configuration - kluczowa adnotacja Spring Framework oznaczająca, że klasa jest źródłem
 * definicji beanów Spring. Ta adnotacja:
 * 
 * 1. Wskazuje, że klasa może zawierać metody oznaczone @Bean, które będą zarządzane
 *    przez kontener Spring IoC (Inversion of Control)
 * 
 * 2. Jest procesowana przez Spring podczas uruchamiania aplikacji, a wszystkie metody
 *    @Bean są wywoływane, a ich wyniki rejestrowane jako beany w kontekście aplikacji
 * 
 * 3. Umożliwia programatyczną konfigurację aplikacji jako alternatywę dla konfiguracji XML
 * 
 * 4. Może importować inne klasy konfiguracyjne używając @Import
 * 
 * 5. Może być warunkowa - używając @Conditional, @ConditionalOnProperty,
 *    @ConditionalOnBean, @ConditionalOnMissingBean itp.
 * 
 * 6. Jest automatycznie wykrywana przez @ComponentScan (który jest częścią @SpringBootApplication)
 * 
 * Przykładowe użycie:
 * @Bean
 * public DataSource dataSource() {
 *     return new HikariDataSource();
 * }
 * 
 * W tym projekcie klasa jest obecnie pusta, ale służy jako punkt rozszerzenia
 * dla przyszłych konfiguracji globalnych aplikacji.
 */
@Configuration
public class ApplicationConfiguration {
}
