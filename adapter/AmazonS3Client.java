import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ADAPTEE 1: Proprietary AWS SDK Client (Simulated)
 *
 * Notice the AWS-specific idioms:
 * - Uses nested PutObjectRequest / S3Object models
 * - Uses AWS S3 specific terminology ("s3.amazonaws.com", MD5 hashes, proprietary metadata format)
 * - Incompatible method signatures compared to CloudStorageService
 */
public class AmazonS3Client {

    private final String accessKey;
    private final String region;
    private final Map<String, byte[]> s3Storage = new HashMap<>();
    private final Map<String, String> s3ContentTypes = new HashMap<>();

    public AmazonS3Client(String accessKey, String region) {
        this.accessKey = accessKey;
        this.region = region;
    }

    public static class PutObjectRequest {
        public final String bucket;
        public final String key;
        public final byte[] bytes;
        public final Map<String, String> userMetadata;

        public PutObjectRequest(String bucket, String key, byte[] bytes, Map<String, String> userMetadata) {
            this.bucket = bucket;
            this.key = key;
            this.bytes = bytes;
            this.userMetadata = userMetadata;
        }
    }

    public static class S3Object {
        public String bucketName;
        public String key;
        public byte[] contentBytes;
        public String s3ContentType;
        public Date lastModifiedDate;
        public Map<String, String> s3Metadata;
    }

    public void putObject(PutObjectRequest request) {
        String path = request.bucket + "/" + request.key;
        s3Storage.put(path, request.bytes);
        s3ContentTypes.put(path, request.userMetadata != null ? request.userMetadata.get("Content-Type") : "application/octet-stream");
        System.out.println("  [AWS S3 SDK] Uploaded " + request.bytes.length + " bytes to s3://" + path + " in region " + region);
    }

    public S3Object getObject(String bucket, String key) {
        String path = bucket + "/" + key;
        byte[] data = s3Storage.get(path);
        if (data == null) {
            throw new RuntimeException("AWS 404: NoSuchKey - The specified key does not exist.");
        }
        S3Object obj = new S3Object();
        obj.bucketName = bucket;
        obj.key = key;
        obj.contentBytes = data;
        obj.s3ContentType = s3ContentTypes.getOrDefault(path, "application/octet-stream");
        obj.lastModifiedDate = new Date();
        obj.s3Metadata = new HashMap<>();
        return obj;
    }

    public boolean doesObjectExist(String bucket, String key) {
        return s3Storage.containsKey(bucket + "/" + key);
    }

    public void deleteObject(String bucket, String key) {
        s3Storage.remove(bucket + "/" + key);
        s3ContentTypes.remove(bucket + "/" + key);
        System.out.println("  [AWS S3 SDK] Deleted s3://" + bucket + "/" + key);
    }

    public String generatePreSignedUrl(String bucket, String key, int expirationInSeconds) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s?AWSAccessKeyId=%s&Expires=%d",
                bucket, region, key, accessKey, System.currentTimeMillis() / 1000 + expirationInSeconds);
    }
}
