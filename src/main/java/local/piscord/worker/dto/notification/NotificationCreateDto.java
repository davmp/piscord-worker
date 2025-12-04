package local.piscord.worker.dto.notification;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;

@RegisterForReflection
public record NotificationCreateDto(
    @NotBlank(message = "User ID is required") String userId,

    @NotBlank(message = "Type is required") String type,

    @NotBlank(message = "Title is required") String title,

    @NotBlank(message = "Body is required") String body,

    String picture,

    String actionUrl,

    String createdAt) {
}
