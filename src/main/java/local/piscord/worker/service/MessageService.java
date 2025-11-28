package local.piscord.worker.service;

import java.time.Instant;

import org.bson.types.ObjectId;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import local.piscord.worker.dto.MessageDto;
import local.piscord.worker.model.Message;
import local.piscord.worker.repository.MessageRepository;

@ApplicationScoped
public class MessageService {

    @Inject
    MessageRepository repo;

    void persist(MessageDto dto) {
        Message message = new Message();

        // ID - Required
        if (dto.id() != null && !dto.id().isBlank()) {
            try {
                message.setId(new ObjectId(dto.id()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid message ID format: " + dto.id(), e);
            }
        } else {
            throw new IllegalArgumentException("Message ID cannot be null or blank.");
        }

        // Room ID - Required
        if (dto.roomId() != null && !dto.roomId().isBlank()) {
            try {
                message.setRoomId(new ObjectId(dto.roomId()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid room ID format: " + dto.roomId(), e);
            }
        } else {
            throw new IllegalArgumentException("Room ID cannot be null or blank.");
        }

        // User ID (Sender) - Required
        if (dto.userId() != null && !dto.userId().isBlank()) {
            try {
                message.setUserId(new ObjectId(dto.userId()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid user ID format: " + dto.userId(), e);
            }
        } else {
            throw new IllegalArgumentException("User ID cannot be null or blank.");
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
        message.setType(dto.type());
        message.setFileUrl(dto.fileUrl());
        message.setEdited(dto.isEdited());
        message.setDeleted(dto.isDeleted());
        message.setCreatedAt(dto.createdAt() == null ? null : Instant.ofEpochSecond(dto.createdAt()));
        message.setUpdatedAt(dto.updatedAt() == null ? null : Instant.ofEpochSecond(dto.updatedAt()));

        repo.insert(message);
    }
}
