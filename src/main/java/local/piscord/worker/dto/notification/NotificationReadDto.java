package local.piscord.worker.dto.notification;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;

@RegisterForReflection
public record NotificationReadDto(
    @NotBlank(message = "Notification ID is required") String id,
    @NotBlank(message = "User ID is required") String userId) {
}
