package local.piscord.worker.service;

import java.time.Instant;

import org.bson.types.ObjectId;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import local.piscord.worker.dto.chat.MessageCreateDto;
import local.piscord.worker.enums.MessageType;
import local.piscord.worker.model.Message;
import local.piscord.worker.repository.MessageRepository;

@ApplicationScoped
public class MessageService {

    @Inject
    MessageRepository repo;

    public Uni<Void> create(MessageCreateDto dto) {
        Message message = new Message();

        // Room ID - Required
        try {
            message.setRoomId(new ObjectId(dto.roomId()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid room ID format: " + dto.roomId(), e);
        }

        // User ID (Sender) - Required
        try {
            message.setUserId(new ObjectId(dto.userId()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format: " + dto.userId(), e);
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
            message.setType(MessageType.fromValue(dto.type()));
        }
        message.setFileUrl(dto.fileUrl());
        message.setEdited(dto.isEdited());
        message.setDeleted(dto.isDeleted());
        message.setCreatedAt(Instant.parse(dto.createdAt()));
        message.setUpdatedAt(Instant.parse(dto.updatedAt()));

        return repo.persist(message);
    }

    public Uni<Void> delete(String id) {
        if (id != null && !id.isBlank()) {
            return repo.delete(id);
        }
        return Uni.createFrom().voidItem();
    }
}
