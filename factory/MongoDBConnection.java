/**
 * FACTORY PATTERN — Concrete Product C
 *
 * MongoDB-specific implementation of DatabaseConnection.
 * Completely different paradigm — NoSQL, document-based, no SQL queries.
 * Yet it still implements the same interface!
 *
 * ROLE: Implements the Product interface with MongoDB-specific behavior.
 */
public class MongoDBConnection implements DatabaseConnection {

    private String host;
    private int port;
    private String database;
    private boolean connected;

    public MongoDBConnection(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.connected = false;
    }

    @Override
    public void connect() {
        // In real code: MongoClient.create("mongodb://host:port")
        System.out.printf("[MongoDB] Connecting to mongodb://%s:%d ...%n", host, port);
        System.out.printf("[MongoDB] Selecting database: %s%n", database);
        System.out.println("[MongoDB] Discovering replica set members...");
        System.out.println("[MongoDB] Write concern: MAJORITY");
        this.connected = true;
        System.out.println("[MongoDB] ✅ Connection established");
    }

    @Override
    public String executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to MongoDB! Call connect() first.");
        }
        // MongoDB uses document queries, not SQL — but adapter makes it work
        System.out.printf("[MongoDB] Executing document query: %s%n", query);
        System.out.println("[MongoDB] Scanning collection with IXSCAN index strategy");
        return String.format("[MongoDB Result] Query '%s' returned 15 documents from %s", query, database);
    }

    @Override
    public void disconnect() {
        System.out.println("[MongoDB] Closing connection...");
        System.out.println("[MongoDB] Closing all cursors and releasing sockets");
        this.connected = false;
        System.out.println("[MongoDB] ❌ Disconnected");
    }

    @Override
    public String getDatabaseType() {
        return "MongoDB";
    }

    @Override
    public String getConnectionInfo() {
        return String.format("MongoDB @ %s:%d/%s [connected=%b]", host, port, database, connected);
    }
}
