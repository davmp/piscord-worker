package local.piscord.worker.enums.events;

public enum ChatEventType {
    // Room
    ROOM_CREATE("room.create"),
    ROOM_UPDATE("room.update"),
    ROOM_JOIN("room.join"),
    ROOM_LEAVE("room.leave"),

    // Message
    MESSAGE_CREATE("message.create"),
    MESSAGE_UPDATE("message.update"),
    MESSAGE_DELETE("message.delete");

    private final String value;

    ChatEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
