package local.piscord.worker.enums.events;

public enum NotificationEventType {
  CREATE("notification.create"),
  READ("notification.read"),
  READ_ALL("notification.read_all"),
  DELETE("notification.delete"),
  DELETE_ALL("notification.delete_all");

  private final String value;

  NotificationEventType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
