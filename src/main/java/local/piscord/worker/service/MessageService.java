package local.piscord.worker.service;

import java.time.Instant;

import org.bson.types.ObjectId;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import local.piscord.worker.dto.chat.MessageDto;
import local.piscord.worker.enums.MessageType;
import local.piscord.worker.model.Message;
import local.piscord.worker.repository.MessageRepository;

@ApplicationScoped
public class MessageService {

    @Inject
    MessageRepository repo;

    public Uni<Void> create(MessageDto dto) {
        return repo.persist(mapToEntity(dto));
    }

    public Uni<Void> update(MessageDto dto) {
        if (dto.id() != null && !dto.id().isBlank()) {
            return repo.update(mapToEntity(dto));
        }
        return Uni.createFrom().voidItem();
    }

    public Uni<Void> delete(String id) {
        if (id != null && !id.isBlank()) {
            return repo.delete(id);
        }
        return Uni.createFrom().voidItem();
    }

    private Message mapToEntity(MessageDto dto) {
        Message message = new Message();

        // ID - Required
        try {
            message.setId(new ObjectId(dto.id()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid message ID format: " + dto.id(), e);
        }

        // Room ID
        if (dto.roomId() != null && !dto.roomId().isBlank()) {
            try {
                message.setRoomId(new ObjectId(dto.roomId()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid room ID format: " + dto.roomId(), e);
            }
        }

        // User ID (Sender)
        if (dto.userId() != null && !dto.userId().isBlank()) {
            try {
                message.setUserId(new ObjectId(dto.userId()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid user ID format: " + dto.userId(), e);
            }
        }

        // Reply To (Message ID) - Optional
        if (dto.replyTo() != null && !dto.replyTo().isBlank()) {
            try {
                message.setReplyTo(new ObjectId(dto.replyTo()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid reply-to ID format: " + dto.replyTo(), e);
            }
        }
        message.setContent(dto.content());
        if (dto.type() != null) {
            message.setType(MessageType.valueOf(dto.type()));
        }
        message.setFileUrl(dto.fileUrl());
        message.setEdited(dto.isEdited());
        message.setDeleted(dto.isDeleted());
        message.setCreatedAt(dto.createdAt() == null ? null : Instant.ofEpochSecond(dto.createdAt()));
        message.setUpdatedAt(dto.updatedAt() == null ? null : Instant.ofEpochSecond(dto.updatedAt()));

        return message;
    }
}
