package tcc.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 100)
        String name,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 254)
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(
                min = 12,
                max = 72,
                message = "A senha deve ter entre 12 e 72 caracteres"
        )
        String password

) {}
