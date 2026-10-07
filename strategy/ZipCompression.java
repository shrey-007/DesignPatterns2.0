import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * STRATEGY PATTERN — Concrete Strategy A: ZIP Compression
 *
 * Implements the CompressionStrategy interface with ZIP/Deflate algorithm.
 * Good balance between speed and compression ratio.
 *
 * ROLE: One specific algorithm implementation.
 */
public class ZipCompression implements CompressionStrategy {

    @Override
    public CompressionResult compress(byte[] data, String fileName) {
        long startTime = System.currentTimeMillis();

        System.out.printf("[ZIP] Compressing '%s' (%d bytes)...%n", fileName, data.length);
        System.out.println("[ZIP] Using DEFLATE algorithm with default compression level");

        // Actual ZIP compression using Java's built-in Deflater
        byte[] compressed = deflateData(data);

        long timeTaken = System.currentTimeMillis() - startTime;
        String compressedName = fileName + getFileExtension();

        System.out.printf("[ZIP] ✅ Compressed: %d → %d bytes%n", data.length, compressed.length);

        return new CompressionResult(
            getAlgorithmName(), fileName, compressedName,
            data.length, compressed.length, timeTaken
        );
    }

    @Override
    public byte[] decompress(byte[] compressedData) {
        System.out.println("[ZIP] Decompressing with INFLATE...");
        return inflateData(compressedData);
    }

    @Override
    public String getAlgorithmName() {
        return "ZIP";
    }

    @Override
    public String getFileExtension() {
        return ".zip";
    }

    // ── Internal compression logic ──────────────
    private byte[] deflateData(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        deflater.end();
        return outputStream.toByteArray();
    }

    private byte[] inflateData(byte[] compressedData) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(compressedData);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedData.length);
            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            inflater.end();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("ZIP decompression failed", e);
        }
    }
}
