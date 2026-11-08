package tcc.api.dto;

public record AuthResponse(
        String token,
        String type
) {}