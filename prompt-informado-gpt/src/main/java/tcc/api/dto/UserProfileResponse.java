package tcc.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        String email,
        String role,
        LocalDateTime createdAt
) {}
