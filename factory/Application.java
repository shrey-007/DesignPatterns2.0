/**
 * FACTORY PATTERN — Client Code
 *
 * This is the code that USES the factory. Notice how it:
 * 1. Never imports MySQLConnection, PostgreSQLConnection, etc.
 * 2. Never calls 'new' on any concrete connection class
 * 3. Works entirely through the DatabaseConnection interface
 * 4. Can switch databases by changing ONE string ("mysql" → "postgresql")
 *
 * ROLE: Demonstrates how the factory decouples client code from concrete classes.
 */
public class Application {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║        FACTORY PATTERN — Database Connections    ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // ─── Scenario 1: Switch-based Factory ─────────────────────

        System.out.println("\n━━━ Scenario 1: Using Simple Factory (switch-based) ━━━\n");

        // In real code, this would come from application.properties or environment variable
        String configuredDb = "mysql";

        // The client doesn't know or care which concrete class is created
        DatabaseConnection connection = DatabaseConnectionFactory.createConnection(
            configuredDb, "localhost", 3306, "ecommerce_db"
        );

        // Use the connection through the interface — completely generic
        runDatabaseOperations(connection);

        // ─── Scenario 2: Easy to switch databases ─────────────────

        System.out.println("\n━━━ Scenario 2: Switching to PostgreSQL (change ONE string) ━━━\n");

        // Just change the type string — ZERO other code changes needed!
        DatabaseConnection pgConnection = DatabaseConnectionFactory.createConnection(
            "postgresql", "db.production.com", 5432, "analytics_db"
        );

        runDatabaseOperations(pgConnection);

        // ─── Scenario 3: NoSQL works too ──────────────────────────

        System.out.println("\n━━━ Scenario 3: MongoDB (completely different paradigm, same interface) ━━━\n");

        DatabaseConnection mongoConnection = DatabaseConnectionFactory.createConnection(
            "mongodb", "mongo-cluster.internal", 27017, "user_profiles"
        );

        runDatabaseOperations(mongoConnection);

        // ─── Scenario 4: Registry-based Factory ───────────────────

        System.out.println("\n━━━ Scenario 4: Using Registry-based Factory (extensible) ━━━\n");

        DatabaseConnection regConnection = DatabaseConnectionFactory.createFromRegistry(
            "sqlite", "localhost", 0, "local_cache"
        );

        runDatabaseOperations(regConnection);

        // ─── Scenario 5: Process multiple databases from config ───

        System.out.println("\n━━━ Scenario 5: Loop through multiple databases from config ━━━\n");

        // Imagine these come from a YAML config file
        String[][] databaseConfigs = {
            {"mysql",      "db1.internal",  "3306",  "orders"},
            {"postgresql", "db2.internal",  "5432",  "inventory"},
            {"mongodb",    "db3.internal",  "27017", "sessions"},
            {"sqlite",     "localhost",     "0",     "app_cache"},
        };

        for (String[] config : databaseConfigs) {
            String type = config[0];
            String host = config[1];
            int port    = Integer.parseInt(config[2]);
            String db   = config[3];

            // Factory creates the right connection — client loop is 100% generic
            DatabaseConnection conn = DatabaseConnectionFactory.createConnection(type, host, port, db);
            System.out.printf("Created: %s%n", conn.getConnectionInfo());
        }
    }

    /**
     * This method works with ANY database connection.
     * It doesn't know or care whether it's MySQL, PostgreSQL, MongoDB, or SQLite.
     * THIS is the power of the Factory pattern — client code is generic.
     */
    private static void runDatabaseOperations(DatabaseConnection connection) {
        System.out.println("Database type: " + connection.getDatabaseType());
        System.out.println("Connection info: " + connection.getConnectionInfo());

        connection.connect();
        String result = connection.executeQuery("SELECT * FROM users WHERE active = true");
        System.out.println("Result: " + result);
        connection.disconnect();
    }
}
