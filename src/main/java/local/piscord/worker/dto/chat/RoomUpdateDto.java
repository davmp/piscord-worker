package local.piscord.worker.dto.chat;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RegisterForReflection
public record RoomUpdateDto(
    @NotBlank(message = "Room ID is required") String id,

    @NotBlank(message = "User ID is required") String userId,

    String name,

    String description,

    String picture,

    String type,

    @NotBlank(message = "Room owner ID is required") String ownerId,

    List<String> removeMembers,

    List<String> addMembers,

    @Min(value = 2, message = "Room maximum members must be at least 2") @Max(value = 100, message = "Room maximum members must less 100") Integer maxMembers) {
}
