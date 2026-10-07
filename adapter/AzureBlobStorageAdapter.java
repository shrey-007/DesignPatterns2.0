import java.util.HashMap;
import java.util.Map;

/**
 * CONCRETE ADAPTER 3: AzureBlobStorageAdapter
 *
 * Adapts AzureBlobClient to the unified CloudStorageService interface.
 */
public class AzureBlobStorageAdapter implements CloudStorageService {

    private final AzureBlobClient azureClient;

    public AzureBlobStorageAdapter(AzureBlobClient azureClient) {
        this.azureClient = azureClient;
    }

    @Override
    public void upload(String bucket, String key, byte[] content, String contentType, Map<String, String> metadata)
            throws StorageException {
        try {
            // Azure uses "container" for bucket and "blobName" for key
            azureClient.uploadBlockBlob(bucket, key, content, contentType);
        } catch (Exception e) {
            throw new StorageException("Azure-Blob", "Failed to upload to Azure: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public StorageObject download(String bucket, String key) throws StorageException {
        try {
            AzureBlobClient.BlockBlobItem blob = azureClient.downloadBlobStream(bucket, key);
            return new StorageObject(
                    blob.containerName,
                    blob.blobName,
                    blob.rawStream,
                    blob.contentType,
                    blob.snapshotTimestamp,
                    new HashMap<>()
            );
        } catch (Exception e) {
            throw new StorageException("Azure-Blob", "Failed to download from Azure: " + e.getMessage(), 404, e);
        }
    }

    @Override
    public boolean exists(String bucket, String key) throws StorageException {
        try {
            return azureClient.checkIfBlobExists(bucket, key);
        } catch (Exception e) {
            throw new StorageException("Azure-Blob", "Failed to check Azure blob: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public void delete(String bucket, String key) throws StorageException {
        try {
            azureClient.deleteBlobIfExists(bucket, key);
        } catch (Exception e) {
            throw new StorageException("Azure-Blob", "Failed to delete Azure blob: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public String generateSignedUrl(String bucket, String key, int durationSeconds) throws StorageException {
        try {
            return azureClient.generateSasToken(bucket, key, durationSeconds);
        } catch (Exception e) {
            throw new StorageException("Azure-Blob", "Failed to generate SAS token: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public String getProviderName() {
        return "Microsoft Azure Blob Storage";
    }
}
