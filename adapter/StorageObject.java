import java.util.Date;
import java.util.Map;

/**
 * DOMAIN MODEL
 *
 * Represents a standard, vendor-agnostic object stored in the cloud.
 */
public class StorageObject {
    private final String bucketName;
    private final String objectKey;
    private final byte[] data;
    private final String contentType;
    private final long sizeBytes;
    private final Date lastModified;
    private final Map<String, String> userMetadata;

    public StorageObject(String bucketName, String objectKey, byte[] data, String contentType,
                         Date lastModified, Map<String, String> userMetadata) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.data = data;
        this.contentType = contentType != null ? contentType : "application/octet-stream";
        this.sizeBytes = data != null ? data.length : 0;
        this.lastModified = lastModified != null ? lastModified : new Date();
        this.userMetadata = userMetadata;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    public String getContentAsString() {
        return data != null ? new String(data) : "";
    }

    @Override
    public String toString() {
        return String.format("StorageObject[bucket=%s, key=%s, size=%d bytes, type=%s, lastModified=%s]",
                bucketName, objectKey, sizeBytes, contentType, lastModified);
    }
}
