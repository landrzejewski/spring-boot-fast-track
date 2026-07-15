package pl.training;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.PersonContextMapper;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    BaseLdapPathContextSource contextSource(LdapProperties properties) {
        var contextSource = new DefaultSpringSecurityContextSource(properties.providerUrl());
        contextSource.setUserDn(properties.managerDn());
        contextSource.setPassword(properties.managerPassword());
        return contextSource;
    }

    @Bean
    LdapAuthoritiesPopulator authoritiesPopulator(BaseLdapPathContextSource contextSource) {
        // "ou=groups" jest relatywne do base DN z context source'u
        var populator = new DefaultLdapAuthoritiesPopulator(contextSource, "ou=groups");
        populator.setGroupSearchFilter("(member={0})");  // {0} = DN użytkownika, {1} = username
        populator.setGroupRoleAttribute("cn");
        populator.setRolePrefix("ROLE_");
        populator.setConvertToUpperCase(true);
        populator.setSearchSubtree(true);
        return populator;
    }

    @Bean
    AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource,
                                                LdapAuthoritiesPopulator authoritiesPopulator) {
        var factory = new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserDnPatterns("uid={0},ou=people");
        // alternatywa zamiast wzorca DN:
        // factory.setUserSearchBase("ou=people");
        // factory.setUserSearchFilter("(uid={0})");
        factory.setUserDetailsContextMapper(new PersonContextMapper());
        factory.setLdapAuthoritiesPopulator(authoritiesPopulator);
        return factory.createAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/public").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMINS")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())  // demo/API
                .build();
    }
}