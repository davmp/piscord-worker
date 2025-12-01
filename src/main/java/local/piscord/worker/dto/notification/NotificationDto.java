package local.piscord.worker.dto.notification;

public record NotificationDto(
    String id,
    String userId,
    String type,
    Boolean isRead,
    String title,
    String body,
    String picture,
    String actionUrl,
    Long createdAt) {
}
