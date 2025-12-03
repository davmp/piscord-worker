package local.piscord.worker.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Updates;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import local.piscord.worker.dto.user.UserRegisterDto;
import local.piscord.worker.dto.user.UserUpdateDto;
import local.piscord.worker.model.User;
import local.piscord.worker.repository.UserRepository;

@ApplicationScoped
public class UserService {

  @Inject
  UserRepository repo;

  public Uni<Void> create(UserRegisterDto dto) {
    User user = new User();

    user.setUsername(dto.username());
    user.setPassword(dto.password());
    user.setPicture(dto.picture());
    user.setBio(dto.bio());
    user.setCreatedAt(Instant.parse(dto.createdAt()));
    user.setUpdatedAt(Instant.parse(dto.updatedAt()));

    return repo.persist(user);
  }

  public Uni<Void> update(UserUpdateDto dto) {
    List<Bson> updates = new ArrayList<>();
    if (dto.username() != null) {
      updates.add(Updates.set("username", dto.username()));
    }
    if (dto.password() != null) {
      updates.add(Updates.set("password", dto.password()));
    }
    if (dto.picture() != null) {
      updates.add(Updates.set("picture", dto.picture()));
    }
    if (dto.bio() != null) {
      updates.add(Updates.set("bio", dto.bio()));
    }

    if (updates.isEmpty()) {
      return Uni.createFrom().voidItem();
    }

    updates.add(Updates.set("updatedAt", Instant.now()));
    return repo.update(dto.id(), updates);
  }
}
