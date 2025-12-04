package local.piscord.worker.repository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

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

    public Uni<Void> update(String id, String userId, List<Bson> updates) {
        return col()
                .updateOne(and(eq("_id", new ObjectId(id)), eq("user_id", new ObjectId(userId))),
                        Updates.combine(updates))
                .replaceWithVoid();
    }

    public Uni<Void> updateAuthorDetails(ObjectId userId, String username, String picture) {
        List<Bson> updates = new java.util.ArrayList<>();

        if (username != null) {
            updates.add(Updates.set("author.username", username));
        }
        if (picture != null) {
            updates.add(Updates.set("author.picture", picture));
        }

        if (updates.isEmpty()) {
            return Uni.createFrom().voidItem();
        }

        return col().updateMany(eq("author._id", userId), Updates.combine(updates)).replaceWithVoid();
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
