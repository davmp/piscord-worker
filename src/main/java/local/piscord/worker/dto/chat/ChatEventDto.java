package local.piscord.worker.dto.chat;

import com.fasterxml.jackson.databind.JsonNode;

import local.piscord.worker.enums.events.ChatEventType;

public record ChatEventDto(ChatEventType type, JsonNode payload) {
}
