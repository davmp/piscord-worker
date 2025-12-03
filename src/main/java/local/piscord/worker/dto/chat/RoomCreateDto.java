package local.piscord.worker.dto.chat;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RoomCreateDto(
    String name,
    String description,
    String picture,
    String type,
    List<String> members,
    List<String> admins,
    String ownerId,
    Integer maxMembers,
    Boolean isActive,
    String directKey,
    String createdAt,
    String updatedAt) {
}
