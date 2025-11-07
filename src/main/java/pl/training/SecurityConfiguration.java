package pl.training;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import pl.training.security.KeycloakAuthoritiesConverter;
import pl.training.security.KeycloakAuthoritiesMapper;
import pl.training.security.KeycloakLogoutHandler;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;


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
                .oauth2ResourceServer(config -> config.jwt(withDefaults()))
                .oauth2Login(config -> config.userInfoEndpoint(this::userInfoCustomizer))
                .authorizeHttpRequests(config -> config
                                .requestMatchers(GET, "/actuator").permitAll()
                                .anyRequest().hasRole("ADMIN") //.authenticated()
                                // .anyRequest().access(new TimeBasedAuthorizationManager())
                )
                .logout(config -> config
                        .logoutRequestMatcher(requestMatcherBuilder().matcher("/logout.html"))
                        .logoutSuccessUrl("/login.html")
                        .invalidateHttpSession(true)
                        .addLogoutHandler(new KeycloakLogoutHandler(new RestTemplate()))
                )
                .build();
    }

    @Bean
    PathPatternRequestMatcher.Builder requestMatcherBuilder() {
        return PathPatternRequestMatcher.withDefaults();
    }

    @Bean
    public JwtAuthenticationConverter jwtConfigurer() {
        var jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakAuthoritiesConverter());
        return jwtConverter;
    }


    // Client scopes -> Client scope details (roles) -> Mapper details -> Add to userinfo enabled (Keycloak Admin console)
    private void userInfoCustomizer(OAuth2LoginConfigurer<HttpSecurity>.UserInfoEndpointConfig userInfoEndpointConfig) {
        userInfoEndpointConfig.userAuthoritiesMapper(new KeycloakAuthoritiesMapper());
    }

}
