/**
 * STRATEGY PATTERN — Context Class
 *
 * The Context is the class that HAS a strategy but doesn't IMPLEMENT the algorithm.
 * It delegates the compression work to whichever strategy is currently set.
 *
 * KEY RULES:
 * 1. The Context holds a reference to the Strategy interface (not a concrete class)
 * 2. The Context delegates algorithm work to the strategy
 * 3. The strategy can be swapped at runtime via setStrategy()
 * 4. The Context handles everything EXCEPT the algorithm itself (file I/O, logging, etc.)
 *
 * ROLE: Uses the strategy — doesn't implement it.
 */
public class FileProcessor {

    // ── The Strategy reference — this is the key field! ──────────
    private CompressionStrategy compressionStrategy;

    private String processorName;

    /**
     * Constructor injection — set the initial strategy.
     * Prefer this when the strategy is known at creation time.
     */
    public FileProcessor(String processorName, CompressionStrategy strategy) {
        this.processorName = processorName;
        this.compressionStrategy = strategy;
        System.out.printf("[FileProcessor '%s'] Initialized with %s strategy%n",
            processorName, strategy.getAlgorithmName());
    }

    /**
     * Setter injection — swap the strategy at runtime.
     * This is what makes Strategy powerful — change behavior without
     * changing the FileProcessor class!
     */
    public void setCompressionStrategy(CompressionStrategy strategy) {
        System.out.printf("[FileProcessor '%s'] Switching strategy: %s → %s%n",
            processorName,
            this.compressionStrategy.getAlgorithmName(),
            strategy.getAlgorithmName());
        this.compressionStrategy = strategy;
    }

    /**
     * Process a file — the SAME method works with ANY compression strategy.
     * The FileProcessor doesn't know or care how compression works.
     * It just delegates to this.compressionStrategy.compress()
     */
    public CompressionResult processFile(String fileName, String content) {
        System.out.println("\n┌─────────────────────────────────────────────┐");
        System.out.printf("│ Processing: %-32s│%n", fileName);
        System.out.printf("│ Strategy: %-33s│%n", compressionStrategy.getAlgorithmName());
        System.out.println("└─────────────────────────────────────────────┘");

        byte[] data = content.getBytes();

        // ── DELEGATE to the strategy ──────────────
        // This is the core of the Strategy pattern:
        // The FileProcessor calls strategy.compress() without knowing
        // which algorithm is actually running!
        CompressionResult result = compressionStrategy.compress(data, fileName);

        System.out.println(result);
        return result;
    }

    /**
     * Compare all given strategies on the same data.
     * Demonstrates runtime swapping beautifully.
     */
    public void benchmark(String fileName, String content, CompressionStrategy... strategies) {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║          COMPRESSION BENCHMARK                ║");
        System.out.printf("║  File: %-40s║%n", fileName);
        System.out.printf("║  Size: %-40s║%n", content.length() + " bytes");
        System.out.println("╚═══════════════════════════════════════════════╝");

        for (CompressionStrategy strategy : strategies) {
            // Swap strategy at runtime — this is the magic!
            setCompressionStrategy(strategy);
            processFile(fileName, content);
        }
    }
}
