package local.piscord.worker.model;

import org.bson.types.ObjectId;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UserSummary {

  ObjectId id;

  String username;

  String picture;

  public UserSummary() {
  }

  public UserSummary(ObjectId id, String username, String picture) {
    this.id = id;
    this.username = username;
    this.picture = picture;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }
}
