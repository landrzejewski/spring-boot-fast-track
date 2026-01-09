package pl.training.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@Component
public class TimeBasedAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authenticationSupplier, RequestAuthorizationContext object) {
        var authentication = authenticationSupplier.get();
        // var request = context.getRequest();
        var hasRole = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        var workHours = LocalDateTime.now().getHour() > 8 && LocalDateTime.now().getHour() < 16;
        return new AuthorizationDecision(hasRole && workHours);
    }

}
