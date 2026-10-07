/**
 * FACTORY PATTERN — Product Interface
 *
 * This is the common interface that ALL database connections must implement.
 * The factory returns objects of this type. Client code programs against
 * this interface — it never knows which concrete connection it's using.
 *
 * ROLE: Defines the contract (what a database connection can do).
 */
public interface DatabaseConnection {

    /**
     * Open a connection to the database.
     */
    void connect();

    /**
     * Execute a query and return results as a formatted string.
     */
    String executeQuery(String query);

    /**
     * Close the connection and release resources.
     */
    void disconnect();

    /**
     * @return The name of the database engine (e.g., "MySQL", "PostgreSQL").
     */
    String getDatabaseType();

    /**
     * @return Connection details for logging/debugging.
     */
    String getConnectionInfo();
}
