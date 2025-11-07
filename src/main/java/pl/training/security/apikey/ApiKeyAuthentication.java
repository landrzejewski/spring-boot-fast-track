package pl.training.security.apikey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.training.security.jwt.JwtPrincipal;

import java.util.Collection;
import java.util.Collections;

public class ApiKeyAuthentication implements Authentication {

    private final String apiKey;
    private Collection<? extends GrantedAuthority> authorities;

    public ApiKeyAuthentication(Collection<? extends GrantedAuthority> authorities, String apiKey) {
        this.authorities = authorities;
        this.apiKey = apiKey;
    }

    public ApiKeyAuthentication(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public Object getDetails() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }

    @Override
    public boolean isAuthenticated() {
        return authorities != null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw  new UnsupportedOperationException("Not supported");
    }

    @Override
    public String getName() {
        return "apikey";
    }

}
