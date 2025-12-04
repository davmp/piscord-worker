package local.piscord.worker.dto.chat;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateDto(
                @NotBlank(message = "Message ID is required") String id,
                @NotBlank(message = "User ID is required") String userId,
                @NotBlank(message = "Message content is required") String content,
                String updatedAt) {
}
