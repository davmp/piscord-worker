package local.piscord.worker.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import local.piscord.worker.enums.NotificationType;

public class Notification {

  @BsonId
  ObjectId id;

  @BsonProperty("user_id")
  ObjectId userId;

  @BsonProperty("type")
  NotificationType type;

  @BsonProperty("is_read")
  Boolean isRead;

  String title;

  String body;

  String picture;

  @BsonProperty("action_url")
  String actionUrl;

  @BsonProperty("created_at")
  Instant createdAt;

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
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
