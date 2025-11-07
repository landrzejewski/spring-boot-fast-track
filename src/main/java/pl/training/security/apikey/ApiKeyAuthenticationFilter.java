package pl.training.security.apikey;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.training.security.jwt.JwtAuthentication;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_PREFIX = "API_KEY ";

    private final AuthenticationConfiguration authenticationConfiguration;

    public ApiKeyAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(API_PREFIX)) {
            var apiKey = authorizationHeader.substring(API_PREFIX.length());
            var jwtAuthentication = new ApiKeyAuthentication(apiKey);
            try {
                var authentication = authenticationConfiguration.getAuthenticationManager()
                        .authenticate(jwtAuthentication);
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
            catch (AuthenticationException authenticationException) {
                response.setStatus(SC_UNAUTHORIZED);
            }
            catch (Exception exception) {
                throw new ServletException(exception);
            }
        }
        filterChain.doFilter(request, response);
    }

}
