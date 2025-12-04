package local.piscord.worker.dto.chat;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RegisterForReflection
public record RoomCreateDto(
    String name,

    String description,

    String picture,

    @NotBlank(message = "Room type is required") String type,

    List<String> members,

    List<String> admins,

    @NotBlank(message = "Room owner ID is required") String ownerId,

    @Min(value = 2, message = "Room maximum members must be at least 2") @Max(value = 100, message = "Room maximum members must less 100") Integer maxMembers,

    Boolean isActive,

    String directKey) {
}
