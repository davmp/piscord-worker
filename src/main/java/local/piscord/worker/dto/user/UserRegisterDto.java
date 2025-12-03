package local.piscord.worker.dto.user;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserRegisterDto(
                String username,
                String password,
                String picture,
                String bio,
                String createdAt,
                String updatedAt) {
}
