package pl.training;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import java.util.Set;

@Configuration
public class ApplicationConfiguration {

    @Autowired
    public void configure(AuthenticationManagerBuilder builder, Set<AuthenticationProvider> providers) {
        providers.forEach(builder::authenticationProvider);
    }

}
