package local.piscord.worker.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import local.piscord.worker.enums.MessageType;

public class Message {

    @BsonId
    public ObjectId id;

    @BsonProperty("room_id")
    public ObjectId roomId;

    @BsonProperty("user_id")
    public ObjectId userId;

    public String content;

    public MessageType type; // text, image, file, system

    @BsonProperty("file_url")
    public String fileUrl;

    @BsonProperty("reply_to")
    public ObjectId replyTo;

    @BsonProperty("is_edited")
    public boolean isEdited;

    @BsonProperty("is_deleted")
    public boolean isDeleted;

    @BsonProperty("created_at")
    public Instant createdAt;

    @BsonProperty("updated_at")
    public Instant updatedAt;

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

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public ObjectId getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(ObjectId replyTo) {
        this.replyTo = replyTo;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}