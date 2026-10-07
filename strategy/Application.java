/**
 * STRATEGY PATTERN — Client Code
 *
 * Demonstrates how the Strategy pattern lets you:
 * 1. Choose algorithms at runtime based on user input/config
 * 2. Swap algorithms without changing client code
 * 3. Benchmark different algorithms with the same interface
 * 4. Use the Null Object Strategy to avoid null checks
 */
public class Application {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     STRATEGY PATTERN — Compression Algorithms    ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // Sample data to compress
        String logData = "ERROR: Connection timeout at 2024-01-15 10:30:00. " +
            "ERROR: Connection timeout at 2024-01-15 10:30:01. " +
            "ERROR: Connection timeout at 2024-01-15 10:30:02. " +
            "ERROR: Connection timeout at 2024-01-15 10:30:03. " +
            "WARN: Retry attempt 1 of 5. WARN: Retry attempt 2 of 5. " +
            "WARN: Retry attempt 3 of 5. INFO: Connection restored.";

        String repeatedData = "AAAAAAAAABBBBBBBBBCCCCCCCCDDDDDDDDEEEEEEEE" +
            "AAAAAAAAABBBBBBBBBCCCCCCCCDDDDDDDDEEEEEEEE" +
            "AAAAAAAAABBBBBBBBBCCCCCCCCDDDDDDDDEEEEEEEE";

        // ─── Scenario 1: Basic usage with constructor injection ───

        System.out.println("\n━━━ Scenario 1: Basic Usage ━━━");

        FileProcessor processor = new FileProcessor("log-archiver", new ZipCompression());
        processor.processFile("server.log", logData);

        // ─── Scenario 2: Swap strategy at runtime ────────────────

        System.out.println("\n━━━ Scenario 2: Runtime Strategy Swap ━━━");

        // Same processor, different algorithm — just swap the strategy!
        processor.setCompressionStrategy(new GzipCompression());
        processor.processFile("server.log", logData);

        // ─── Scenario 3: Use Null Strategy (no compression) ──────

        System.out.println("\n━━━ Scenario 3: Null Strategy (no compression) ━━━");

        processor.setCompressionStrategy(new NoCompression());
        processor.processFile("config.json", logData);

        // ─── Scenario 4: RLE on repetitive data ─────────────────

        System.out.println("\n━━━ Scenario 4: RLE on Repetitive Data ━━━");

        processor.setCompressionStrategy(new RleCompression());
        processor.processFile("bitmap.raw", repeatedData);

        // ─── Scenario 5: Benchmark all strategies ────────────────

        System.out.println("\n━━━ Scenario 5: Benchmark All Strategies ━━━");

        processor.benchmark("access.log", logData,
            new ZipCompression(),
            new GzipCompression(),
            new RleCompression(),
            new NoCompression()
        );

        // ─── Scenario 6: Strategy selection from user input ──────

        System.out.println("\n━━━ Scenario 6: Strategy from Config/User Input ━━━");

        // In real code, this comes from CLI args, config file, or user selection
        String userChoice = "gzip";
        CompressionStrategy selectedStrategy = getStrategyFromConfig(userChoice);

        FileProcessor archiver = new FileProcessor("backup-tool", selectedStrategy);
        archiver.processFile("database-dump.sql", logData);
    }

    /**
     * Helper: Map a config string to a strategy object.
     * In real code, this might use a Factory to create strategies.
     * (Strategy + Factory patterns work great together!)
     */
    private static CompressionStrategy getStrategyFromConfig(String algorithmName) {
        switch (algorithmName.toLowerCase()) {
            case "zip":  return new ZipCompression();
            case "gzip": return new GzipCompression();
            case "rle":  return new RleCompression();
            case "none": return new NoCompression();
            default:
                System.out.printf("Unknown algorithm '%s', defaulting to GZIP%n", algorithmName);
                return new GzipCompression();
        }
    }
}
