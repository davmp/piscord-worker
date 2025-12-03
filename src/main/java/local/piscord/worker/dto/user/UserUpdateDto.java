package local.piscord.worker.dto.user;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserUpdateDto(
                String id,
                String username,
                String password,
                String picture,
                String bio,
                String updatedAt) {
}
