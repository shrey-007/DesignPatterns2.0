/**
 * CONCRETE DECORATOR 4: RetryDecorator
 *
 * Implements transient fault tolerance. If downstream service fails with 5xx
 * or an exception, this decorator automatically retries with backoff up to maxRetries.
 */
public class RetryDecorator extends HttpClientDecorator {

    private final int maxRetries;
    private final long backoffMs;

    public RetryDecorator(HttpClient delegate, int maxRetries, long backoffMs) {
        super(delegate);
        this.maxRetries = maxRetries;
        this.backoffMs = backoffMs;
    }

    public RetryDecorator(HttpClient delegate) {
        this(delegate, 3, 50);
    }

    @Override
    public HttpResponse send(HttpRequest request) throws Exception {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxRetries) {
            attempts++;
            try {
                HttpResponse response = super.send(request);

                // If response is not a server error (5xx), return it
                if (response.getStatusCode() < 500) {
                    return response;
                }

                System.out.printf("  [RetryDecorator] Attempt %d failed with status %d. Retrying in %dms...%n",
                        attempts, response.getStatusCode(), backoffMs * attempts);

                if (attempts < maxRetries) {
                    Thread.sleep(backoffMs * attempts);
                } else {
                    return response; // Return the last 5xx response if all retries exhausted
                }

            } catch (Exception e) {
                lastException = e;
                System.out.printf("  [RetryDecorator] Attempt %d threw exception: %s. Retrying in %dms...%n",
                        attempts, e.getMessage(), backoffMs * attempts);

                if (attempts < maxRetries) {
                    Thread.sleep(backoffMs * attempts);
                }
            }
        }

        if (lastException != null) {
            throw lastException;
        }

        throw new IllegalStateException("Retry attempts exhausted");
    }
}
