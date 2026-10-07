import java.util.Map;

/**
 * TARGET INTERFACE
 *
 * This is the contract defined and owned by our application domain.
 * Our client application code calls ONLY this interface, remaining
 * completely oblivious to AWS, Google Cloud, or Azure vendor SDK details.
 */
public interface CloudStorageService {

    /**
     * Uploads file bytes to a specified cloud bucket.
     */
    void upload(String bucket, String key, byte[] content, String contentType, Map<String, String> metadata)
            throws StorageException;

    /**
     * Downloads an object from the specified cloud bucket.
     */
    StorageObject download(String bucket, String key)
            throws StorageException;

    /**
     * Checks if the object exists in the storage bucket.
     */
    boolean exists(String bucket, String key)
            throws StorageException;

    /**
     * Deletes the specified object from the storage bucket.
     */
    void delete(String bucket, String key)
            throws StorageException;

    /**
     * Generates a temporary pre-signed URL for direct browser access.
     */
    String generateSignedUrl(String bucket, String key, int durationSeconds)
            throws StorageException;

    /**
     * Returns the name of the underlying cloud provider (e.g., AWS, GCP, Azure).
     */
    String getProviderName();
}
