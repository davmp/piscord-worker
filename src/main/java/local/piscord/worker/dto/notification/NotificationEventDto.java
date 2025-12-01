package local.piscord.worker.dto.notification;

import com.fasterxml.jackson.databind.JsonNode;

import local.piscord.worker.enums.events.NotificationEventType;

public record NotificationEventDto(NotificationEventType type, JsonNode payload) {
}
