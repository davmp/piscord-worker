package local.piscord.worker.dto.chat;

import java.util.List;

public record RoomDto(
    String id,
    String name,
    String description,
    String picture,
    String type,
    List<String> members,
    List<String> admins,
    String ownerId,
    int maxMembers,
    boolean isActive,
    String directKey,
    long createdAt,
    long updatedAt) {
}
