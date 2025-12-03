package local.piscord.worker.dto.notification;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.runtime.annotations.RegisterForReflection;
import local.piscord.worker.enums.events.NotificationEventType;

@RegisterForReflection
public record NotificationEventDto(NotificationEventType type, JsonNode payload) {
}
