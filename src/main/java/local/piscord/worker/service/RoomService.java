package local.piscord.worker.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Updates;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import local.piscord.worker.dto.chat.RoomCreateDto;
import local.piscord.worker.dto.chat.RoomJoinDto;
import local.piscord.worker.dto.chat.RoomLeaveDto;
import local.piscord.worker.dto.chat.RoomUpdateDto;
import local.piscord.worker.enums.RoomType;
import local.piscord.worker.model.Room;
import local.piscord.worker.repository.RoomRepository;

@ApplicationScoped
public class RoomService {

  @Inject
  RoomRepository repo;

  public Uni<Void> create(RoomCreateDto dto) {
    Room room = new Room();

    room.setName(dto.name());
    room.setDescription(dto.description());
    room.setPicture(dto.picture());
    room.setType(RoomType.fromValue(dto.type()));

    // Members - Required
    try {
      List<ObjectId> members = new ArrayList<>();
      for (String member : dto.members()) {
        members.add(new ObjectId(member));
      }
      room.setMembers(members);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid members format: " + dto.members(), e);
    }

    // Admins - Required
    try {
      List<ObjectId> admins = new ArrayList<>();
      for (String admin : dto.admins()) {
        admins.add(new ObjectId(admin));
      }
      room.setAdmins(admins);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid admins format: " + dto.admins(), e);
    }

    // Owner ID - Required
    try {
      room.setOwnerId(new ObjectId(dto.ownerId()));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid owner ID format: " + dto.ownerId(), e);
    }

    room.setMaxMembers(dto.maxMembers());
    room.setIsActive(dto.isActive());
    room.setDirectKey(dto.directKey());
    room.setCreatedAt(Instant.now());
    room.setUpdatedAt(Instant.now());

    return repo.persist(room);
  }

  public Uni<Void> update(RoomUpdateDto dto) {
    List<Bson> updates = new ArrayList<>();

    if (dto.name() != null) {
      updates.add(Updates.set("name", dto.name()));
    }
    if (dto.description() != null) {
      updates.add(Updates.set("description", dto.description()));
    }
    if (dto.picture() != null) {
      updates.add(Updates.set("picture", dto.picture()));
    }
    if (dto.type() != null) {
      updates.add(Updates.set("type", dto.type()));
    }
    if (dto.ownerId() != null) {
      updates.add(Updates.set("ownerId", dto.ownerId()));
    }
    if (updates.isEmpty()) {
      return Uni.createFrom().voidItem();
    }

    if (dto.removeMembers() != null || dto.addMembers() != null) {
      if (dto.removeMembers() != null) {
        updates.add(Updates.pullAll("members", dto.removeMembers().stream().map(ObjectId::new).toList()));
      }
      if (dto.addMembers() != null) {
        updates.add(Updates.addEachToSet("members", dto.addMembers().stream().map(ObjectId::new).toList()));
      }

      if (dto.maxMembers() != null) {
        updates.add(Updates.set("max_members", dto.maxMembers()));
      }
    }

    if (updates.isEmpty()) {
      return Uni.createFrom().voidItem();
    }

    updates.add(Updates.set("updatedAt", Instant.now()));
    return repo.update(dto.id(), dto.userId(), updates);
  }

  public Uni<Void> join(RoomJoinDto dto) {
    return repo.join(dto.id(), dto.userId());
  }

  public Uni<Void> leave(RoomLeaveDto dto) {
    return repo.leave(dto.id(), dto.userId());
  }
}