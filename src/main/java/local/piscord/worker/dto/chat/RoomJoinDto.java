package local.piscord.worker.dto.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RoomJoinDto(String id, String userId) {
}
