import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;

/**
 * STRATEGY PATTERN — Concrete Strategy B: GZIP Compression
 *
 * Implements the CompressionStrategy interface with GZIP algorithm.
 * Common in web servers (Content-Encoding: gzip) and log file compression.
 * Generally better compression than ZIP for single streams.
 *
 * ROLE: Another specific algorithm implementation — same interface, different behavior.
 */
public class GzipCompression implements CompressionStrategy {

    @Override
    public CompressionResult compress(byte[] data, String fileName) {
        long startTime = System.currentTimeMillis();

        System.out.printf("[GZIP] Compressing '%s' (%d bytes)...%n", fileName, data.length);
        System.out.println("[GZIP] Using GNU zip algorithm — optimized for single-stream data");

        // Actual GZIP compression using Java's built-in GZIPOutputStream
        byte[] compressed = gzipCompress(data);

        long timeTaken = System.currentTimeMillis() - startTime;
        String compressedName = fileName + getFileExtension();

        System.out.printf("[GZIP] ✅ Compressed: %d → %d bytes%n", data.length, compressed.length);

        return new CompressionResult(
            getAlgorithmName(), fileName, compressedName,
            data.length, compressed.length, timeTaken
        );
    }

    @Override
    public byte[] decompress(byte[] compressedData) {
        System.out.println("[GZIP] Decompressing with GZIP stream...");
        return gzipDecompress(compressedData);
    }

    @Override
    public String getAlgorithmName() {
        return "GZIP";
    }

    @Override
    public String getFileExtension() {
        return ".gz";
    }

    // ── Internal compression logic ──────────────
    private byte[] gzipCompress(byte[] data) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(data.length);
            GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
            gzipStream.write(data);
            gzipStream.close();
            return byteStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("GZIP compression failed", e);
        }
    }

    private byte[] gzipDecompress(byte[] compressedData) {
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedData);
            GZIPInputStream gzipStream = new GZIPInputStream(byteStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            gzipStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("GZIP decompression failed", e);
        }
    }
}
