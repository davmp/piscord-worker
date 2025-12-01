package local.piscord.worker.model;

import java.time.Instant;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import local.piscord.worker.enums.RoomType;

public class Room {

  @BsonId
  ObjectId id;

  String name;

  String description;

  String picture;

  RoomType type; // public, private, direct

  List<ObjectId> members;

  List<ObjectId> admins;

  @BsonProperty("owner_id")
  ObjectId ownerId;

  @BsonProperty("max_members")
  Integer maxMembers;

  @BsonProperty("is_active")
  Boolean isActive;

  @BsonProperty("direct_key")
  String directKey;

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

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public void setType(RoomType type) {
    this.type = type;
  }

  public void setMembers(List<ObjectId> members) {
    this.members = members;
  }

  public void setAdmins(List<ObjectId> admins) {
    this.admins = admins;
  }

  public void setOwnerId(ObjectId ownerId) {
    this.ownerId = ownerId;
  }

  public void setMaxMembers(Integer maxMembers) {
    this.maxMembers = maxMembers;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public void setDirectKey(String directKey) {
    this.directKey = directKey;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
