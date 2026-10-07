/**
 * STRATEGY PATTERN — Concrete Strategy C: Run-Length Encoding (RLE)
 *
 * A simple, fast compression algorithm that works well on data with
 * many repeated characters (e.g., "AAABBBCCCC" → "3A3B4C").
 *
 * ROLE: Yet another algorithm implementation — demonstrates that even
 *       fundamentally different algorithms fit behind the same interface.
 */
public class RleCompression implements CompressionStrategy {

    @Override
    public CompressionResult compress(byte[] data, String fileName) {
        long startTime = System.currentTimeMillis();

        System.out.printf("[RLE] Compressing '%s' (%d bytes)...%n", fileName, data.length);
        System.out.println("[RLE] Using Run-Length Encoding — best for repetitive data");

        byte[] compressed = rleEncode(data);

        long timeTaken = System.currentTimeMillis() - startTime;
        String compressedName = fileName + getFileExtension();

        System.out.printf("[RLE] ✅ Compressed: %d → %d bytes%n", data.length, compressed.length);

        return new CompressionResult(
            getAlgorithmName(), fileName, compressedName,
            data.length, compressed.length, timeTaken
        );
    }

    @Override
    public byte[] decompress(byte[] compressedData) {
        System.out.println("[RLE] Decompressing with Run-Length Decoding...");
        return rleDecode(compressedData);
    }

    @Override
    public String getAlgorithmName() {
        return "RLE";
    }

    @Override
    public String getFileExtension() {
        return ".rle";
    }

    // ── Internal RLE logic ──────────────────────
    private byte[] rleEncode(byte[] data) {
        if (data.length == 0) return new byte[0];

        StringBuilder encoded = new StringBuilder();
        int count = 1;

        for (int i = 1; i <= data.length; i++) {
            if (i < data.length && data[i] == data[i - 1] && count < 255) {
                count++;
            } else {
                encoded.append(count);
                encoded.append((char) data[i - 1]);
                count = 1;
            }
        }
        return encoded.toString().getBytes();
    }

    private byte[] rleDecode(byte[] compressedData) {
        String encoded = new String(compressedData);
        StringBuilder decoded = new StringBuilder();

        int i = 0;
        while (i < encoded.length()) {
            // Parse the count (could be multi-digit)
            StringBuilder countStr = new StringBuilder();
            while (i < encoded.length() && Character.isDigit(encoded.charAt(i))) {
                countStr.append(encoded.charAt(i));
                i++;
            }
            int count = Integer.parseInt(countStr.toString());

            // The character to repeat
            if (i < encoded.length()) {
                char ch = encoded.charAt(i);
                for (int j = 0; j < count; j++) {
                    decoded.append(ch);
                }
                i++;
            }
        }
        return decoded.toString().getBytes();
    }
}
