package tcc.api.secureapi.controller;

import tcc.api.secureapi.dto.UserProfileResponse;
import tcc.api.secureapi.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Exemplo de RBAC: regra na URL (SecurityConfig) + regra no método (defesa em profundidade). */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserProfileResponse> listUsers() {
        return userService.listAll();
    }
}
