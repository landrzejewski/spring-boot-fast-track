package pl.training.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

public class JwtAuthentication implements Authentication {

    private final String token;
    private JwtPrincipal principal;

    public JwtAuthentication(JwtPrincipal principal, String token) {
        this.principal = principal;
        this.token = token;
    }

    public JwtAuthentication(String token) {
        this.token = token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal.roles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public Object getDetails() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return principal != null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw  new UnsupportedOperationException("Not supported");
    }

    @Override
    public String getName() {
        return "JwtAuthentication";
    }

}
