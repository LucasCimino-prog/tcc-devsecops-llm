package tcc.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class UserDtos {

    @Data
    public static class RegisterRequestDto {
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        private String name;

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        private String email;

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        private String password;
    }

    @Data
    public static class LoginRequestDto {
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        private String email;

        @NotBlank(message = "A senha é obrigatória.")
        private String password;
    }

    @Data
    public static class UserResponseDto {
        private Long id;
        private String name;
        private String email;
        private String role;

        public static UserResponseDto fromEntity(tcc.api.model.User user) {
            UserResponseDto dto = new UserResponseDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole().name());
            return dto;
        }
    }
}