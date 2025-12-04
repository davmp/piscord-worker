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
import local.piscord.worker.dto.chat.MessageDeleteDto;
import local.piscord.worker.dto.chat.MessageSendDto;
import local.piscord.worker.dto.chat.MessageUpdateDto;
import local.piscord.worker.model.Message;
import local.piscord.worker.model.MessagePreview;
import local.piscord.worker.model.UserSummary;
import local.piscord.worker.repository.MessageRepository;

@ApplicationScoped
public class MessageService {

    @Inject
    MessageRepository repo;

    public Uni<Void> send(MessageSendDto dto) {
        Message message = new Message();

        // Room ID
        try {
            message.setRoomId(new ObjectId(dto.roomId()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid room ID format: " + dto.roomId(), e);
        }

        // Author
        try {
            UserSummary author = new UserSummary();

            author.setId(new ObjectId(dto.author().id()));
            author.setUsername(dto.author().username());
            author.setPicture(dto.author().picture());

            message.setAuthor(author);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid message author: " + dto.author(), e);
        }

        // Reply To (Optional)
        if (dto.replyTo() != null) {
            try {
                MessagePreview replyPreview = new MessagePreview();

                replyPreview.setId(new ObjectId(dto.replyTo().id()));
                replyPreview.setContent(dto.replyTo().content());
                replyPreview.setAuthor(
                        new UserSummary(
                                new ObjectId(dto.replyTo().author().id()),
                                dto.replyTo().author().username(),
                                dto.replyTo().author().picture()));
                replyPreview.setCreatedAt(Instant.parse(dto.replyTo().sentAt()));

                message.setReplyTo(replyPreview);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid reply-to: " + dto.replyTo(), e);
            }
        }

        message.setContent(dto.content());
        message.setFileUrl(dto.fileUrl());
        message.setDeleted(false);
        message.setEditedAt(null);
        message.setCreatedAt(Instant.parse(dto.sentAt()));
        message.setUpdatedAt(Instant.parse(dto.sentAt()));

        return repo.persist(message);
    }

    public Uni<Void> update(MessageUpdateDto dto) {
        List<Bson> updates = new ArrayList<>();

        updates.add(Updates.set("content", dto.content()));
        updates.add(Updates.set("isEdited", true));
        updates.add(Updates.set("updatedAt", dto.updatedAt() != null ? Instant.parse(dto.updatedAt()) : Instant.now()));

        if (updates.isEmpty()) {
            return Uni.createFrom().nullItem();
        }

        return repo.update(dto.id(), dto.userId(), updates);
    }

    public Uni<Void> delete(MessageDeleteDto dto) {
        List<Bson> updates = new ArrayList<>();

        updates.add(Updates.set("content", "Essa mensagem foi apagada"));
        updates.add(Updates.set("fileUrl", ""));
        updates.add(Updates.set("replyTo", null));
        updates.add(Updates.set("isDeleted", true));
        updates.add(Updates.set("updatedAt", Instant.now()));

        return repo.update(dto.id(), dto.userId(), updates);
    }
}
