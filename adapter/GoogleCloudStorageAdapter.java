import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * CONCRETE ADAPTER 2: GoogleCloudStorageAdapter
 *
 * Adapts GoogleCloudStorageClient to the unified CloudStorageService interface.
 * Converts our standard calls into GCP's BlobId and BlobInfo models.
 */
public class GoogleCloudStorageAdapter implements CloudStorageService {

    private final GoogleCloudStorageClient gcpClient;

    public GoogleCloudStorageAdapter(GoogleCloudStorageClient gcpClient) {
        this.gcpClient = gcpClient;
    }

    @Override
    public void upload(String bucket, String key, byte[] content, String contentType, Map<String, String> metadata)
            throws StorageException {
        try {
            GoogleCloudStorageClient.BlobId blobId = new GoogleCloudStorageClient.BlobId(bucket, key);
            gcpClient.createBlob(blobId, content, contentType);
        } catch (Exception e) {
            throw new StorageException("Google-Cloud-Storage", "Failed to upload blob: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public StorageObject download(String bucket, String key) throws StorageException {
        try {
            GoogleCloudStorageClient.BlobId blobId = new GoogleCloudStorageClient.BlobId(bucket, key);
            GoogleCloudStorageClient.BlobInfo blobInfo = gcpClient.readBlob(blobId);

            return new StorageObject(
                    blobInfo.blobId.bucket,
                    blobInfo.blobId.name,
                    blobInfo.payload,
                    blobInfo.mediaType,
                    new Date(blobInfo.updateTime),
                    new HashMap<>()
            );
        } catch (Exception e) {
            throw new StorageException("Google-Cloud-Storage", "Failed to download blob: " + e.getMessage(), 404, e);
        }
    }

    @Override
    public boolean exists(String bucket, String key) throws StorageException {
        try {
            GoogleCloudStorageClient.BlobId blobId = new GoogleCloudStorageClient.BlobId(bucket, key);
            return gcpClient.blobExists(blobId);
        } catch (Exception e) {
            throw new StorageException("Google-Cloud-Storage", "Failed to check blob: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public void delete(String bucket, String key) throws StorageException {
        try {
            GoogleCloudStorageClient.BlobId blobId = new GoogleCloudStorageClient.BlobId(bucket, key);
            gcpClient.deleteBlob(blobId);
        } catch (Exception e) {
            throw new StorageException("Google-Cloud-Storage", "Failed to delete blob: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public String generateSignedUrl(String bucket, String key, int durationSeconds) throws StorageException {
        try {
            GoogleCloudStorageClient.BlobId blobId = new GoogleCloudStorageClient.BlobId(bucket, key);
            return gcpClient.createSignedDownloadUrl(blobId, durationSeconds);
        } catch (Exception e) {
            throw new StorageException("Google-Cloud-Storage", "Failed to generate signed URL: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public String getProviderName() {
        return "Google Cloud Storage (GCS)";
    }
}
