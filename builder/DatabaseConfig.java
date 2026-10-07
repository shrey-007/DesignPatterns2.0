import java.util.*;

/**
 * BUILDER PATTERN — Product + Builder
 *
 * The DatabaseConfig is an IMMUTABLE object with many fields (some required,
 * some optional). Without the builder, you'd need a constructor with 12+ parameters:
 *
 *   new DatabaseConfig("mysql", "db.prod.com", 3306, "appdb", "admin", "secret",
 *                      10, 30000, true, 3, "read-replica.com", "UTC");
 *   // Which parameter is which?! Unreadable!
 *
 * With the builder:
 *   new DatabaseConfig.Builder("mysql", "db.prod.com", "appdb")
 *       .port(3306)
 *       .credentials("admin", "secret")
 *       .maxPoolSize(10)
 *       .timeout(30000)
 *       .enableSSL(true)
 *       .build();
 *   // Crystal clear!
 *
 * STRUCTURE:
 * - Product (DatabaseConfig) — immutable, private constructor
 * - Builder (static inner class) — mutable, fluent setters, builds the Product
 */
public class DatabaseConfig {

    // ─────────────────────────────────────────────────────
    // PRODUCT FIELDS — all private final (immutable!)
    // ─────────────────────────────────────────────────────
    private final String databaseType;     // required
    private final String host;             // required
    private final String databaseName;     // required
    private final int port;                // optional, has default
    private final String username;         // optional
    private final String password;         // optional
    private final int maxPoolSize;         // optional, default 5
    private final int connectionTimeoutMs; // optional, default 30000
    private final int queryTimeoutMs;      // optional, default 10000
    private final boolean sslEnabled;      // optional, default false
    private final int maxRetries;          // optional, default 3
    private final String readReplicaHost;  // optional, can be null
    private final String timezone;         // optional, default "UTC"
    private final Map<String, String> customProperties; // optional, extra config

    // ─────────────────────────────────────────────────────
    // PRIVATE CONSTRUCTOR — only the Builder can call this!
    // ─────────────────────────────────────────────────────
    private DatabaseConfig(Builder builder) {
        this.databaseType       = builder.databaseType;
        this.host               = builder.host;
        this.databaseName       = builder.databaseName;
        this.port               = builder.port;
        this.username           = builder.username;
        this.password           = builder.password;
        this.maxPoolSize        = builder.maxPoolSize;
        this.connectionTimeoutMs= builder.connectionTimeoutMs;
        this.queryTimeoutMs     = builder.queryTimeoutMs;
        this.sslEnabled         = builder.sslEnabled;
        this.maxRetries         = builder.maxRetries;
        this.readReplicaHost    = builder.readReplicaHost;
        this.timezone           = builder.timezone;
        this.customProperties   = Collections.unmodifiableMap(new HashMap<>(builder.customProperties));
    }

    // ─────────────────────────────────────────────────────
    // GETTERS ONLY — no setters! Object is immutable.
    // ─────────────────────────────────────────────────────
    public String getDatabaseType()       { return databaseType; }
    public String getHost()               { return host; }
    public String getDatabaseName()       { return databaseName; }
    public int getPort()                  { return port; }
    public String getUsername()            { return username; }
    public String getPassword()           { return password; }
    public int getMaxPoolSize()           { return maxPoolSize; }
    public int getConnectionTimeoutMs()   { return connectionTimeoutMs; }
    public int getQueryTimeoutMs()        { return queryTimeoutMs; }
    public boolean isSslEnabled()         { return sslEnabled; }
    public int getMaxRetries()            { return maxRetries; }
    public String getReadReplicaHost()    { return readReplicaHost; }
    public String getTimezone()           { return timezone; }
    public Map<String, String> getCustomProperties() { return customProperties; }

    /**
     * Generates a JDBC-style connection URL from the config.
     */
    public String getConnectionUrl() {
        return String.format("jdbc:%s://%s:%d/%s?ssl=%b&timezone=%s",
            databaseType, host, port, databaseName, sslEnabled, timezone);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════╗\n");
        sb.append("║         DATABASE CONFIGURATION               ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Type:           %-28s ║%n", databaseType));
        sb.append(String.format("║ Host:           %-28s ║%n", host));
        sb.append(String.format("║ Port:           %-28d ║%n", port));
        sb.append(String.format("║ Database:       %-28s ║%n", databaseName));
        sb.append(String.format("║ Username:       %-28s ║%n", username != null ? username : "(none)"));
        sb.append(String.format("║ Password:       %-28s ║%n", password != null ? "********" : "(none)"));
        sb.append(String.format("║ Pool Size:      %-28d ║%n", maxPoolSize));
        sb.append(String.format("║ Conn Timeout:   %-28s ║%n", connectionTimeoutMs + " ms"));
        sb.append(String.format("║ Query Timeout:  %-28s ║%n", queryTimeoutMs + " ms"));
        sb.append(String.format("║ SSL:            %-28b ║%n", sslEnabled));
        sb.append(String.format("║ Max Retries:    %-28d ║%n", maxRetries));
        sb.append(String.format("║ Read Replica:   %-28s ║%n", readReplicaHost != null ? readReplicaHost : "(none)"));
        sb.append(String.format("║ Timezone:       %-28s ║%n", timezone));
        if (!customProperties.isEmpty()) {
            sb.append(String.format("║ Custom Props:   %-28s ║%n", customProperties));
        }
        sb.append("║                                              ║\n");
        sb.append(String.format("║ URL: %-40s ║%n", getConnectionUrl()));
        sb.append("╚══════════════════════════════════════════════╝");
        return sb.toString();
    }


    // ═════════════════════════════════════════════════════
    //  THE BUILDER — static inner class
    // ═════════════════════════════════════════════════════
    public static class Builder {

        // Required fields — set in the Builder constructor
        private final String databaseType;
        private final String host;
        private final String databaseName;

        // Optional fields — have sensible defaults
        private int port = -1;                   // -1 means "use default for db type"
        private String username = null;
        private String password = null;
        private int maxPoolSize = 5;
        private int connectionTimeoutMs = 30_000; // 30 seconds
        private int queryTimeoutMs = 10_000;      // 10 seconds
        private boolean sslEnabled = false;
        private int maxRetries = 3;
        private String readReplicaHost = null;
        private String timezone = "UTC";
        private Map<String, String> customProperties = new HashMap<>();

        /**
         * BUILDER CONSTRUCTOR — only REQUIRED parameters go here.
         * Everything else uses fluent setter methods with defaults.
         */
        public Builder(String databaseType, String host, String databaseName) {
            this.databaseType = databaseType;
            this.host = host;
            this.databaseName = databaseName;
        }

        // ── Fluent setters — each returns 'this' for chaining ──

        public Builder port(int port) {
            this.port = port;
            return this; // ← MUST return 'this' for chaining!
        }

        public Builder credentials(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        public Builder maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public Builder connectionTimeout(int timeoutMs) {
            this.connectionTimeoutMs = timeoutMs;
            return this;
        }

        public Builder queryTimeout(int timeoutMs) {
            this.queryTimeoutMs = timeoutMs;
            return this;
        }

        public Builder enableSSL(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder readReplica(String readReplicaHost) {
            this.readReplicaHost = readReplicaHost;
            return this;
        }

        public Builder timezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public Builder customProperty(String key, String value) {
            this.customProperties.put(key, value);
            return this;
        }

        /**
         * BUILD METHOD — validates and creates the immutable Product.
         * This is the ONLY place that calls the private constructor.
         * All validation happens HERE, before the object is created.
         */
        public DatabaseConfig build() {
            // ── Validation ───────────────────────────────
            if (databaseType == null || databaseType.isEmpty()) {
                throw new IllegalStateException("Database type is required");
            }
            if (host == null || host.isEmpty()) {
                throw new IllegalStateException("Host is required");
            }
            if (databaseName == null || databaseName.isEmpty()) {
                throw new IllegalStateException("Database name is required");
            }
            if (maxPoolSize < 1 || maxPoolSize > 100) {
                throw new IllegalStateException("Pool size must be between 1 and 100, got: " + maxPoolSize);
            }
            if (connectionTimeoutMs < 0) {
                throw new IllegalStateException("Connection timeout must be non-negative");
            }
            if (maxRetries < 0) {
                throw new IllegalStateException("Max retries must be non-negative");
            }

            // Auto-detect port from database type if not explicitly set
            if (port == -1) {
                port = getDefaultPort(databaseType);
            }

            // If SSL is enabled, recommend setting credentials
            if (sslEnabled && (username == null || password == null)) {
                System.out.println("[Builder WARN] SSL is enabled but no credentials set");
            }

            return new DatabaseConfig(this);
        }

        /**
         * Returns default port for known database types.
         */
        private int getDefaultPort(String dbType) {
            switch (dbType.toLowerCase()) {
                case "mysql":      return 3306;
                case "postgresql":
                case "postgres":   return 5432;
                case "mongodb":    return 27017;
                case "redis":      return 6379;
                case "sqlite":     return 0;     // file-based, no port
                default:           return 3306;   // fallback
            }
        }
    }
}
