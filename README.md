# 📖 Piscord Worker (Persistence worker)

Persistence worker for Piscord, a real-time chat platform. This project was developed using **Java 21**, **Quarkus Framework**, **GraalVM**, **Redis**, and **MongoDB**.

> [!TIP]
> For complete system orchestration (Backend + Frontend + Database) via Kubernetes, visit the main repository:
> 👉 **[Piscord App - Main Repository](https://github.com/davmp/piscord-app)**

---

## 🚀 Technologies

- **Quarkus** (Java 21) with **GraalVM Native Image** for fast startup and low memory consumption.[web:16][web:21]  
- **Redis Streams** for ingesting chat events and writing commands.[web:2][web:18]  
- Database (e.g.: PostgreSQL / MongoDB, according to the main project stack).[web:8]  

## Responsibilities

- Consume messages from one or more Redis streams (e.g. `chat-messages`, `user-events`).[web:2][web:18]  
- Apply writing business rules and persist data in the database.  
- Ensure that only the worker does `INSERT`/`UPDATE`/`DELETE`, keeping the backend focused on WebSocket/reading API.

## ⚙️ Environment Variables

| Variable | Description | DefaultValue |
|----------------------------|-------------------------------------------------------------|-------------------------------------|
| `REDIS_URL` | Redis connection URL | `redis://redis:6379` |
| `REDIS_STREAMS` | List of streams that the worker will consume | `chat-messages,user-events` |
| `REDIS_CONSUMER_GROUP` | Name of the consumer group used by the worker | `piscord-workers` |
| `DB_URL` | Database Connection URL | `postgresql://user:pass@db:5432/app`|
| `DB_MAX_POOL_SIZE` | Maximum connection pool size | `10` |
| `QUARKUS_PROFILE` | Quarkus profile (`dev`, `prod`) | `prod` |
