package tcc.api.secureapi.service;

import tcc.api.secureapi.dto.UserProfileResponse;
import tcc.api.secureapi.exception.ResourceNotFoundException;
import tcc.api.secureapi.repository.UserRepository;
import tcc.api.secureapi.security.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Proteção BOLA por construção: o id consultado vem EXCLUSIVAMENTE do principal (token assinado).
     * Não há parâmetro de id controlado pelo cliente.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getOwnProfile(AuthenticatedUser principal) {
        return userRepository.findById(principal.getId())
                .map(UserProfileResponse::from)
                .orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * Rota /users/{id} (compatibilidade REST): a verificação de propriedade fica na camada de serviço,
     * antes de qualquer acesso ao banco. Divergência de id gera 404 (não revela se o recurso existe) e é auditada.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileById(UUID requestedId, AuthenticatedUser principal) {
        if (!principal.getId().equals(requestedId)) {
            log.warn("Possível BOLA: usuário {} tentou acessar o perfil {}", principal.getId(), requestedId);
            throw new ResourceNotFoundException();
        }
        return getOwnProfile(principal);
    }

    /** Uso exclusivo da rota administrativa (protegida por RBAC). */
    @Transactional(readOnly = true)
    public List<UserProfileResponse> listAll() {
        return userRepository.findAll().stream().map(UserProfileResponse::from).toList();
    }
}
