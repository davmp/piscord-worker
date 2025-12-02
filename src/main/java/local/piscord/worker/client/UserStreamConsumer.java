package local.piscord.worker.client;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.stream.StreamMessage;
import io.quarkus.redis.datasource.stream.XGroupCreateArgs;
import io.quarkus.redis.datasource.stream.XReadGroupArgs;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.dto.user.UserEventDto;
import local.piscord.worker.dto.user.UserRegisterDto;
import local.piscord.worker.dto.user.UserUpdateDto;
import local.piscord.worker.enums.events.UserEventType;
import local.piscord.worker.service.UserService;

public class UserStreamConsumer {
    private static final Logger LOG = Logger.getLogger(UserStreamConsumer.class);

    @ConfigProperty(name = "piscord.redis.stream.user.key")
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
    UserService userService;

    void onStart(@Observes StartupEvent ev) {
        initStream(key, this::startUserConsumerLoop);
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

    private void startUserConsumerLoop() {
        startConsumerLoop(key, this::processUserEvent);
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

    private void processUserEvent(StreamMessage<String, String, String> streamMessage) {
        handleEvent(key, streamMessage, event -> {
            try {
                return switch (event.type()) {
                    // User
                    case REGISTER ->
                        userService.create(objectMapper.treeToValue(event.payload(), UserRegisterDto.class));
                    case UPDATE ->
                        userService.update(objectMapper.treeToValue(event.payload(), UserUpdateDto.class));
                    default -> {
                        LOG.warnf("Unknown or unhandled user event type: %s", event.type());
                        yield Uni.createFrom().voidItem();
                    }
                };
            } catch (JsonProcessingException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                LOG.errorf("Failed to process user event %s", event.type(), e);
                return Uni.createFrom().voidItem();
            }
        });
    }

    private void handleEvent(String key, StreamMessage<String, String, String> streamMessage,
            Function<UserEventDto, Uni<Void>> processor) {
        String typeStr = streamMessage.payload().get("type");
        String payloadStr = streamMessage.payload().get("payload");

        if (typeStr == null || payloadStr == null) {
            LOG.warnf("Discarding invalid message format id: %s. Missing type or payload.", streamMessage.id());
            ackMessage(key, streamMessage.id());
            return;
        }

        LOG.debugf("Processing message %s: type=%s", streamMessage.id(), typeStr);

        try {
            UserEventType type = UserEventType.valueOf(typeStr);
            JsonNode payload = objectMapper.readTree(payloadStr);

            Uni<Void> processingUni = processor.apply(new UserEventDto(type, payload));

            processingUni
                    .flatMap(v -> ackMessage(key, streamMessage.id()))
                    .subscribe().with(
                            success -> LOG.debugf("Event %s processed and acknowledged", streamMessage.id()),
                            failure -> LOG.errorf("Failed to process event %s", streamMessage.id(), failure));
        } catch (Exception e) {
            LOG.errorf("Failed to process user event %s", streamMessage.id(), e);
            ackMessage(key, streamMessage.id());
        }
    }

    private Uni<Void> ackMessage(String key, String messageId) {
        return dataSource.stream(String.class)
                .xack(key, group, messageId)
                .replaceWithVoid();
    }
}
