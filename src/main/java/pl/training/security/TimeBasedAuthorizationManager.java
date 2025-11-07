package pl.training.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class TimeBasedAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        var authentication = authenticationSupplier.get();
        // var request = context.getRequest();
        var hasRole = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        var workHours = LocalDateTime.now().getHour() > 8 && LocalDateTime.now().getHour() < 16;
        return new AuthorizationDecision(hasRole && workHours);
    }

}
