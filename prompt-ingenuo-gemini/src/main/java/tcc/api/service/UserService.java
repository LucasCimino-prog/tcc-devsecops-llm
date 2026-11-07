package tcc.api.service;

import tcc.api.dto.LoginRequest;
import tcc.api.dto.RegisterRequest;
import tcc.api.dto.UserResponse;
import tcc.api.model.User;
import tcc.api.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        // Dica: Em produção, utilize BCryptPasswordEncoder para criptografar a senha
        User user = new User(request.getName(), request.getEmail(), request.getPassword());
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou senha inválida."));

        // Comparação simples de senha (em produção, utilize o encoder correspondente)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Usuário não encontrado ou senha inválida.");
        }

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public UserResponse getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}