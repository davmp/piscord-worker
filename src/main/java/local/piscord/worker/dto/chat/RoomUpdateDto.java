package local.piscord.worker.dto.chat;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RoomUpdateDto(
        String id,
        String userId,

        String name,
        String description,
        String picture,
        String type,
        String ownerId,
        List<String> removeMembers,
        List<String> addMembers,
        Integer maxMembers,
        String updatedAt) {
}
