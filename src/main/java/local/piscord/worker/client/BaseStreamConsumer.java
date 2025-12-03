package local.piscord.worker.client;

import java.time.Duration;
import java.util.function.Consumer;

import org.jboss.logging.Logger;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.stream.StreamMessage;
import io.quarkus.redis.datasource.stream.XGroupCreateArgs;
import io.quarkus.redis.datasource.stream.XReadGroupArgs;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public abstract class BaseStreamConsumer {

  private static final Logger LOG = Logger.getLogger(BaseStreamConsumer.class);

  protected ReactiveRedisDataSource dataSource;

  protected void initStream(String key, String group, Runnable onSuccess) {
    dataSource.stream(String.class)
        .xgroupCreate(key, group, "0", new XGroupCreateArgs().mkstream())
        .onFailure().recoverWithItem(t -> {
          LOG.debugf("Consumer group might already exist for key %s: %s", key, t.getMessage());
          return null;
        })
        .subscribe().with(
            x -> onSuccess.run(),
            t -> LOG.errorf("Failed to create consumer group for key %s: %s", key, t.getMessage()));
  }

  protected void startConsumerLoop(String key, String group, String consumer,
      Consumer<StreamMessage<String, String, String>> processor) {
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
              LOG.errorf("Error in consumer loop for stream %s, restarting in 5 seconds...", key, t);
              Uni.createFrom().voidItem()
                  .onItem().delayIt().by(Duration.ofSeconds(5))
                  .subscribe().with(v -> startConsumerLoop(key, group, consumer, processor));
            });
  }

  protected Uni<Void> ackMessage(String key, String group, String messageId) {
    return dataSource.stream(String.class)
        .xack(key, group, messageId)
        .replaceWithVoid();
  }
}
