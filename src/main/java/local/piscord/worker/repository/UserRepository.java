package local.piscord.worker.repository;

import static com.mongodb.client.model.Filters.eq;

import java.util.List;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.model.User;

@ApplicationScoped
public class UserRepository {

  @Inject
  ReactiveMongoClient reactiveClient;

  private ReactiveMongoCollection<User> col() {
    return reactiveClient
        .getDatabase("piscord")
        .getCollection("users", User.class);
  }

  public Uni<User> findById(String id) {
    return col().find(eq("_id", new ObjectId(id))).toUni();
  }

  public Uni<User> findByUsername(String username) {
    return col().find(eq("username", username)).toUni();
  }

  public Uni<Void> persist(User user) {
    return col().insertOne(user).replaceWithVoid();
  }

  public Uni<Void> update(String id, List<Bson> updates) {
    if (updates.isEmpty()) {
      return Uni.createFrom().voidItem();
    }

    return col().updateOne(eq("_id", new ObjectId(id)), Updates.combine(updates)).replaceWithVoid();
  }

  public void init(@Observes StartupEvent ev) {
    col().createIndex(Indexes.ascending("username"), new IndexOptions().unique(true))
        .subscribe()
        .with(s -> {
        }, Throwable::printStackTrace);
  }
}
