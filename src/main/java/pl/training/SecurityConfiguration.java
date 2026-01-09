package pl.training;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Bean
    public CorsConfiguration corsConfiguration() {
        var corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("https://training.pl"));
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);
        return corsConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(config -> config.ignoringRequestMatchers("/api/**"))
                .cors(config -> config.configurationSource(request -> corsConfiguration()))
                .authorizeHttpRequests(config -> config
                        .anyRequest().authenticated()
                )
              /*  .logout(config -> config
                        .logoutRequestMatcher(requestMatcherBuilder().matcher("/logout.html"))
                        .logoutSuccessUrl("/login.html")
                        .invalidateHttpSession(true)
                )*/
                .build();
    }

    @Bean
    PathPatternRequestMatcher.Builder requestMatcherBuilder() {
        return PathPatternRequestMatcher.withDefaults();
    }

}
