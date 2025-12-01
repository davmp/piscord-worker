package local.piscord.worker.repository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.model.Notification;

@ApplicationScoped
public class NotificationRepository {

  @Inject
  ReactiveMongoClient client;

  private ReactiveMongoCollection<Notification> col() {
    return client
        .getDatabase("piscord")
        .getCollection("notifications", Notification.class);
  }

  public Uni<Void> persist(Notification notification) {
    return col().insertOne(notification).replaceWithVoid();
  }

  public Uni<Void> read(String id, String userId) {
    return col().updateOne(
        and(eq("_id", new ObjectId(id)), eq("user_id", new ObjectId(userId))),
        set("is_read", true))
        .replaceWithVoid();
  }

  public Uni<Void> readAll(String userId) {
    return col().updateMany(eq("user_id", new ObjectId(userId)), set("is_read", true)).replaceWithVoid();
  }

  public Uni<Void> delete(String id, String userId) {
    return col().deleteOne(and(eq("_id", new ObjectId(id)), eq("user_id", new ObjectId(userId)))).replaceWithVoid();
  }

  public Uni<Void> deleteAll(String userId) {
    return col().deleteMany(eq("user_id", new ObjectId(userId))).replaceWithVoid();
  }

  public void init(@Observes StartupEvent ev) {
    col().createIndex(Indexes.compoundIndex(Indexes.ascending("user_id"), Indexes.descending("created_at")))
        .subscribe()
        .with(s -> {
        }, Throwable::printStackTrace);

    // TTL index: expire after 5 days (432000 seconds)
    col().createIndex(Indexes.ascending("created_at"),
        new IndexOptions().expireAfter(5L * 24 * 60 * 60, TimeUnit.SECONDS))
        .subscribe()
        .with(s -> {
        }, Throwable::printStackTrace);
  }
}
