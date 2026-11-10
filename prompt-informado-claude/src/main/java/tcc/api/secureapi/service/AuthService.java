package tcc.api.secureapi.service;

import tcc.api.secureapi.domain.Role;
import tcc.api.secureapi.domain.User;
import tcc.api.secureapi.dto.AuthResponse;
import tcc.api.secureapi.dto.LoginRequest;
import tcc.api.secureapi.dto.RegisterRequest;
import tcc.api.secureapi.dto.UserProfileResponse;
import tcc.api.secureapi.exception.EmailAlreadyUsedException;
import tcc.api.secureapi.repository.UserRepository;
import tcc.api.secureapi.security.AuthenticatedUser;
import tcc.api.secureapi.security.JwtService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserProfileResponse register(RegisterRequest req) {
        String email = normalize(req.email());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException();
        }
        // Papel SEMPRE definido no servidor: auto-cadastro só gera USER.
        User user = new User(req.name().trim(), email, passwordEncoder.encode(req.password()), Set.of(Role.USER));
        try {
            return UserProfileResponse.from(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException e) { // corrida: unique constraint em e-mail
            throw new EmailAlreadyUsedException();
        }
    }

    public AuthResponse login(LoginRequest req) {
        // Lança AuthenticationException (tratada como 401 genérico) para qualquer falha de credencial.
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalize(req.email()), req.password()));
        AuthenticatedUser principal = (AuthenticatedUser) auth.getPrincipal();
        return new AuthResponse(jwtService.generateToken(principal), "Bearer", jwtService.expirationSeconds());
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
