/**
 * FACTORY PATTERN — Concrete Product B
 *
 * PostgreSQL-specific implementation of DatabaseConnection.
 * Different connection logic, different features than MySQL.
 *
 * ROLE: Implements the Product interface with PostgreSQL-specific behavior.
 */
public class PostgreSQLConnection implements DatabaseConnection {

    private String host;
    private int port;
    private String database;
    private String schema;
    private boolean connected;

    public PostgreSQLConnection(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.schema = "public"; // PostgreSQL default schema
        this.connected = false;
    }

    @Override
    public void connect() {
        // In real code: DriverManager.getConnection("jdbc:postgresql://host:port/db", props)
        System.out.printf("[PostgreSQL] Connecting to %s:%d/%s ...%n", host, port, database);
        System.out.printf("[PostgreSQL] Using schema: %s%n", schema);
        System.out.println("[PostgreSQL] Enabling SSL/TLS connection");
        System.out.println("[PostgreSQL] Setting search_path and timezone");
        this.connected = true;
        System.out.println("[PostgreSQL] ✅ Connection established");
    }

    @Override
    public String executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to PostgreSQL! Call connect() first.");
        }
        System.out.printf("[PostgreSQL] Executing on schema '%s': %s%n", schema, query);
        // PostgreSQL supports advanced features like CTEs, window functions, etc.
        return String.format("[PostgreSQL Result] Query '%s' returned 38 rows from %s.%s",
            query, database, schema);
    }

    @Override
    public void disconnect() {
        System.out.println("[PostgreSQL] Closing connection...");
        System.out.println("[PostgreSQL] Terminating backend process");
        this.connected = false;
        System.out.println("[PostgreSQL] ❌ Disconnected");
    }

    @Override
    public String getDatabaseType() {
        return "PostgreSQL";
    }

    @Override
    public String getConnectionInfo() {
        return String.format("PostgreSQL @ %s:%d/%s (schema=%s) [connected=%b]",
            host, port, database, schema, connected);
    }
}
