package local.piscord.worker.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Message {

    @BsonId
    ObjectId id = new ObjectId();

    ObjectId roomId;

    UserSummary author;

    String content;

    String fileUrl;

    MessagePreview replyTo;

    Boolean isDeleted;

    Instant editedAt;

    Instant createdAt;

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

    public UserSummary getAuthor() {
        return author;
    }

    public void setAuthor(UserSummary author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public MessagePreview getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(MessagePreview replyTo) {
        this.replyTo = replyTo;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Instant getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Instant createdAt) {
        this.editedAt = createdAt;
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