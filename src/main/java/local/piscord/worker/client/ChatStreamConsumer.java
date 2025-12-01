package local.piscord.worker.client;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.stream.StreamMessage;
import io.quarkus.redis.datasource.stream.XGroupCreateArgs;
import io.quarkus.redis.datasource.stream.XReadGroupArgs;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.dto.chat.ChatEventDto;
import local.piscord.worker.dto.chat.MessageDto;
import local.piscord.worker.dto.chat.RoomDto;
import local.piscord.worker.dto.chat.RoomJoinDto;
import local.piscord.worker.dto.chat.RoomLeaveDto;
import local.piscord.worker.dto.chat.RoomUpdateDto;
import local.piscord.worker.service.MessageService;
import local.piscord.worker.service.RoomService;

@ApplicationScoped
public class ChatStreamConsumer {

    private static final Logger LOG = Logger.getLogger(ChatStreamConsumer.class);

    @ConfigProperty(name = "piscord.redis.stream.chat.key")
    String key;

    @ConfigProperty(name = "piscord.redis.stream.group")
    String group;

    @ConfigProperty(name = "piscord.redis.stream.consumer")
    String consumer;

    @Inject
    ReactiveRedisDataSource dataSource;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    RoomService roomService;

    @Inject
    MessageService messageService;

    void onStart(@Observes StartupEvent ev) {
        initStream(key, this::startChatConsumerLoop);
    }

    private void initStream(String key, Runnable onSuccess) {
        dataSource.stream(String.class)
                .xgroupCreate(key, group, "0", new XGroupCreateArgs().mkstream())
                .onFailure().recoverWithItem(t -> {
                    LOG.debugf("Consumer group might already exist for key %s: %s", key, t.getMessage());
                    return null;
                })
                .subscribe().with(
                        x -> onSuccess.run(),
                        t -> LOG.errorf("Failed to create consumer group for key %s", key, t));
    }

    private void startChatConsumerLoop() {
        startConsumerLoop(key, this::processChatEvent);
    }

    private void startConsumerLoop(String key, Consumer<StreamMessage<String, String, String>> processor) {
        LOG.infof("Starting consumer loop for stream %s, group %s, consumer %s", key, group, consumer);

        Multi.createBy().repeating()
                .uni(() -> dataSource.stream(String.class)
                        .xreadgroup(group, consumer, key, ">",
                                new XReadGroupArgs().count(1).block(Duration.ofSeconds(2))))
                .whilst(x -> true) // Infinite loop
                .subscribe().with(
                        messages -> {
                            if (messages != null) {
                                for (StreamMessage<String, String, String> msg : messages) {
                                    processor.accept(msg);
                                }
                            }
                        },
                        t -> {
                            LOG.errorf("Error in consumer loop for stream %s, restarting...", key, t);
                            startConsumerLoop(key, processor);
                        });
    }

    private void processChatEvent(StreamMessage<String, String, String> streamMessage) {
        handleEvent(key, streamMessage, event -> {
            try {
                return switch (event.type()) {
                    // Rooms
                    case ROOM_CREATE ->
                        roomService.create(objectMapper.treeToValue(event.payload(), RoomDto.class));
                    case ROOM_UPDATE ->
                        roomService.update(objectMapper.treeToValue(event.payload(), RoomUpdateDto.class));
                    case ROOM_JOIN ->
                        roomService.join(objectMapper.treeToValue(event.payload(), RoomJoinDto.class));
                    case ROOM_LEAVE ->
                        roomService.leave(objectMapper.treeToValue(event.payload(), RoomLeaveDto.class));

                    // Messages
                    case MESSAGE_CREATE ->
                        messageService.create(objectMapper.treeToValue(event.payload(), MessageDto.class));
                    case MESSAGE_UPDATE ->
                        messageService.update(objectMapper.treeToValue(event.payload(), MessageDto.class));
                    case MESSAGE_DELETE ->
                        messageService.delete(objectMapper.treeToValue(event.payload(), String.class));
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
        String messageJson = streamMessage.payload().get("content");

        if (messageJson == null) {
            if (!streamMessage.payload().isEmpty()) {
                messageJson = streamMessage.payload().values().iterator().next();
            }
        }

        if (messageJson == null) {
            LOG.warnf("Received empty message id: %s", streamMessage.id());
            ackMessage(key, streamMessage.id());
            return;
        }

        LOG.debugf("Processing message %s: %s", streamMessage.id(), messageJson);

        try {
            ChatEventDto event = objectMapper.readValue(messageJson, ChatEventDto.class);
            Uni<Void> processingUni = processor.apply(event);

            processingUni
                    .flatMap(v -> ackMessage(key, streamMessage.id()))
                    .subscribe().with(
                            success -> LOG.debugf("Event %s processed and acknowledged", streamMessage.id()),
                            failure -> LOG.errorf("Failed to process event %s", streamMessage.id(), failure));

        } catch (Exception e) {
            LOG.errorf("Failed to deserialize or process message %s", streamMessage.id(), e);
            ackMessage(key, streamMessage.id()).subscribe().with(x -> {
            }, t -> {
            });
        }
    }

    private Uni<Void> ackMessage(String key, String messageId) {
        return dataSource.stream(String.class)
                .xack(key, group, messageId)
                .replaceWithVoid();
    }
}
