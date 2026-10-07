import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * CLIENT BUSINESS SERVICE
 *
 * This service handles enterprise document archiving, retrieval, and public link sharing.
 * Notice that it depends ONLY on CloudStorageService!
 * It knows NOTHING about AWS, Google, or Azure!
 */
public class DocumentStorageManager {

    private final CloudStorageService storageService;
    private final String defaultBucket;

    public DocumentStorageManager(CloudStorageService storageService, String defaultBucket) {
        this.storageService = storageService;
        this.defaultBucket = defaultBucket;
    }

    public void archiveDocument(String documentId, String textContent) {
        System.out.printf("[DocManager] Archiving document '%s' via %s...%n",
                documentId, storageService.getProviderName());

        byte[] data = textContent.getBytes(StandardCharsets.UTF_8);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("doc-id", documentId);
        metadata.put("system", "ERP-Archiver");

        storageService.upload(defaultBucket, "documents/" + documentId + ".txt", data, "text/plain", metadata);
        System.out.println("[DocManager] Successfully archived: " + documentId);
    }

    public String fetchDocument(String documentId) {
        String key = "documents/" + documentId + ".txt";
        System.out.printf("[DocManager] Fetching document '%s' from %s...%n",
                key, storageService.getProviderName());

        StorageObject obj = storageService.download(defaultBucket, key);
        return obj.getContentAsString();
    }

    public String getShareableLink(String documentId, int ttlSeconds) {
        String key = "documents/" + documentId + ".txt";
        return storageService.generateSignedUrl(defaultBucket, key, ttlSeconds);
    }
}
