package local.piscord.worker.dto.chat;

import java.util.List;

public record RoomUpdateDto(
    String id,
    String userId,

    String name,
    String description,
    String picture,
    String type,
    String owner,
    List<String> removeMembers,
    List<String> addMembers,
    Integer maxMembers,
    Long updatedAt) {
}
