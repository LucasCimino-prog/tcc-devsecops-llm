package tcc.api.dto;

public record LoginResponse(String token, String tipo, long expiraEmMs) {}
