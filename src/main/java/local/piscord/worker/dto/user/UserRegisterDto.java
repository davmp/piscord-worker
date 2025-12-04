package local.piscord.worker.dto.user;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;

@RegisterForReflection
public record UserRegisterDto(
    @NotBlank(message = "Username is required") String username,
    @NotBlank(message = "Password is required") String password,
    String picture,
    String bio) {
}
