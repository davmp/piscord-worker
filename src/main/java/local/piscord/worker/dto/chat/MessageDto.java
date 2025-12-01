package local.piscord.worker.dto.chat;

public record MessageDto(
        String id,
        String roomId,
        String userId,
        String content,
        String type,
        String fileUrl,
        String replyTo,
        Boolean isEdited,
        Boolean isDeleted,
        Long createdAt,
        Long updatedAt) {
}
