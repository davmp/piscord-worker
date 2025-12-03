package local.piscord.worker.dto.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RoomLeaveDto(String id, String userId) {
}
