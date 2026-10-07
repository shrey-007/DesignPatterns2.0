/**
 * FACTORY PATTERN — Concrete Product D
 *
 * SQLite-specific implementation of DatabaseConnection.
 * Unlike MySQL/PostgreSQL, SQLite is a file-based embedded database
 * with no server process. Very different internals, same interface.
 *
 * ROLE: Implements the Product interface with SQLite-specific behavior.
 */
public class SQLiteConnection implements DatabaseConnection {

    private String filePath;
    private boolean connected;

    public SQLiteConnection(String filePath) {
        this.filePath = filePath;
        this.connected = false;
    }

    // Constructor that matches the (host, port, database) signature
    // SQLite ignores host/port since it's file-based
    public SQLiteConnection(String host, int port, String database) {
        this.filePath = database + ".db";
        this.connected = false;
    }

    @Override
    public void connect() {
        // In real code: DriverManager.getConnection("jdbc:sqlite:" + filePath)
        System.out.printf("[SQLite] Opening database file: %s%n", filePath);
        System.out.println("[SQLite] No server needed — embedded database");
        System.out.println("[SQLite] Enabling WAL (Write-Ahead Logging) mode");
        this.connected = true;
        System.out.println("[SQLite] ✅ Database file opened");
    }

    @Override
    public String executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("SQLite database file not opened! Call connect() first.");
        }
        System.out.printf("[SQLite] Executing on %s: %s%n", filePath, query);
        return String.format("[SQLite Result] Query '%s' returned 7 rows from %s", query, filePath);
    }

    @Override
    public void disconnect() {
        System.out.println("[SQLite] Closing database file...");
        System.out.println("[SQLite] Flushing WAL to main database file");
        this.connected = false;
        System.out.println("[SQLite] ❌ Database file closed");
    }

    @Override
    public String getDatabaseType() {
        return "SQLite";
    }

    @Override
    public String getConnectionInfo() {
        return String.format("SQLite @ file://%s [connected=%b]", filePath, connected);
    }
}
