package tcc.api.secureapi.security;

import tcc.api.secureapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Usado no login (por e-mail). */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .map(AuthenticatedUser::from)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    /** Usado pelo filtro JWT: recarrega papéis/estado do banco a cada requisição (revogação de papel/conta é imediata). */
    @Transactional(readOnly = true)
    public Optional<AuthenticatedUser> loadUserById(UUID id) {
        return userRepository.findById(id).map(AuthenticatedUser::from);
    }
}
