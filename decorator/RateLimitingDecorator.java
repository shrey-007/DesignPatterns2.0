/**
 * CONCRETE DECORATOR 5: RateLimitingDecorator
 *
 * Implements client-side throttling to ensure the client doesn't breach
 * downstream rate limits (e.g., maximum requests per time window).
 */
public class RateLimitingDecorator extends HttpClientDecorator {

    private final int maxRequestsPerWindow;
    private final long windowDurationMs;
    private int requestCount;
    private long windowStartTime;

    public RateLimitingDecorator(HttpClient delegate, int maxRequestsPerWindow, long windowDurationMs) {
        super(delegate);
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowDurationMs = windowDurationMs;
        this.requestCount = 0;
        this.windowStartTime = System.currentTimeMillis();
    }

    @Override
    public synchronized HttpResponse send(HttpRequest request) throws Exception {
        long now = System.currentTimeMillis();

        // Reset sliding window if expired
        if (now - windowStartTime > windowDurationMs) {
            windowStartTime = now;
            requestCount = 0;
        }

        if (requestCount >= maxRequestsPerWindow) {
            long waitTime = windowDurationMs - (now - windowStartTime);
            if (waitTime > 0) {
                System.out.printf("  [RateLimiter] Rate limit exceeded (%d/%d). Throttling for %dms...%n",
                        requestCount, maxRequestsPerWindow, waitTime);
                Thread.sleep(waitTime);
                windowStartTime = System.currentTimeMillis();
                requestCount = 0;
            }
        }

        requestCount++;
        return super.send(request);
    }
}
