package local.piscord.worker.enums.events;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static NotificationEventType fromValue(String value) {
    return Arrays.stream(NotificationEventType.values())
        .filter(t -> t.value.equals(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + value));
  }
}
