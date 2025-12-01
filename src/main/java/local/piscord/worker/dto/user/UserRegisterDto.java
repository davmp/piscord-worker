package local.piscord.worker.dto.user;

public record UserRegisterDto(
        String id,
        String username,
        String password,
        String picture,
        String bio,
        Long createdAt,
        Long updatedAt) {
}
