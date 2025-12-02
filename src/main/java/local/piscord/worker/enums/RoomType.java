package local.piscord.worker.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoomType {
  PUBLIC("public"),
  PRIVATE("private"),
  DIRECT("direct");

  private final String value;

  RoomType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static RoomType fromValue(String value) {
    return Arrays.stream(RoomType.values())
        .filter(t -> t.value.equals(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + value));
  }
}
