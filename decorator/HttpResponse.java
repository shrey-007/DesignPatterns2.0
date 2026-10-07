import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Response Model.
 * Encapsulates the response status code, body, response headers, latency, and cache origin.
 */
public class HttpResponse {
    private final int statusCode;
    private final String body;
    private final Map<String, String> headers;
    private final long latencyMs;
    private final boolean servedFromCache;

    public HttpResponse(int statusCode, String body, Map<String, String> headers, long latencyMs, boolean servedFromCache) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
        this.latencyMs = latencyMs;
        this.servedFromCache = servedFromCache;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public boolean isServedFromCache() {
        return servedFromCache;
    }

    @Override
    public String toString() {
        return String.format("[HTTP %d] (Latency: %dms, FromCache: %s) => %s",
                statusCode, latencyMs, servedFromCache, body);
    }
}
