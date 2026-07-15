package pl.training.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.training.security.jwt.JwtPrincipal;
import pl.training.security.jwt.JwtService;

import java.util.Set;

@Component
public class SecurityInitializer implements ApplicationRunner {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public SecurityInitializer(JpaUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.findByName("jan").isEmpty()) {
            var user = new UserEntity();
            user.setName("jan");
            user.setPassword(passwordEncoder.encode("123"));
            user.setEnabled(true);
            user.setVerified(true);
            user.setRoles("ROLE_ADMIN");
            userRepository.save(user);
        }
        var jwtPrincipal = new JwtPrincipal("jan", Set.of("ROLE_ADMIN"));
        var token = jwtService.createToken(jwtPrincipal);
        System.out.println(token);
    }

}
