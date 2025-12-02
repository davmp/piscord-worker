package local.piscord.worker.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {
  NEW_MESSAGE("new_message"),
  USER_JOINED("user_joined"),
  USER_LEFT("user_left"),
  FRIEND_REQUEST("friend_request"),
  FRIEND_REQUEST_ACCEPTED("friend_request_accepted"),
  ROOM_INVITE("room_invite"),
  MENTION("mention"),
  SYSTEM("system");

  private final String value;

  NotificationType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static NotificationType fromValue(String value) {
    return Arrays.stream(NotificationType.values())
        .filter(t -> t.value.equals(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + value));
  }
}
