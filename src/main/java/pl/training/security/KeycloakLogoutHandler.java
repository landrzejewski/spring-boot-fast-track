package pl.training.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class KeycloakLogoutHandler implements LogoutHandler {

    private static final String LOGOUT_ENDPOINT = "/protocol/openid-connect/logout";
    private static final String ID_TOKEN_HINT = "id_token_hint";

    private final RestTemplate restTemplate;

    public KeycloakLogoutHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var user = (OidcUser) authentication.getPrincipal();
        var endSessionEndpoint = user.getIssuer() + LOGOUT_ENDPOINT;
        var builder = UriComponentsBuilder
                .fromUriString(endSessionEndpoint)
                .queryParam(ID_TOKEN_HINT, user.getIdToken().getTokenValue());
        restTemplate.getForObject(builder.toUriString(), String.class);
    }

}
