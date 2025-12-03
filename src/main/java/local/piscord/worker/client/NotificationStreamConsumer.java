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
import local.piscord.worker.dto.notification.NotificationCreateDto;
import local.piscord.worker.dto.notification.NotificationDeleteAllDto;
import local.piscord.worker.dto.notification.NotificationDeleteDto;
import local.piscord.worker.dto.notification.NotificationEventDto;
import local.piscord.worker.dto.notification.NotificationReadAllDto;
import local.piscord.worker.dto.notification.NotificationReadDto;
import local.piscord.worker.enums.events.NotificationEventType;
import local.piscord.worker.service.NotificationService;

@ApplicationScoped
public class NotificationStreamConsumer extends BaseStreamConsumer {

  private static final Logger LOG = Logger.getLogger(NotificationStreamConsumer.class);

  @ConfigProperty(name = "piscord.redis.stream.notification.key")
  String key;

  @ConfigProperty(name = "piscord.redis.stream.group")
  String group;

  @ConfigProperty(name = "piscord.redis.stream.consumer")
  String consumer;

  @Inject
  ObjectMapper objectMapper;

  @Inject
  NotificationService notificationService;

  @Inject
  public void setDataSource(ReactiveRedisDataSource dataSource) {
    this.dataSource = dataSource;
  }

  void onStart(@Observes StartupEvent ev) {
    initStream(key, group, this::startNotificationConsumerLoop);
  }

  private void startNotificationConsumerLoop() {
    startConsumerLoop(key, group, consumer, this::processNotificationEvent);
  }

  private void processNotificationEvent(StreamMessage<String, String, String> streamMessage) {
    handleEvent(key, streamMessage, event -> {
      try {
        return switch (event.type()) {
          // Notifications
          case CREATE ->
            notificationService.create(objectMapper.treeToValue(event.payload(), NotificationCreateDto.class));
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
      ackMessage(key, group, streamMessage.id());
      return;
    }

    LOG.debugf("Processing message %s: type=%s", streamMessage.id(), typeStr);

    try {
      NotificationEventType type = NotificationEventType.fromValue(typeStr);
      JsonNode payload = objectMapper.readTree(payloadStr);

      Uni<Void> processingUni = processor.apply(new NotificationEventDto(type, payload));

      processingUni
          .flatMap(v -> ackMessage(key, group, streamMessage.id()))
          .subscribe().with(
              success -> LOG.debugf("Event %s processed and acknowledged", streamMessage.id()),
              failure -> LOG.errorf("Failed to process event %s: %s", streamMessage.id(), failure));

    } catch (Exception e) {
      LOG.errorf("Failed to process notification event %s: %s", streamMessage.id(), e.getMessage());
      ackMessage(key, group, streamMessage.id()).subscribe().with(x -> {
      }, t -> {
      });
    }
  }
}
