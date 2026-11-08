package tcc.api.dto;

import tcc.api.entity.Usuario;

import java.time.LocalDateTime;

public record PerfilResponse(Long id, String nome, String email, LocalDateTime criadoEm) {

    public static PerfilResponse de(Usuario u) {
        return new PerfilResponse(u.getId(), u.getNome(), u.getEmail(), u.getCriadoEm());
    }
}
