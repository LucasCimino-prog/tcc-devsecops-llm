package tcc.api.controller;

import tcc.api.dto.UserDtos.*;
import tcc.api.security.CustomUserDetails;
import tcc.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Rota de Cadastro Pública com Bean Validation (@Valid)
    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto dto) {
        UserResponseDto response = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Rota de Login Pública
    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDto dto) {
        String token = userService.login(dto);
        return ResponseEntity.ok(token);
    }

    // Rota Protegida de Consulta de Perfil mitigando BOLA e validando RBAC
    @GetMapping("/users/{id}/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Diretriz 3: RBAC aplicado
    public ResponseEntity<UserResponseDto> getProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails authenticatedUser) {

        UserResponseDto profile = userService.getProfile(id, authenticatedUser);
        return ResponseEntity.ok(profile);
    }
}