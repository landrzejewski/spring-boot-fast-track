package pl.training.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;

@Service
public class JwtService {

    private static final String ISSUER = "https://localhost:8000";
    private static final String USER_CLAIM = "user";
    private static final String ROLES_CLAIM = "roles";
    private static final long EXPIRATION_PERIOD = 3600;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;


    public JwtService(@Value("${jwt.secret}") String secret) {
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
    }

    public String createToken(JwtPrincipal jwtPrincipal) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withClaim(USER_CLAIM, jwtPrincipal.username())
                .withClaim(ROLES_CLAIM, jwtPrincipal.roles().stream().toList())
                .withExpiresAt(Instant.now().plusSeconds(EXPIRATION_PERIOD))
                .sign(algorithm);
    }

    public Optional<JwtPrincipal> verify(String token) {
        try {
            var jwt = verifier.verify(token);
            var username = jwt.getClaim(USER_CLAIM).asString();
            var roles = jwt.getClaim(ROLES_CLAIM).asList(String.class);
            var principal = new JwtPrincipal(username, new HashSet<>(roles));
            return Optional.of(principal);
        } catch (JWTVerificationException jwtVerificationException) {
            return Optional.empty();
        }
    }

}
