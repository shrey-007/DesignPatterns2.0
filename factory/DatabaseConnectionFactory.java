import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * FACTORY PATTERN — The Factory Class
 *
 * This is the HEART of the pattern. It centralizes all object creation logic
 * in one place. The client never calls 'new MySQLConnection()' directly —
 * it asks the factory for a connection, and the factory decides which
 * concrete class to instantiate.
 *
 * ROLE: Encapsulates object creation logic. Maps a discriminator (db type)
 *       to the correct concrete Product class.
 *
 * Two implementations shown:
 * 1. Switch-based (simple, most common)
 * 2. Registry-based (extensible, no modification needed to add types)
 */
public class DatabaseConnectionFactory {

    // ────────────────────────────────────────────────
    // APPROACH 1: Simple Factory (switch-based)
    //   Pros: Simple, easy to read
    //   Cons: Must modify this method to add new types
    // ────────────────────────────────────────────────

    public static DatabaseConnection createConnection(String dbType, String host, int port, String database) {

        switch (dbType.toLowerCase()) {
            case "mysql":
                return new MySQLConnection(host, port, database);

            case "postgresql":
            case "postgres":
                return new PostgreSQLConnection(host, port, database);

            case "mongodb":
            case "mongo":
                return new MongoDBConnection(host, port, database);

            case "sqlite":
                return new SQLiteConnection(host, port, database);

            default:
                throw new IllegalArgumentException(
                    "Unsupported database type: '" + dbType + "'. " +
                    "Supported types: mysql, postgresql, mongodb, sqlite"
                );
        }
    }

    // ────────────────────────────────────────────────
    // APPROACH 2: Registry-based Factory (map-based)
    //   Pros: Open for extension — add new types without modifying this class
    //   Cons: Slightly more complex setup
    // ────────────────────────────────────────────────

    // A functional interface for creating connections
    @FunctionalInterface
    interface ConnectionCreator {
        DatabaseConnection create(String host, int port, String database);
    }

    private static final Map<String, ConnectionCreator> registry = new HashMap<>();

    // Register built-in types
    static {
        registry.put("mysql",      MySQLConnection::new);
        registry.put("postgresql", PostgreSQLConnection::new);
        registry.put("postgres",   PostgreSQLConnection::new);
        registry.put("mongodb",    MongoDBConnection::new);
        registry.put("mongo",      MongoDBConnection::new);
        registry.put("sqlite",     SQLiteConnection::new);
    }

    /**
     * Register a new database type at runtime — no need to modify factory code!
     * Example: DatabaseConnectionFactory.register("redis", RedisConnection::new);
     */
    public static void register(String dbType, ConnectionCreator creator) {
        registry.put(dbType.toLowerCase(), creator);
        System.out.printf("[Factory] Registered new database type: '%s'%n", dbType);
    }

    /**
     * Create a connection using the registry (extensible approach).
     */
    public static DatabaseConnection createFromRegistry(String dbType, String host, int port, String database) {
        ConnectionCreator creator = registry.get(dbType.toLowerCase());

        if (creator == null) {
            throw new IllegalArgumentException(
                "Unsupported database type: '" + dbType + "'. " +
                "Registered types: " + registry.keySet()
            );
        }

        return creator.create(host, port, database);
    }
}
