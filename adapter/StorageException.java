/**
 * DOMAIN EXCEPTION
 *
 * Unifies diverse proprietary exceptions thrown by different cloud SDKs
 * (AmazonClientException, StorageException from Google, BlobStorageException from Azure)
 * into a single unified business exception.
 */
public class StorageException extends RuntimeException {

    private final String provider;
    private final int vendorStatusCode;

    public StorageException(String provider, String message, int vendorStatusCode, Throwable cause) {
        super(String.format("[%s Error %d]: %s", provider, vendorStatusCode, message), cause);
        this.provider = provider;
        this.vendorStatusCode = vendorStatusCode;
    }

    public StorageException(String provider, String message) {
        this(provider, message, 500, null);
    }

    public String getProvider() {
        return provider;
    }

    public int getVendorStatusCode() {
        return vendorStatusCode;
    }
}
