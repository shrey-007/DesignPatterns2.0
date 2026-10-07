/**
 * BUILDER PATTERN — Client Code
 *
 * Demonstrates how the Builder pattern lets you:
 * 1. Create complex objects with readable, named parameters
 * 2. Use only the fields you need (everything else gets defaults)
 * 3. Get validation at build time (fail fast, before the object exists)
 * 4. Create immutable objects safely
 */
public class Application {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     BUILDER PATTERN — Database Configuration     ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // ─── Scenario 1: Production config — fully specified ──────

        System.out.println("\n━━━ Scenario 1: Full Production Config ━━━\n");

        DatabaseConfig productionConfig = new DatabaseConfig.Builder("postgresql", "db.prod.internal", "ecommerce")
            .port(5432)
            .credentials("app_user", "s3cur3P@ssw0rd!")
            .maxPoolSize(20)
            .connectionTimeout(5000)
            .queryTimeout(3000)
            .enableSSL(true)
            .maxRetries(5)
            .readReplica("db-replica.prod.internal")
            .timezone("America/New_York")
            .customProperty("applicationName", "ecommerce-api")
            .customProperty("reWriteBatchedInserts", "true")
            .build();

        System.out.println(productionConfig);

        // ─── Scenario 2: Minimal config — only required fields ───

        System.out.println("\n━━━ Scenario 2: Minimal Config (defaults for everything) ━━━\n");

        // Look how clean this is — just 3 required params!
        // Port auto-detected as 3306 (MySQL default), pool=5, timeout=30s, etc.
        DatabaseConfig devConfig = new DatabaseConfig.Builder("mysql", "localhost", "dev_db")
            .build();

        System.out.println(devConfig);

        // ─── Scenario 3: Testing config ──────────────────────────

        System.out.println("\n━━━ Scenario 3: Test Environment Config ━━━\n");

        DatabaseConfig testConfig = new DatabaseConfig.Builder("mysql", "test-db.staging", "test_ecommerce")
            .credentials("test_user", "test_password")
            .maxPoolSize(2)               // small pool for tests
            .connectionTimeout(1000)       // fast fail for tests
            .queryTimeout(500)
            .build();

        System.out.println(testConfig);

        // ─── Scenario 4: MongoDB config ──────────────────────────

        System.out.println("\n━━━ Scenario 4: MongoDB Config ━━━\n");

        DatabaseConfig mongoConfig = new DatabaseConfig.Builder("mongodb", "mongo-cluster.internal", "user_sessions")
            .port(27017)
            .credentials("mongo_admin", "m0ng0P@ss")
            .maxPoolSize(30)
            .enableSSL(true)
            .customProperty("readPreference", "secondaryPreferred")
            .customProperty("writeConcern", "majority")
            .build();

        System.out.println(mongoConfig);

        // ─── Scenario 5: Connection URL generation ───────────────

        System.out.println("\n━━━ Scenario 5: Generated Connection URLs ━━━\n");

        System.out.println("Production URL: " + productionConfig.getConnectionUrl());
        System.out.println("Dev URL:        " + devConfig.getConnectionUrl());
        System.out.println("Test URL:       " + testConfig.getConnectionUrl());
        System.out.println("Mongo URL:      " + mongoConfig.getConnectionUrl());

        // ─── Scenario 6: Validation catches errors at build time ─

        System.out.println("\n━━━ Scenario 6: Build-time Validation ━━━\n");

        try {
            DatabaseConfig badConfig = new DatabaseConfig.Builder("mysql", "localhost", "mydb")
                .maxPoolSize(500) // Invalid! Max is 100
                .build();
        } catch (IllegalStateException e) {
            System.out.println("✅ Caught validation error: " + e.getMessage());
        }

        try {
            DatabaseConfig badConfig = new DatabaseConfig.Builder("mysql", "", "mydb")
                .build();
        } catch (IllegalStateException e) {
            System.out.println("✅ Caught validation error: " + e.getMessage());
        }

        System.out.println("\n━━━ Done! ━━━");
    }
}
