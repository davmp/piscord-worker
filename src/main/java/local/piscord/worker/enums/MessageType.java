package local.piscord.worker.enums;

public enum MessageType {
    TEXT("text"),
    IMAGE("image"),
    FILE("file"),
    SYSTEM("system");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
