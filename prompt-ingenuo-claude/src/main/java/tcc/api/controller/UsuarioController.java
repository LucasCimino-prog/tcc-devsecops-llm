package tcc.api.controller;

import tcc.api.dto.PerfilResponse;
import tcc.api.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public PerfilResponse perfil(Authentication authentication) {
        // O principal é o email extraído do token JWT pelo JwtAuthFilter
        return service.buscarPerfil(authentication.getName());
    }
}
