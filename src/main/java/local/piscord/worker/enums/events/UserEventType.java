package local.piscord.worker.enums.events;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserEventType {
  REGISTER("user.register"),
  UPDATE("user.update");

  private final String value;

  UserEventType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static UserEventType fromValue(String value) {
    return Arrays.stream(UserEventType.values())
        .filter(t -> t.value.equals(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + value));
  }
}
