/**
 * STRATEGY PATTERN — Concrete Strategy D: No Compression (Null Strategy)
 *
 * This is the "do nothing" strategy — a VERY important pattern concept!
 * It avoids null checks in client code. Instead of:
 *    if (strategy != null) strategy.compress(data);
 * You can always call:
 *    strategy.compress(data);  // NoCompression just passes data through
 *
 * This is also known as the "Null Object Pattern."
 *
 * ROLE: A valid strategy that performs no transformation — eliminates null checks.
 */
public class NoCompression implements CompressionStrategy {

    @Override
    public CompressionResult compress(byte[] data, String fileName) {
        long startTime = System.currentTimeMillis();

        System.out.printf("[NONE] Storing '%s' without compression (%d bytes)...%n", fileName, data.length);
        System.out.println("[NONE] Data will be stored as-is");

        long timeTaken = System.currentTimeMillis() - startTime;

        return new CompressionResult(
            getAlgorithmName(), fileName, fileName,
            data.length, data.length, timeTaken
        );
    }

    @Override
    public byte[] decompress(byte[] compressedData) {
        // Nothing to decompress — data is already in original form
        return compressedData;
    }

    @Override
    public String getAlgorithmName() {
        return "NONE";
    }

    @Override
    public String getFileExtension() {
        return ""; // No extension added for uncompressed files
    }
}
