package tcc.api.secureapi.repository;

import tcc.api.secureapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** Apenas métodos derivados (queries parametrizadas): sem concatenação de SQL/JPQL. */
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
