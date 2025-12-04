package local.piscord.worker.service;

import java.time.Instant;

import org.bson.types.ObjectId;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import local.piscord.worker.dto.notification.NotificationCreateDto;
import local.piscord.worker.dto.notification.NotificationDeleteAllDto;
import local.piscord.worker.dto.notification.NotificationDeleteDto;
import local.piscord.worker.dto.notification.NotificationReadAllDto;
import local.piscord.worker.dto.notification.NotificationReadDto;
import local.piscord.worker.enums.NotificationType;
import local.piscord.worker.model.Notification;
import local.piscord.worker.repository.NotificationRepository;

@ApplicationScoped
public class NotificationService {

  @Inject
  NotificationRepository repo;

  public Uni<Void> create(NotificationCreateDto dto) {
    Notification notification = new Notification();

    // User ID - Required
    try {
      notification.setUserId(new ObjectId(dto.userId()));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid user ID format: " + dto.userId(), e);
    }

    notification.setType(NotificationType.fromValue(dto.type()));
    notification.setTitle(dto.title());
    notification.setBody(dto.body());
    notification.setPicture(dto.picture());
    notification.setActionUrl(dto.actionUrl());
    notification.setCreatedAt(Instant.parse(dto.createdAt()));

    return repo.persist(notification);
  }

  public Uni<Void> read(NotificationReadDto dto) {
    return repo.read(dto.id(), dto.userId());
  }

  public Uni<Void> readAll(NotificationReadAllDto dto) {
    return repo.readAll(dto.userId());
  }

  public Uni<Void> delete(NotificationDeleteDto dto) {
    return repo.delete(dto.id(), dto.userId());
  }

  public Uni<Void> deleteAll(NotificationDeleteAllDto dto) {
    return repo.deleteAll(dto.userId());
  }
}
