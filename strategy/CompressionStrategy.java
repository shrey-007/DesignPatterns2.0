/**
 * STRATEGY PATTERN — Strategy Interface
 *
 * This is the common interface for ALL compression algorithms.
 * The Context (FileProcessor) works with this interface — it doesn't know
 * or care whether it's using ZIP, GZIP, LZ4, or no compression at all.
 *
 * ROLE: Declares the algorithm contract that all concrete strategies implement.
 *
 * KEY DESIGN RULE:
 * The method signature must work for ALL algorithm variants.
 * If one variant needs extra data, either pass it through the method
 * or rethink your interface design.
 */
public interface CompressionStrategy {

    /**
     * Compress the given data and return a result containing the compressed data,
     * original size, compressed size, and the algorithm name.
     *
     * @param data       The raw data to compress.
     * @param fileName   The name of the file being compressed.
     * @return           A CompressionResult with details about the operation.
     */
    CompressionResult compress(byte[] data, String fileName);

    /**
     * Decompress previously compressed data back to its original form.
     *
     * @param compressedData  The compressed bytes.
     * @return                The original uncompressed data.
     */
    byte[] decompress(byte[] compressedData);

    /**
     * @return The name of this compression algorithm (e.g., "ZIP", "GZIP").
     */
    String getAlgorithmName();

    /**
     * @return The file extension used for compressed files (e.g., ".zip", ".gz").
     */
    String getFileExtension();
}
