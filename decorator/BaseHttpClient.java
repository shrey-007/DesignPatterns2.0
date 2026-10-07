import java.util.HashMap;
import java.util.Map;

/**
 * CONCRETE COMPONENT
 *
 * The raw, undecorated HTTP client that performs the actual network request simulation.
 * It has no caching, no retries, no auth injection, and no logging.
 */
public class BaseHttpClient implements HttpClient {

    private int requestCounter = 0;

    @Override
    public HttpResponse send(HttpRequest request) throws Exception {
        requestCounter++;
        long startTime = System.currentTimeMillis();

        // Simulate network latency (20ms)
        Thread.sleep(20);

        // Simulate occasional backend server errors for testing retries
        if (request.getUrl().contains("/flaky-endpoint") && requestCounter % 3 != 0) {
            long latency = System.currentTimeMillis() - startTime;
            Map<String, String> errorHeaders = new HashMap<>();
            errorHeaders.put("Content-Type", "application/json");
            return new HttpResponse(503, "{\"error\": \"Service Unavailable - Temporary Spike\"}", errorHeaders, latency, false);
        }

        // Normal successful response simulation
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        responseHeaders.put("Server", "AntigravityGateway/2.0");

        String responsePayload = String.format(
                "{\"status\":\"success\",\"url\":\"%s\",\"method\":\"%s\",\"receivedAuth\":\"%s\"}",
                request.getUrl(),
                request.getMethod(),
                request.getHeader("Authorization") != null ? "PRESENT" : "MISSING"
        );

        long latency = System.currentTimeMillis() - startTime;
        return new HttpResponse(200, responsePayload, responseHeaders, latency, false);
    }
}
