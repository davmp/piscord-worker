package local.piscord.worker.dto.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record MessageCreateDto(
    String roomId,
    String userId,
    String content,
    String type,
    String fileUrl,
    String replyTo,
    Boolean isEdited,
    Boolean isDeleted,
    String createdAt,
    String updatedAt) {
}
