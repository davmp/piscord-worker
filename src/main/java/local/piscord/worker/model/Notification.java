package local.piscord.worker.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import io.quarkus.runtime.annotations.RegisterForReflection;
import local.piscord.worker.enums.NotificationType;

@RegisterForReflection
public class Notification {

  @BsonId
  ObjectId id = new ObjectId();

  ObjectId userId;

  NotificationType type;

  Boolean isRead;

  String title;

  String body;

  String picture;

  String actionUrl;

  Instant createdAt;

  public ObjectId getId() {
    return id;
  }

  public ObjectId getUserId() {
    return userId;
  }

  public NotificationType getType() {
    return type;
  }

  public Boolean getIsRead() {
    return isRead;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public String getPicture() {
    return picture;
  }

  public String getActionUrl() {
    return actionUrl;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setUserId(ObjectId userId) {
    this.userId = userId;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public void setIsRead(Boolean isRead) {
    this.isRead = isRead;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public void setActionUrl(String actionUrl) {
    this.actionUrl = actionUrl;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
