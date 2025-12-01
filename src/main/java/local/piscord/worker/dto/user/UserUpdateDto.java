package local.piscord.worker.dto.user;

public record UserUpdateDto(
        String id,
        String username,
        String password,
        String picture,
        String bio,
        Long updatedAt) {
}
