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
import local.piscord.worker.dto.chat.RoomKickDto;
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
    if (dto.ownerId() != null) {
      updates.add(Updates.set("ownerId", dto.ownerId()));
    }
    if (updates.isEmpty()) {
      return Uni.createFrom().voidItem();
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
    return repo.findById(dto.id())
        .onItem().ifNull().failWith(new IllegalArgumentException("Room not found"))
        .chain(room -> {
          if (!room.getOwnerId().equals(new ObjectId(dto.userId()))) {
            return repo.leave(dto.id(), dto.userId());
          }

          ObjectId newOwnerId = null;

          if (room.getAdmins() != null && !room.getAdmins().isEmpty()) {
            newOwnerId = room.getAdmins().get(0);
          } else if (room.getMembers() != null && !room.getMembers().isEmpty()) {
            newOwnerId = room.getMembers().get(0);
          }

          if (newOwnerId != null) {
            return repo.transferOwnership(dto.id(), newOwnerId.toHexString(), dto.userId());
          } else {
            return repo.delete(dto.id());
          }
        });
  }

  public Uni<Void> kick(RoomKickDto dto) {
    return repo.findById(dto.id())
        .onItem().ifNull().failWith(new IllegalArgumentException("Room not found"))
        .chain(room -> {
          boolean isOwner = room.getOwnerId().equals(new ObjectId(dto.adminId()));
          boolean isAdmin = room.getAdmins() != null && room.getAdmins().contains(new ObjectId(dto.adminId()));

          if (!isOwner && !isAdmin) {
            return Uni.createFrom().failure(new SecurityException("User is not authorized to kick members"));
          }

          boolean targetIsOwner = room.getOwnerId().equals(new ObjectId(dto.userId()));
          boolean targetIsAdmin = room.getAdmins() != null && room.getAdmins().contains(new ObjectId(dto.userId()));

          if (targetIsOwner) {
            return Uni.createFrom().failure(new SecurityException("Cannot kick the owner"));
          }

          if (targetIsAdmin && !isOwner) {
            return Uni.createFrom().failure(new SecurityException("Admins cannot kick other admins"));
          }

          return repo.kick(dto.id(), dto.userId());
        });
  }
}