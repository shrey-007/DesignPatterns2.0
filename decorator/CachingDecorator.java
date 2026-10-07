import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CONCRETE DECORATOR 3: CachingDecorator
 *
 * Implements an in-memory TTL cache for idempotent GET requests.
 * If a matching request has a valid cached response, it returns the cached
 * copy immediately without touching the downstream network client.
 */
public class CachingDecorator extends HttpClientDecorator {

    private static class CacheEntry {
        final HttpResponse response;
        final long expiresAtEpochMs;

        CacheEntry(HttpResponse response, long expiresAtEpochMs) {
            this.response = response;
            this.expiresAtEpochMs = expiresAtEpochMs;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAtEpochMs;
        }
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public CachingDecorator(HttpClient delegate, long ttlMillis) {
        super(delegate);
        this.ttlMillis = ttlMillis;
    }

    @Override
    public HttpResponse send(HttpRequest request) throws Exception {
        // Only cache idempotent GET requests
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return super.send(request);
        }

        String cacheKey = request.getUrl();
        CacheEntry entry = cache.get(cacheKey);

        if (entry != null && !entry.isExpired()) {
            // Return cached response wrapper indicating cache hit
            return new HttpResponse(
                    entry.response.getStatusCode(),
                    entry.response.getBody(),
                    entry.response.getHeaders(),
                    0, // 0ms latency for cache hit
                    true
            );
        }

        // Cache miss: execute wrapped client
        HttpResponse freshResponse = super.send(request);

        // Cache only successful responses
        if (freshResponse.isSuccessful()) {
            cache.put(cacheKey, new CacheEntry(freshResponse, System.currentTimeMillis() + ttlMillis));
        }

        return freshResponse;
    }

    public void clearCache() {
        cache.clear();
    }
}
