package tcc.api.service;

import tcc.api.dto.UserDtos.*;
import tcc.api.model.User;
import tcc.api.repository.UserRepository;
import tcc.api.security.CustomUserDetails;
import tcc.api.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public UserResponseDto register(RegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // Hash forte BCrypt
                .role(User.Role.ROLE_USER) // RBAC Padrão para novos cadastros
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(savedUser);
    }

    public String login(LoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        return tokenProvider.generateToken(authentication);
    }

    public UserResponseDto getProfile(Long id, CustomUserDetails authenticatedUser) {
        // Diretriz 5: Defesa Arquitetural contra BOLA (OWASP API1:2023)
        // Garante que o usuário autenticado só pode consultar o próprio perfil (a menos que seja ADMIN)
        boolean isAdmin = authenticatedUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !authenticatedUser.getId().equals(id)) {
            throw new SecurityException("Acesso Negado: Você não possui autorização para acessar os dados deste recurso.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        return UserResponseDto.fromEntity(user);
    }
}