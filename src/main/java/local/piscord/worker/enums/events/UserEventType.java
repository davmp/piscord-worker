package local.piscord.worker.enums.events;

public enum UserEventType {
  REGISTER("user.register"),
  UPDATE("user.update");

  private final String value;

  UserEventType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
