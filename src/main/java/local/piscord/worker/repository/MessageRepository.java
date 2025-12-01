package local.piscord.worker.repository;

import static com.mongodb.client.model.Filters.eq;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Indexes;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import local.piscord.worker.model.Message;

@ApplicationScoped
public class MessageRepository {

    @Inject
    ReactiveMongoClient client;

    private ReactiveMongoCollection<Message> col() {
        return client
                .getDatabase("piscord")
                .getCollection("messages", Message.class);
    }

    public Uni<Void> persist(Message msg) {
        return col().insertOne(msg).replaceWithVoid();
    }

    public Uni<Void> update(Message msg) {
        return col().replaceOne(eq("_id", msg.getId()), msg).replaceWithVoid();
    }

    public Uni<Void> delete(String id) {
        return col().deleteOne(eq("_id", new ObjectId(id))).replaceWithVoid();
    }

    public void init(@Observes StartupEvent ev) {
        col().createIndex(Indexes.compoundIndex(Indexes.ascending("room_id"), Indexes.descending("created_at")))
                .subscribe()
                .with(s -> {
                }, Throwable::printStackTrace);

        col().createIndex(Indexes.ascending("user_id"))
                .subscribe()
                .with(s -> {
                }, Throwable::printStackTrace);
    }
}
