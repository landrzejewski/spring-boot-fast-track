package pl.training;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import pl.training.security.jwt.JwtAuthenticationFilter;
import pl.training.security.jwt.JwtAuthenticationProvider;
import pl.training.security.jwt.JwtPrincipal;
import pl.training.security.jwt.JwtService;

import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;


/*AuthenticationManager authenticationManager; // Interfejs/kontrakt dla procesu uwierzytelnienia użytkownika
        ProviderManager providerManager; // Podstawowa implementacja AuthenticationManager, deleguje proces uwierzytelnienia do jednego z obiektów AuthenticationProvider
            AuthenticationProvider authenticationProvider; // Interfejs/kontrakt dla obiektów realizujących uwierzytelnianie z wykorzystaniem konkretnego mechanizmu/implementacji
                DaoAuthenticationProvider daoAuthenticationProvider; // Jedna z implementacji AuthenticationProvider, ładuje dane o użytkowniku wykorzystując UserDetailsService i porównuje je z tymi podanymi w czasie logowani
                    UserDetailsService userDetailsService; // Interfejs/kontrakt usługi ładującej dane dotyczące użytkownika

    UserDetailsManager userDetailsManager; Interfejs/kontrakt pochodny UserDetailsService, pozwalający na zarządzanie użytkownikami
        InMemoryUserDetailsManager inMemoryUserDetailsManager; // Jedna z implementacji UsersDetailsManager, przechowuje informacje w pamięci

    PasswordEncoder passwordEncoder; //Interfejs/kontrakt pozwalający na hashowanie i porównywanie haseł
        BCryptPasswordEncoder bCryptPasswordEncoder; //Jedna z implementacji PasswordEncoder

    SecurityContextHolder securityContextHolder; // Przechowuje/udostępnia SecurityContext
        SecurityContext securityContext; // Kontener przechowujący Authentication
            Authentication authentication; // Reprezentuje dane uwierzytelniające jak i uwierzytelnionego użytkownika/system
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken; // Jedna z implementacji Authentication, zawiera login i hasło jako credentials
                    UserDetails userDetails; // Interfejs/kontrakt opisujący użytkownika
                    GrantedAuthority grantedAuthority; // Interfejs/kontrakt opisujący role/uprawnienia
                        SimpleGrantedAuthority simpleGrantedAuthority; // Jedna z implementacji SimpleGrantedAuthority

    AuthorizationManager authorizationManager; // Interfejs/kontrakt dla procesu autoryzacji
        AuthoritiesAuthorizationManager authoritiesAuthorizationManager; // Jedna z implementacji AuthorizationManager (role)*/

@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true/*, proxyTargetClass = true*/)
@Configuration
public class SecurityConfiguration implements ApplicationRunner {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*public UserDetails user() {
        return User.withUsername("jan")
                .password(passwordEncoder().encode("123"))
                .roles("ADMIN")
                //.authorities("read", "write")
                .build();
    }*/

    /*@Bean
    public UserDetailsService userDetailsService() {
        return username ->
            if (!username.equals("jan")) {
                throw new UsernameNotFoundException("User not found");
            }
            return user();
        };
    }*/

   /* @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        return new InMemoryUserDetailsManager(user());
        *//*var manager = new JdbcUserDetailsManager(dataSource);
        // manager.setUsersByUsernameQuery("select username, password, enabled from users where username = ?");
        // manager.setAuthoritiesByUsernameQuery("select username, authority from authorities where username = ?");
        return manager;*//*
    }*/

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(config -> config.ignoringRequestMatchers("/api/**"))
                .cors(config -> config.configurationSource(request -> corsConfiguration()))
                .httpBasic(withDefaults())
                // .formLogin(withDefaults())
                .formLogin(config -> config
                                .loginPage("/login.html")
                                .defaultSuccessUrl("/")
                        //.usernameParameter("username")
                        //.passwordParameter("password")
                        /*.successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

                            }
                        })*/
                        /*.failureHandler((request, response, exception) -> {

                        })*/
                )
                .authorizeHttpRequests(config -> config
                        .requestMatchers("/login.html").permitAll()
                        .requestMatchers(GET, "/actuator").permitAll()
                        .anyRequest().hasRole("ADMIN") //.authenticated()
                        // .anyRequest().access(new TimeBasedAuthorizationManager())
                )
                .logout(config -> config
                        .logoutRequestMatcher(requestMatcherBuilder().matcher("/logout.html"))
                        .logoutSuccessUrl("/login.html")
                        .invalidateHttpSession(true)
                )
                .build();

    }

    @Bean
    PathPatternRequestMatcher.Builder requestMatcherBuilder() {
        return PathPatternRequestMatcher.withDefaults();
    }


    @Autowired
    private JwtService jwtService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var jwtPrincipal = new JwtPrincipal("jan", Set.of("ROLE_ADMIN"));
        var token = jwtService.createToken(jwtPrincipal);
        System.out.println("Token: " + token);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        var daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtService jwtService) {
        return new JwtAuthenticationProvider(jwtService);
    }

}
