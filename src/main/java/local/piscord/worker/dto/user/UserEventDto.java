package local.piscord.worker.dto.user;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.runtime.annotations.RegisterForReflection;
import local.piscord.worker.enums.events.UserEventType;

@RegisterForReflection
public record UserEventDto(UserEventType type, JsonNode payload) {
}
