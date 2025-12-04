package local.piscord.worker.model;

import java.time.Instant;

import org.bson.types.ObjectId;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class MessagePreview {

  ObjectId id;

  String content;

  UserSummary author;

  Instant createdAt;

  public MessagePreview() {
  }

  public MessagePreview(ObjectId id, String content, UserSummary author, Instant createdAt) {
    this.id = id;
    this.content = content;
    this.author = author;
    this.createdAt = createdAt;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public UserSummary getAuthor() {
    return author;
  }

  public void setAuthor(UserSummary author) {
    this.author = author;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

}
