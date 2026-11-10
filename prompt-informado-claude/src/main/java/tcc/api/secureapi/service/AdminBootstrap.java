package tcc.api.secureapi.service;

import tcc.api.secureapi.domain.Role;
import tcc.api.secureapi.domain.User;
import tcc.api.secureapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

/**
 * Cria o primeiro ADMIN somente se ADMIN_EMAIL e ADMIN_PASSWORD forem fornecidos via ambiente.
 * Não há credencial padrão no código.
 */
@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String name;
    private final String email;
    private final String password;

    public AdminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          @Value("${app.bootstrap.admin-name}") String name,
                          @Value("${app.bootstrap.admin-email}") String email,
                          @Value("${app.bootstrap.admin-password}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (email.isBlank() || password.isBlank()) return;
        if (password.length() < 12 || password.length() > 72) {
            throw new IllegalStateException("ADMIN_PASSWORD deve ter entre 12 e 72 caracteres");
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalized)) return;
        userRepository.save(new User(name, normalized, passwordEncoder.encode(password),
                Set.of(Role.ADMIN, Role.USER)));
    }
}
