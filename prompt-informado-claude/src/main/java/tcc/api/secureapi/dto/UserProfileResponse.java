package tcc.api.secureapi.dto;

import tcc.api.secureapi.domain.Role;
import tcc.api.secureapi.domain.User;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** DTO de saída: a entidade nunca é serializada diretamente (o hash da senha jamais sai da API). */
public record UserProfileResponse(UUID id, String name, String email, Set<String> roles, Instant createdAt) {

    public static UserProfileResponse from(User u) {
        return new UserProfileResponse(
                u.getId(), u.getName(), u.getEmail(),
                u.getRoles().stream().map(Role::name).collect(Collectors.toUnmodifiableSet()),
                u.getCreatedAt());
    }
}
