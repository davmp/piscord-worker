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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.dto.notification.NotificationDeleteAllDto;
import local.piscord.worker.dto.notification.NotificationDeleteDto;
import local.piscord.worker.dto.notification.NotificationDto;
import local.piscord.worker.dto.notification.NotificationEventDto;
import local.piscord.worker.dto.notification.NotificationReadAllDto;
import local.piscord.worker.dto.notification.NotificationReadDto;
import local.piscord.worker.enums.events.NotificationEventType;
import local.piscord.worker.service.NotificationService;

@ApplicationScoped
public class NotificationStreamConsumer {

  private static final Logger LOG = Logger.getLogger(NotificationStreamConsumer.class);

  @ConfigProperty(name = "piscord.redis.stream.notification.key")
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
  NotificationService notificationService;

  void onStart(@Observes StartupEvent ev) {
    initStream(key, this::startNotificationConsumerLoop);
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

  private void startNotificationConsumerLoop() {
    startConsumerLoop(key, this::processNotificationEvent);
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

  private void processNotificationEvent(StreamMessage<String, String, String> streamMessage) {
    handleEvent(key, streamMessage, event -> {
      try {
        return switch (event.type()) {
          // Notifications
          case CREATE ->
            notificationService.create(objectMapper.treeToValue(event.payload(), NotificationDto.class));
          case READ ->
            notificationService.read(objectMapper.treeToValue(event.payload(), NotificationReadDto.class));
          case READ_ALL ->
            notificationService.readAll(objectMapper.treeToValue(event.payload(), NotificationReadAllDto.class));
          case DELETE ->
            notificationService.delete(objectMapper.treeToValue(event.payload(), NotificationDeleteDto.class));
          case DELETE_ALL ->
            notificationService.deleteAll(objectMapper.treeToValue(event.payload(), NotificationDeleteAllDto.class));
          default -> {
            LOG.warnf("Unknown or unhandled notification event type: %s", event.type());
            yield Uni.createFrom().voidItem();
          }
        };
      } catch (JsonProcessingException | IllegalArgumentException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private void handleEvent(String key, StreamMessage<String, String, String> streamMessage,
      Function<NotificationEventDto, Uni<Void>> processor) {
    String typeStr = streamMessage.payload().get("type");
    String payloadStr = streamMessage.payload().get("payload");

    if (typeStr == null || payloadStr == null) {
      LOG.warnf("Discarding invalid message format id: %s. Missing type or payload.", streamMessage.id());
      ackMessage(key, streamMessage.id());
      return;
    }

    LOG.debugf("Processing message %s: type=%s", streamMessage.id(), typeStr);

    try {
      NotificationEventType type = NotificationEventType.valueOf(typeStr);
      JsonNode payload = objectMapper.readTree(payloadStr);

      Uni<Void> processingUni = processor.apply(new NotificationEventDto(type, payload));

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
