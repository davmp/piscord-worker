package local.piscord.worker.model;

import java.time.Instant;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import io.quarkus.runtime.annotations.RegisterForReflection;
import local.piscord.worker.enums.RoomType;

@RegisterForReflection
public class Room {

  @BsonId
  ObjectId id = new ObjectId();

  String name;

  String description;

  String picture;

  RoomType type;

  List<ObjectId> members;

  List<ObjectId> admins;

  ObjectId ownerId;

  Integer maxMembers;

  Boolean isActive;

  String directKey;

  Instant createdAt;

  Instant updatedAt;

  public ObjectId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getPicture() {
    return picture;
  }

  public RoomType getType() {
    return type;
  }

  public List<ObjectId> getMembers() {
    return members;
  }

  public List<ObjectId> getAdmins() {
    return admins;
  }

  public ObjectId getOwnerId() {
    return ownerId;
  }

  public Integer getMaxMembers() {
    return maxMembers;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public String getDirectKey() {
    return directKey;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
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
