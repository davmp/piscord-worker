package local.piscord.worker.repository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

import java.util.List;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.model.Room;

@ApplicationScoped
public class RoomRepository {

  @Inject
  ReactiveMongoClient client;

  private ReactiveMongoCollection<Room> col() {
    return client
        .getDatabase("piscord")
        .getCollection("rooms", Room.class);
  }

  public Uni<Void> persist(Room room) {
    return col().insertOne(room).replaceWithVoid();
  }

  public Uni<Room> findById(String id) {
    return col().find(eq("_id", new ObjectId(id))).collect().first();
  }

  public Uni<Void> update(String id, String userId, List<Bson> updates) {
    return col()
        .updateOne(and(eq("_id", new ObjectId(id)), in("admins", new ObjectId(userId))), Updates.combine(updates))
        .replaceWithVoid();
  }

  public Uni<Void> join(String id, String userId) {
    return col()
        .updateOne(and(eq("_id", new ObjectId(id)), in("admins", new ObjectId(userId))),
            Updates.addToSet("members", new ObjectId(userId)))
        .replaceWithVoid();
  }

  public Uni<Void> leave(String id, String userId) {
    return col()
        .updateOne(and(eq("_id", new ObjectId(id)), in("admins", new ObjectId(userId))),
            Updates.combine(
                Updates.pull("members", new ObjectId(userId)),
                Updates.pull("admins", new ObjectId(userId))))
        .replaceWithVoid();
  }

  public Uni<Void> kick(String id, String userId) {
    return col()
        .updateOne(eq("_id", new ObjectId(id)),
            Updates.combine(
                Updates.pull("members", new ObjectId(userId)),
                Updates.pull("admins", new ObjectId(userId))))
        .replaceWithVoid();
  }

  public Uni<Void> delete(String id) {
    return col().deleteOne(eq("_id", new ObjectId(id))).replaceWithVoid();
  }

  public Uni<Void> transferOwnership(String id, String newOwnerId, String oldOwnerId) {
    return col()
        .updateOne(eq("_id", new ObjectId(id)),
            Updates.combine(
                Updates.set("ownerId", new ObjectId(newOwnerId)),
                Updates.pull("members", new ObjectId(oldOwnerId)),
                Updates.pull("admins", new ObjectId(oldOwnerId))))
        .replaceWithVoid();
  }

  public void init(@Observes StartupEvent ev) {
    col().createIndex(Indexes.ascending("created_by"))
        .subscribe()
        .with(s -> {
        }, Throwable::printStackTrace);

    col().createIndex(Indexes.ascending("members"))
        .subscribe()
        .with(s -> {
        }, Throwable::printStackTrace);
  }
}
