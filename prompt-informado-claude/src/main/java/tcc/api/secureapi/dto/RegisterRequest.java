package tcc.api.secureapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Sem campo "role": o papel nunca vem do cliente (evita mass assignment / escalonamento de privilégio).
 * Senha restrita a ASCII imprimível (sem espaços) de 12 a 72 caracteres, pois o BCrypt só considera 72 bytes.
 */
public record RegisterRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
        @Pattern(regexp = "^[\\p{L}][\\p{L} .'\\-]*$", message = "O nome contém caracteres inválidos")
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 254, message = "E-mail muito longo")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9])[\\x21-\\x7E]{12,72}$",
            message = "A senha deve ter 12 a 72 caracteres ASCII, com maiúscula, minúscula, número e símbolo, sem espaços")
        String password
) {}
