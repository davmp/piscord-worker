package local.piscord.worker.dto;

import local.piscord.worker.enums.MessageType;

public record MessageDto(
    String id,
    String roomId,
    String userId,
    String content,
    MessageType type,
    String fileUrl,
    String replyTo,
    Boolean isEdited,
    Boolean isDeleted,
    Long createdAt,
    Long updatedAt
) {
}
