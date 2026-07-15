package pl.training.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityInitializer implements ApplicationRunner {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityInitializer(JpaUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    }

}
