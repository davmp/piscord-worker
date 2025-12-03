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
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.dto.user.UserEventDto;
import local.piscord.worker.dto.user.UserRegisterDto;
import local.piscord.worker.dto.user.UserUpdateDto;
import local.piscord.worker.enums.events.UserEventType;
import local.piscord.worker.service.UserService;

public class UserStreamConsumer extends BaseStreamConsumer {
    private static final Logger LOG = Logger.getLogger(UserStreamConsumer.class);

    @ConfigProperty(name = "piscord.redis.stream.user.key")
    String key;

    @ConfigProperty(name = "piscord.redis.stream.group")
    String group;

    @ConfigProperty(name = "piscord.redis.stream.consumer")
    String consumer;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    UserService userService;

    @Inject
    public void setDataSource(ReactiveRedisDataSource dataSource) {
        this.dataSource = dataSource;
    }

    void onStart(@Observes StartupEvent ev) {
        initStream(key, group, this::startUserConsumerLoop);
    }

    private void startUserConsumerLoop() {
        startConsumerLoop(key, group, consumer, this::processUserEvent);
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
                LOG.errorf("Failed to process user event %s: %s", event.type(), e.getMessage());
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
            ackMessage(key, group, streamMessage.id());
            return;
        }

        LOG.debugf("Processing message %s: type=%s", streamMessage.id(), typeStr);

        try {
            UserEventType type = UserEventType.fromValue(typeStr);
            JsonNode payload = objectMapper.readTree(payloadStr);

            Uni<Void> processingUni = processor.apply(new UserEventDto(type, payload));

            processingUni
                    .flatMap(v -> ackMessage(key, group, streamMessage.id()))
                    .subscribe().with(
                            success -> LOG.debugf("Event %s processed and acknowledged", streamMessage.id()),
                            failure -> LOG.errorf("Failed to process event %s: %s", streamMessage.id(), failure));
        } catch (Exception e) {
            LOG.errorf("Failed to process user event %s: %s", streamMessage.id(), e.getMessage());
            ackMessage(key, group, streamMessage.id());
        }
    }
}
