package local.piscord.worker.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import io.quarkus.runtime.annotations.RegisterForReflection;
import local.piscord.worker.enums.MessageType;

@RegisterForReflection
public class Message {

    @BsonId
    ObjectId id = new ObjectId();

    ObjectId roomId;

    ObjectId userId;

    String content;

    MessageType type; // text, image, file, system

    String fileUrl;

    ObjectId replyTo;

    boolean isEdited;

    boolean isDeleted;

    Instant createdAt;

    Instant updatedAt;

    public ObjectId getId() {
        return id;
    }

    public ObjectId getRoomId() {
        return roomId;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public ObjectId getReplyTo() {
        return replyTo;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(ObjectId id) {
        this.id = id;
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