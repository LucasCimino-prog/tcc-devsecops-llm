package tcc.api.service;

import tcc.api.dto.LoginRequest;
import tcc.api.dto.LoginResponse;
import tcc.api.dto.PerfilResponse;
import tcc.api.dto.RegistroRequest;
import tcc.api.entity.Usuario;
import tcc.api.repository.UsuarioRepository;
import tcc.api.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository repository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public PerfilResponse registrar(RegistroRequest req) {
        String email = normalizar(req.email());

        if (repository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        Usuario usuario = new Usuario(
                req.nome().trim(),
                email,
                passwordEncoder.encode(req.senha())
        );
        return PerfilResponse.de(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        // Mensagem genérica: não revela se o email existe ou se a senha é que está errada
        Usuario usuario = repository.findByEmail(normalizar(req.email()))
                .filter(u -> passwordEncoder.matches(req.senha(), u.getSenha()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Email ou senha inválidos"));

        return new LoginResponse(
                jwtService.gerarToken(usuario),
                "Bearer",
                jwtService.getExpiracaoMs()
        );
    }

    @Transactional(readOnly = true)
    public PerfilResponse buscarPerfil(String email) {
        return repository.findByEmail(email)
                .map(PerfilResponse::de)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase();
    }
}
