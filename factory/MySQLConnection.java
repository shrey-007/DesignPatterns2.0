/**
 * FACTORY PATTERN — Concrete Product A
 *
 * One specific implementation of DatabaseConnection.
 * Contains MySQL-specific connection logic, query syntax, etc.
 *
 * ROLE: Implements the Product interface with MySQL-specific behavior.
 */
public class MySQLConnection implements DatabaseConnection {

    private String host;
    private int port;
    private String database;
    private boolean connected;

    public MySQLConnection(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.connected = false;
    }

    @Override
    public void connect() {
        // In real code: DriverManager.getConnection("jdbc:mysql://host:port/db", user, pass)
        System.out.printf("[MySQL] Connecting to %s:%d/%s ...%n", host, port, database);
        System.out.println("[MySQL] Using InnoDB storage engine");
        System.out.println("[MySQL] Setting character set to utf8mb4");
        this.connected = true;
        System.out.println("[MySQL] ✅ Connection established");
    }

    @Override
    public String executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to MySQL! Call connect() first.");
        }
        System.out.printf("[MySQL] Executing: %s%n", query);
        // Simulate MySQL-specific query execution
        return String.format("[MySQL Result] Query '%s' returned 42 rows from %s", query, database);
    }

    @Override
    public void disconnect() {
        System.out.println("[MySQL] Closing connection...");
        System.out.println("[MySQL] Releasing connection back to pool");
        this.connected = false;
        System.out.println("[MySQL] ❌ Disconnected");
    }

    @Override
    public String getDatabaseType() {
        return "MySQL";
    }

    @Override
    public String getConnectionInfo() {
        return String.format("MySQL @ %s:%d/%s [connected=%b]", host, port, database, connected);
    }
}
