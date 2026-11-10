package tcc.api.secureapi.controller;

import tcc.api.secureapi.dto.UserProfileResponse;
import tcc.api.secureapi.security.AuthenticatedUser;
import tcc.api.secureapi.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** Rota principal: sem id na URL, logo não existe vetor de BOLA. */
    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return userService.getOwnProfile(principal);
    }

    /** O UUID é validado pelo Spring (400 se malformado) e a posse é verificada no serviço. */
    @GetMapping("/{id}")
    public UserProfileResponse byId(@PathVariable UUID id,
                                    @AuthenticationPrincipal AuthenticatedUser principal) {
        return userService.getProfileById(id, principal);
    }
}
