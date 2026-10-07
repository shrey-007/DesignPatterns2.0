import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ADAPTEE 2: Proprietary Google Cloud Storage Client (Simulated)
 *
 * Notice the Google-specific idioms:
 * - Uses BlobId, BlobInfo, storage.googleapis.com
 * - Method names: createBlob(), readBlob(), deleteBlob()
 * - Throws proprietary GcpStorageException
 */
public class GoogleCloudStorageClient {

    private final String projectId;
    private final Map<String, byte[]> gcpStorage = new HashMap<>();
    private final Map<String, String> gcpMediaTypes = new HashMap<>();

    public GoogleCloudStorageClient(String projectId) {
        this.projectId = projectId;
    }

    public static class BlobId {
        public final String bucket;
        public final String name;

        public BlobId(String bucket, String name) {
            this.bucket = bucket;
            this.name = name;
        }
    }

    public static class BlobInfo {
        public BlobId blobId;
        public byte[] payload;
        public String mediaType;
        public long updateTime;
    }

    public void createBlob(BlobId id, byte[] content, String mediaType) {
        String path = id.bucket + "/" + id.name;
        gcpStorage.put(path, content);
        gcpMediaTypes.put(path, mediaType != null ? mediaType : "application/octet-stream");
        System.out.println("  [Google Cloud SDK] Wrote blob to gs://" + path + " on project " + projectId);
    }

    public BlobInfo readBlob(BlobId id) {
        String path = id.bucket + "/" + id.name;
        byte[] content = gcpStorage.get(path);
        if (content == null) {
            throw new RuntimeException("GCP 404: Blob does not exist in bucket " + id.bucket);
        }
        BlobInfo info = new BlobInfo();
        info.blobId = id;
        info.payload = content;
        info.mediaType = gcpMediaTypes.get(path);
        info.updateTime = System.currentTimeMillis();
        return info;
    }

    public boolean blobExists(BlobId id) {
        return gcpStorage.containsKey(id.bucket + "/" + id.name);
    }

    public boolean deleteBlob(BlobId id) {
        String path = id.bucket + "/" + id.name;
        gcpMediaTypes.remove(path);
        boolean removed = gcpStorage.remove(path) != null;
        System.out.println("  [Google Cloud SDK] Deleted gs://" + path + " => " + removed);
        return removed;
    }

    public String createSignedDownloadUrl(BlobId id, int ttlSeconds) {
        return String.format("https://storage.googleapis.com/%s/%s?project=%s&expires=%d",
                id.bucket, id.name, projectId, System.currentTimeMillis() / 1000 + ttlSeconds);
    }
}
