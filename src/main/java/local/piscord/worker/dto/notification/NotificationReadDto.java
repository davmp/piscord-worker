package local.piscord.worker.dto.notification;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record NotificationReadDto(String id, String userId) {
}
