package local.piscord.worker.dto.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;

@RegisterForReflection
public record RoomKickDto(
    @NotBlank(message = "Room ID is required") String id,
    @NotBlank(message = "Admin ID is required") String adminId,
    @NotBlank(message = "User ID is required") String userId) {
}
