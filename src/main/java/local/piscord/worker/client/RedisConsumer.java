package local.piscord.worker.client;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.model.Message;
import local.piscord.worker.repository.MessageRepository;

@ApplicationScoped
public class RedisConsumer {

    private static final Logger LOG = Logger.getLogger(RedisConsumer.class);

    @ConfigProperty(name = "piscord.redis.channel")
    String channel;

    @Inject
    ReactiveRedisDataSource reactiveRedisDataSource;

    @Inject
    MessageRepository messageRepository;

    @Inject
    ObjectMapper objectMapper;

    void onStart(@Observes StartupEvent ev) {
        LOG.info("Starting Redis Consumer...");
        reactiveRedisDataSource.pubsub(String.class)
                .subscribe(channel)
                .subscribe().with(this::onMessage);
    }

    private void onMessage(String messageJson) {
        LOG.debugf("Received message: %s", messageJson);
        try {
            Message message = objectMapper.readValue(messageJson, Message.class);
            messageRepository.persistReactive(message)
                    .subscribe().with(
                            success -> LOG.debug("Message persisted successfully"),
                            failure -> LOG.error("Failed to persist message", failure)
                    );
        } catch (JsonProcessingException e) {
            LOG.error("Failed to deserialize message", e);
        }
    }

}
