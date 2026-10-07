/**
 * CLIENT APPLICATION
 *
 * Demonstrates the Adapter Pattern in action:
 * - We have 3 completely distinct third-party cloud SDKs (AWS S3, Google Cloud, Azure Blob).
 * - Each has different method signatures, data structures, and naming conventions.
 * - Through our Adapters (AwsS3Adapter, GoogleCloudStorageAdapter, AzureBlobStorageAdapter),
 *   our application code (DocumentStorageManager) works with ALL of them without changing a single line!
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        ADAPTER PATTERN: MULTI-CLOUD STORAGE ADAPTERS           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        String sampleDocument = "CONFIDENTIAL CONTRACT 2026: Design Patterns Architecture Review.";

        // ─────────────────────────────────────────────────────────────
        // 1. Using AWS S3 via AwsS3Adapter
        // ─────────────────────────────────────────────────────────────
        System.out.println("━━━ 1. Testing AWS S3 Adapter ━━━");
        AmazonS3Client rawAwsClient = new AmazonS3Client("AKIA_EXAMPLE_123", "us-east-1");
        CloudStorageService awsStorage = new AwsS3Adapter(rawAwsClient);

        DocumentStorageManager awsManager = new DocumentStorageManager(awsStorage, "company-documents-bucket");
        awsManager.archiveDocument("DOC-001", sampleDocument);
        String awsRetrieved = awsManager.fetchDocument("DOC-001");
        System.out.println("Content retrieved from AWS: \"" + awsRetrieved + "\"");
        System.out.println("Presigned URL: " + awsManager.getShareableLink("DOC-001", 3600));
        System.out.println();

        // ─────────────────────────────────────────────────────────────
        // 2. Swapping to Google Cloud Storage via GoogleCloudStorageAdapter
        // ─────────────────────────────────────────────────────────────
        System.out.println("━━━ 2. Testing Google Cloud Storage Adapter ━━━");
        GoogleCloudStorageClient rawGcpClient = new GoogleCloudStorageClient("enterprise-cloud-project-99");
        CloudStorageService gcpStorage = new GoogleCloudStorageAdapter(rawGcpClient);

        DocumentStorageManager gcpManager = new DocumentStorageManager(gcpStorage, "gcp-corporate-archive");
        gcpManager.archiveDocument("DOC-002", sampleDocument);
        String gcpRetrieved = gcpManager.fetchDocument("DOC-002");
        System.out.println("Content retrieved from GCP: \"" + gcpRetrieved + "\"");
        System.out.println("Presigned URL: " + gcpManager.getShareableLink("DOC-002", 1800));
        System.out.println();

        // ─────────────────────────────────────────────────────────────
        // 3. Swapping to Microsoft Azure Blob Storage via AzureBlobStorageAdapter
        // ─────────────────────────────────────────────────────────────
        System.out.println("━━━ 3. Testing Azure Blob Storage Adapter ━━━");
        AzureBlobClient rawAzureClient = new AzureBlobClient("DefaultEndpointsProtocol=https;AccountName=prod;AccountKey=xyz");
        CloudStorageService azureStorage = new AzureBlobStorageAdapter(rawAzureClient);

        DocumentStorageManager azureManager = new DocumentStorageManager(azureStorage, "legal-contracts-container");
        azureManager.archiveDocument("DOC-003", sampleDocument);
        String azureRetrieved = azureManager.fetchDocument("DOC-003");
        System.out.println("Content retrieved from Azure: \"" + azureRetrieved + "\"");
        System.out.println("SAS Token URL: " + azureManager.getShareableLink("DOC-003", 7200));
        System.out.println();

        // ─────────────────────────────────────────────────────────────
        // 4. Uniform Error Handling & Object Existence Checks
        // ─────────────────────────────────────────────────────────────
        System.out.println("━━━ 4. Uniform Exception Handling Across Providers ━━━");
        try {
            awsStorage.download("company-documents-bucket", "non-existent-file.txt");
        } catch (StorageException e) {
            System.out.printf("Caught unified domain exception from %s: [HTTP %d] %s%n",
                    e.getProvider(), e.getVendorStatusCode(), e.getMessage());
        }

        System.out.println("\n✓ Adapter Pattern demonstration complete.");
    }
}
