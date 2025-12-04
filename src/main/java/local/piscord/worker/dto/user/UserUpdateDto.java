package local.piscord.worker.dto.user;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;

@RegisterForReflection
public record UserUpdateDto(
    @NotBlank(message = "User ID is required") String id,
    String username,
    String password,
    String picture,
    String bio) {
}
