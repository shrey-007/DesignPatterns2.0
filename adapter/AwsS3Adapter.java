import java.util.HashMap;
import java.util.Map;

/**
 * CONCRETE ADAPTER 1: AwsS3Adapter
 *
 * Implements the domain's CloudStorageService target interface by delegating
 * calls to the incompatible AmazonS3Client adaptee.
 *
 * It bridges:
 * 1. Method signature differences
 * 2. Parameter structure conversions (e.g. PutObjectRequest construction)
 * 3. Return type conversions (S3Object -> StorageObject)
 * 4. Error translation (AWS exceptions -> StorageException)
 */
public class AwsS3Adapter implements CloudStorageService {

    private final AmazonS3Client s3Client;

    public AwsS3Adapter(AmazonS3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void upload(String bucket, String key, byte[] content, String contentType, Map<String, String> metadata)
            throws StorageException {
        try {
            Map<String, String> meta = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
            if (contentType != null) {
                meta.put("Content-Type", contentType);
            }
            AmazonS3Client.PutObjectRequest request = new AmazonS3Client.PutObjectRequest(bucket, key, content, meta);
            s3Client.putObject(request);
        } catch (Exception e) {
            throw new StorageException("AWS-S3", "Failed to upload to S3: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public StorageObject download(String bucket, String key) throws StorageException {
        try {
            AmazonS3Client.S3Object s3Object = s3Client.getObject(bucket, key);
            return new StorageObject(
                    s3Object.bucketName,
                    s3Object.key,
                    s3Object.contentBytes,
                    s3Object.s3ContentType,
                    s3Object.lastModifiedDate,
                    s3Object.s3Metadata
            );
        } catch (Exception e) {
            throw new StorageException("AWS-S3", "Object not found or failed to fetch: " + e.getMessage(), 404, e);
        }
    }

    @Override
    public boolean exists(String bucket, String key) throws StorageException {
        try {
            return s3Client.doesObjectExist(bucket, key);
        } catch (Exception e) {
            throw new StorageException("AWS-S3", "Failed to check existence: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public void delete(String bucket, String key) throws StorageException {
        try {
            s3Client.deleteObject(bucket, key);
        } catch (Exception e) {
            throw new StorageException("AWS-S3", "Failed to delete from S3: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public String generateSignedUrl(String bucket, String key, int durationSeconds) throws StorageException {
        try {
            return s3Client.generatePreSignedUrl(bucket, key, durationSeconds);
        } catch (Exception e) {
            throw new StorageException("AWS-S3", "Failed to generate presigned URL: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public String getProviderName() {
        return "Amazon Web Services (S3)";
    }
}
