package local.piscord.worker.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import local.piscord.worker.enums.MessageType;

public class Message {

    @BsonId
    ObjectId id;

    @BsonProperty("room_id")
    ObjectId roomId;

    @BsonProperty("user_id")
    ObjectId userId;

    String content;

    MessageType type; // text, image, file, system

    @BsonProperty("file_url")
    String fileUrl;

    @BsonProperty("reply_to")
    ObjectId replyTo;

    @BsonProperty("is_edited")
    boolean isEdited;

    @BsonProperty("is_deleted")
    boolean isDeleted;

    @BsonProperty("created_at")
    Instant createdAt;

    @BsonProperty("updated_at")
    Instant updatedAt;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getRoomId() {
        return roomId;
    }

    public void setRoomId(ObjectId roomId) {
        this.roomId = roomId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setReplyTo(ObjectId replyTo) {
        this.replyTo = replyTo;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}