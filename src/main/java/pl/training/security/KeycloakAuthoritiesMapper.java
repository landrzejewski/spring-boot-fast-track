package pl.training.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private static final String REALM_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        var grantedAuthorities = new HashSet<String>();
        authorities.forEach(authority -> {
           /* if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                var userInfo = oidcUserAuthority.getUserInfo();
                var realmAccess = userInfo.getClaimAsMap(REALM_CLAIM);
                var roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
                grantedAuthorities.addAll(roles);
            } else */
            if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {
                var userAttributes = oauth2UserAuthority.getAttributes();
                var realmAccess = (Map<String, Object>) userAttributes.get(REALM_CLAIM);
                var roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
                grantedAuthorities.addAll(roles);
            } else {
                grantedAuthorities.add(authority.getAuthority());
            }
        });
        return grantedAuthorities.stream()
                .filter(role -> role.startsWith(ROLE_PREFIX))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
