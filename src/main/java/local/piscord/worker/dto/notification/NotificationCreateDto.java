package local.piscord.worker.dto.notification;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record NotificationCreateDto(
                String userId,
                String type,
                Boolean isRead,
                String title,
                String body,
                String picture,
                String actionUrl,
                String createdAt) {
}
