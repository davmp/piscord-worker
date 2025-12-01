package local.piscord.worker.dto.user;

import com.fasterxml.jackson.databind.JsonNode;

import local.piscord.worker.enums.events.UserEventType;

public record UserEventDto(UserEventType type, JsonNode payload) {
}
