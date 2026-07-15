package pl.training.security;

import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequestMapping("api/users/me")
@RestController
public class UserRestController {

    @GetMapping
    public Object getCurrentUser(Authentication authentication, Principal principal) {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
