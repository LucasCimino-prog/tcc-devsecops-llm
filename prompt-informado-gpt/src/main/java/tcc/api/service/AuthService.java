package tcc.api.service;

import tcc.api.dto.*;
import tcc.api.entity.Role;
import tcc.api.entity.User;
import tcc.api.repository.UserRepository;
import tcc.api.security.JwtService;
import tcc.api.security.UserPrincipal;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequest request) {

        String email = normalizeEmail(request.email());

        validatePasswordBytes(request.password());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Não foi possível realizar o cadastro com os dados informados."
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        User user = new User(
                request.name().trim(),
                email,
                encodedPassword,
                Role.USER
        );

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        validatePasswordBytes(request.password());

        String email = normalizeEmail(request.email());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpiration() / 1000
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UserPrincipal principal) {

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Usuário não encontrado."
                        )
                );

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validatePasswordBytes(String password) {

        int bytes = password.getBytes(
                StandardCharsets.UTF_8
        ).length;

        if (bytes > 72) {
            throw new IllegalArgumentException(
                    "A senha excede o limite permitido."
            );
        }
    }
}
