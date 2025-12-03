package local.piscord.worker.dto.notification;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record NotificationDeleteDto(String id, String userId) {
}
