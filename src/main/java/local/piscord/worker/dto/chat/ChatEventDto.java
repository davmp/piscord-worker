package local.piscord.worker.dto.chat;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.runtime.annotations.RegisterForReflection;
import local.piscord.worker.enums.events.ChatEventType;

@RegisterForReflection
public record ChatEventDto(ChatEventType type, JsonNode payload) {
}
