package local.piscord.worker.repository;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.descending;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import local.piscord.worker.model.Message;

@ApplicationScoped
public class MessageRepository {

    @Inject
    MongoClient client;

    @Inject
    ReactiveMongoClient reactiveClient;

    private MongoCollection<Message> col() {
        return client
                .getDatabase("piscord")
                .getCollection("messages", Message.class);
    }

    private ReactiveMongoCollection<Message> reactiveCol() {
        return reactiveClient
                .getDatabase("piscord")
                .getCollection("messages", Message.class);
    }

    public Uni<Void> persistReactive(Message msg) {
        return reactiveCol().insertOne(msg).replaceWithVoid();
    }

    public void insert(Message msg) {
        col().insertOne(msg);
    }

    public List<Message> findLatest(String roomId, int limit) {
        return col()
                .find(eq("room_id", new ObjectId(roomId)))
                .sort(descending("created_at"))
                .limit(limit)
                .into(new ArrayList<>());
    }
}
