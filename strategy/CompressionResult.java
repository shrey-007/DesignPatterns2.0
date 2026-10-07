/**
 * STRATEGY PATTERN — Result Data Class
 *
 * Holds the result of a compression operation.
 * All strategies produce this same result type, making them interchangeable.
 */
public class CompressionResult {

    private String algorithmUsed;
    private String originalFileName;
    private String compressedFileName;
    private long originalSize;
    private long compressedSize;
    private double compressionRatio;
    private long timeTakenMs;

    public CompressionResult(String algorithmUsed, String originalFileName,
                              String compressedFileName, long originalSize,
                              long compressedSize, long timeTakenMs) {
        this.algorithmUsed = algorithmUsed;
        this.originalFileName = originalFileName;
        this.compressedFileName = compressedFileName;
        this.originalSize = originalSize;
        this.compressedSize = compressedSize;
        this.compressionRatio = originalSize > 0 ? (1.0 - (double) compressedSize / originalSize) * 100 : 0;
        this.timeTakenMs = timeTakenMs;
    }

    // ── Getters ──────────────────────────────────
    public String getAlgorithmUsed()     { return algorithmUsed; }
    public String getOriginalFileName()  { return originalFileName; }
    public String getCompressedFileName(){ return compressedFileName; }
    public long getOriginalSize()        { return originalSize; }
    public long getCompressedSize()      { return compressedSize; }
    public double getCompressionRatio()  { return compressionRatio; }
    public long getTimeTakenMs()         { return timeTakenMs; }

    @Override
    public String toString() {
        return String.format(
            "CompressionResult {\n" +
            "  algorithm     = %s\n" +
            "  originalFile  = %s (%d bytes)\n" +
            "  compressedFile= %s (%d bytes)\n" +
            "  ratio         = %.1f%% reduction\n" +
            "  time          = %d ms\n" +
            "}",
            algorithmUsed, originalFileName, originalSize,
            compressedFileName, compressedSize,
            compressionRatio, timeTakenMs
        );
    }
}
