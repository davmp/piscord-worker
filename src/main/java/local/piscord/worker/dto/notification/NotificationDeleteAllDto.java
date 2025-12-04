package local.piscord.worker.dto.notification;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;

@RegisterForReflection
public record NotificationDeleteAllDto(
    @NotBlank(message = "User ID is required") String userId) {
}
