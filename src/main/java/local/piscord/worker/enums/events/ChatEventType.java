package local.piscord.worker.enums.events;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatEventType {
    // Room
    ROOM_CREATE("room.create"),
    ROOM_UPDATE("room.update"),
    ROOM_JOIN("room.join"),
    ROOM_LEAVE("room.leave"),

    // Message
    MESSAGE_SEND("message.send"),
    MESSAGE_UPDATE("message.update"),
    MESSAGE_DELETE("message.delete");

    private final String value;

    ChatEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ChatEventType fromValue(String value) {
        return Arrays.stream(ChatEventType.values())
                .filter(t -> t.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + value));
    }
}
