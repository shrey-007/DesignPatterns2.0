import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ADAPTEE 3: Proprietary Azure Blob Storage SDK Client (Simulated)
 *
 * Notice the Azure-specific idioms:
 * - Uses Azure container names and blob names
 * - Uses BlockBlobItem model
 * - Method names: uploadBlockBlob(), downloadBlobStream(), deleteBlobIfExists()
 * - Returns SAS tokens for signed URLs
 */
public class AzureBlobClient {

    private final String connectionString;
    private final Map<String, byte[]> blobStore = new HashMap<>();
    private final Map<String, String> blobMimeTypes = new HashMap<>();

    public AzureBlobClient(String connectionString) {
        this.connectionString = connectionString;
    }

    public static class BlockBlobItem {
        public String containerName;
        public String blobName;
        public byte[] rawStream;
        public String contentType;
        public Date snapshotTimestamp;
    }

    public void uploadBlockBlob(String container, String blobName, byte[] binaryData, String mimeType) {
        String key = container + "#" + blobName;
        blobStore.put(key, binaryData);
        blobMimeTypes.put(key, mimeType != null ? mimeType : "application/octet-stream");
        System.out.println("  [Azure Blob SDK] Uploaded block blob to azure://" + container + "/" + blobName);
    }

    public BlockBlobItem downloadBlobStream(String container, String blobName) {
        String key = container + "#" + blobName;
        byte[] data = blobStore.get(key);
        if (data == null) {
            throw new RuntimeException("Azure 404: BlobNotFound - The specified blob does not exist.");
        }
        BlockBlobItem item = new BlockBlobItem();
        item.containerName = container;
        item.blobName = blobName;
        item.rawStream = data;
        item.contentType = blobMimeTypes.get(key);
        item.snapshotTimestamp = new Date();
        return item;
    }

    public boolean checkIfBlobExists(String container, String blobName) {
        return blobStore.containsKey(container + "#" + blobName);
    }

    public boolean deleteBlobIfExists(String container, String blobName) {
        String key = container + "#" + blobName;
        blobMimeTypes.remove(key);
        boolean removed = blobStore.remove(key) != null;
        System.out.println("  [Azure Blob SDK] Deleted blob azure://" + container + "/" + blobName + " => " + removed);
        return removed;
    }

    public String generateSasToken(String container, String blobName, int validitySeconds) {
        return String.format("https://azurestorage.blob.core.windows.net/%s/%s?sv=2024-01&se=%d&sig=azureSharedAccessSignature",
                container, blobName, System.currentTimeMillis() / 1000 + validitySeconds);
    }
}
