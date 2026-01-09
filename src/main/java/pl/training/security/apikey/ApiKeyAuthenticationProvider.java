package pl.training.security.apikey;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

// @Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private static final GrantedAuthority DEFAULT_ROLE = new SimpleGrantedAuthority("ROLE_ADMIN");

    private final Set<String> validKeys;

    public ApiKeyAuthenticationProvider(Set<String> validKeys) {
        this.validKeys = validKeys;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof ApiKeyAuthentication apiKeyAuthentication) {
            var apiKey = apiKeyAuthentication.getApiKey();
            if (!validKeys.contains(apiKey)) {
                throw new BadCredentialsException("Invalid ApiKey");
            }
            return new ApiKeyAuthentication(Set.of(DEFAULT_ROLE), apiKey);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthentication.class.isAssignableFrom(authentication);
    }

}
