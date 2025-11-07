package pl.training.security.user;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecrityInintializer implements ApplicationRunner {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SecrityInintializer(JpaUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.findByUsername("jan").isEmpty()) {
            var user = new UserEntity();
            user.setUsername("jan");
            user.setPassword(passwordEncoder.encode("123"));
            user.setEnabled(true);
            user.setVerified(true);
            user.setRoles("ROLE_ADMIN,ROLE_USER");
            userRepository.save(user);
        }
    }

}
