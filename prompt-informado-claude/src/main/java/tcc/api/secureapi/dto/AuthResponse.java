package tcc.api.secureapi.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds) {}
