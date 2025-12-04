package local.piscord.worker.client;

import java.util.function.Function;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.stream.StreamMessage;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.dto.chat.ChatEventDto;
import local.piscord.worker.dto.chat.MessageDeleteDto;
import local.piscord.worker.dto.chat.MessageSendDto;
import local.piscord.worker.dto.chat.MessageUpdateDto;
import local.piscord.worker.dto.chat.RoomCreateDto;
import local.piscord.worker.dto.chat.RoomJoinDto;
import local.piscord.worker.dto.chat.RoomLeaveDto;
import local.piscord.worker.dto.chat.RoomUpdateDto;
import local.piscord.worker.enums.events.ChatEventType;
import local.piscord.worker.service.MessageService;
import local.piscord.worker.service.RoomService;

@ApplicationScoped
public class ChatStreamConsumer extends BaseStreamConsumer {

    private static final Logger LOG = Logger.getLogger(ChatStreamConsumer.class);

    @ConfigProperty(name = "piscord.redis.stream.chat.key")
    String key;

    @ConfigProperty(name = "piscord.redis.stream.group")
    String group;

    @ConfigProperty(name = "piscord.redis.stream.consumer")
    String consumer;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    RoomService roomService;

    @Inject
    MessageService messageService;

    @Inject
    public void setDataSource(ReactiveRedisDataSource dataSource) {
        this.dataSource = dataSource;
    }

    void onStart(@Observes StartupEvent ev) {
        initStream(key, group, this::startChatConsumerLoop);
    }

    private void startChatConsumerLoop() {
        startConsumerLoop(key, group, consumer, this::processChatEvent);
    }

    private void processChatEvent(StreamMessage<String, String, String> streamMessage) {
        handleEvent(key, streamMessage, event -> {
            try {
                return switch (event.type()) {
                    // Rooms
                    case ROOM_CREATE ->
                        roomService.create(objectMapper.treeToValue(event.payload(), RoomCreateDto.class));
                    case ROOM_UPDATE ->
                        roomService.update(objectMapper.treeToValue(event.payload(), RoomUpdateDto.class));
                    case ROOM_JOIN ->
                        roomService.join(objectMapper.treeToValue(event.payload(), RoomJoinDto.class));
                    case ROOM_LEAVE ->
                        roomService.leave(objectMapper.treeToValue(event.payload(), RoomLeaveDto.class));

                    // Messages
                    case MESSAGE_SEND ->
                        messageService.send(objectMapper.treeToValue(event.payload(), MessageSendDto.class));
                    case MESSAGE_UPDATE ->
                        messageService.update(objectMapper.treeToValue(event.payload(), MessageUpdateDto.class));
                    case MESSAGE_DELETE ->
                        messageService.delete(objectMapper.treeToValue(event.payload(), MessageDeleteDto.class));
                    default -> {
                        LOG.warnf("Unknown or unhandled chat event type: %s", event.type());
                        yield Uni.createFrom().voidItem();
                    }
                };
            } catch (JsonProcessingException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleEvent(String key, StreamMessage<String, String, String> streamMessage,
            Function<ChatEventDto, Uni<Void>> processor) {
        String typeStr = streamMessage.payload().get("type");
        String payloadStr = streamMessage.payload().get("payload");

        if (typeStr == null || payloadStr == null) {
            LOG.warnf("Discarding invalid message format id: %s. Missing type or payload.", streamMessage.id());
            ackMessage(key, group, streamMessage.id());
            return;
        }

        LOG.debugf("Processing message %s: type=%s", streamMessage.id(), typeStr);

        try {
            ChatEventType type = ChatEventType.fromValue(typeStr);
            JsonNode payload = objectMapper.readTree(payloadStr);

            processor.apply(new ChatEventDto(type, payload))
                    .flatMap(v -> ackMessage(key, group, streamMessage.id()))
                    .subscribe().with(
                            success -> LOG.debugf("Event %s processed and acknowledged", streamMessage.id()),
                            failure -> LOG.errorf("Failed to process event %s: %s", streamMessage.id(), failure));

        } catch (IllegalArgumentException | JsonProcessingException e) {
            LOG.errorf("Failed to process chat event %s: %s", streamMessage.id(), e.getMessage());
            ackMessage(key, group, streamMessage.id()).subscribe().with(x -> {
            }, t -> {
            });
        } catch (Exception e) {
            LOG.errorf("Failed to process chat event, not acknowledging message %s", streamMessage.id());
        }
    }
}
