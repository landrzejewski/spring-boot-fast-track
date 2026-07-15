package pl.training;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class DemoController {

    @GetMapping("/public")
    String publicEndpoint() {
        return "public";
    }

    @GetMapping("/me")
    Map<String, Object> me(@AuthenticationPrincipal LdapUserDetails user) {
        return Map.of(
                "username", user.getUsername(),
                "dn", user.getDn(),
                "authorities", user.getAuthorities());
    }

    @GetMapping("/admin/panel")
    String adminPanel() {
        return "tylko dla ROLE_ADMINS";
    }

    @GetMapping("/developers")
    @PreAuthorize("hasRole('DEVELOPERS')")
    String developers() {
        return "tylko dla ROLE_DEVELOPERS";
    }
}