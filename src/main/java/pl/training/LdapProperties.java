package pl.training;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.ldap")
public record LdapProperties(
        String url,
        String base,
        String managerDn,
        String managerPassword) {

    public String providerUrl() {
        return url + "/" + base;
    }
}