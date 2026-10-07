import java.util.HashMap;

/**
 * CLIENT APPLICATION
 *
 * Demonstrates the power of the Decorator pattern:
 * 1. Base component can be used standalone.
 * 2. Behaviors (Auth, Logging, Cache, Retry, RateLimiter) can be wrapped/composed dynamically.
 * 3. Client treats the decorated stack as a simple HttpClient without knowing internal layers.
 */
public class Application {

    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          DECORATOR PATTERN: HTTP CLIENT MIDDLEWARE PIPELINE    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // ─────────────────────────────────────────────────────────────
        // 1. Raw Base Client (No Decorators)
        // ─────────────────────────────────────────────────────────────
        System.out.println("─── Stage 1: Barebones Base HTTP Client ───");
        HttpClient rawClient = new BaseHttpClient();
        HttpRequest req1 = new HttpRequest("GET", "https://api.github.com/zen", new HashMap<>(), null);
        HttpResponse resp1 = rawClient.send(req1);
        System.out.println("Result: " + resp1 + "\n");

        // ─────────────────────────────────────────────────────────────
        // 2. Base Client + Authentication + Logging
        // ─────────────────────────────────────────────────────────────
        System.out.println("─── Stage 2: Wrapped with Auth + Logging Decorators ───");
        HttpClient authenticatedClient = new LoggingDecorator(
                new AuthenticationDecorator(
                        new BaseHttpClient(),
                        "secret-jwt-token-xyz"
                ),
                "AUTH-CLIENT"
        );

        HttpRequest req2 = new HttpRequest("GET", "https://api.example.com/v1/user/profile", new HashMap<>(), null);
        HttpResponse resp2 = authenticatedClient.send(req2);
        System.out.println("Result: " + resp2 + "\n");

        // ─────────────────────────────────────────────────────────────
        // 3. Stacking Caching Decorator
        // ─────────────────────────────────────────────────────────────
        System.out.println("─── Stage 3: Adding In-Memory Caching Decorator ───");
        HttpClient cachedClient = new CachingDecorator(
                new LoggingDecorator(
                        new BaseHttpClient(),
                        "CACHE-DEMO"
                ),
                5000 // 5 seconds TTL
        );

        HttpRequest cacheableReq = new HttpRequest("GET", "https://api.example.com/v1/products/42", new HashMap<>(), null);

        System.out.println(">> First call (cache miss - executes network call):");
        cachedClient.send(cacheableReq);

        System.out.println("\n>> Second call with identical request (cache HIT - 0ms network latency):");
        HttpResponse cachedResp = cachedClient.send(cacheableReq);
        System.out.println("From Cache? " + cachedResp.isServedFromCache() + " | Body: " + cachedResp.getBody() + "\n");

        // ─────────────────────────────────────────────────────────────
        // 4. Stacking Retry Decorator for Flaky Endpoints
        // ─────────────────────────────────────────────────────────────
        System.out.println("─── Stage 4: Adding Retry Decorator for Transient Failures ───");
        HttpClient resilientClient = new LoggingDecorator(
                new RetryDecorator(
                        new BaseHttpClient(),
                        3, // max retries
                        40 // backoff ms
                ),
                "RESILIENT-CLIENT"
        );

        HttpRequest flakyReq = new HttpRequest("GET", "https://api.example.com/flaky-endpoint", new HashMap<>(), null);
        System.out.println("Calling flaky endpoint (will retry until successful 200 response):");
        HttpResponse retryResp = resilientClient.send(flakyReq);
        System.out.println("Final Result: " + retryResp + "\n");

        // ─────────────────────────────────────────────────────────────
        // 5. Full Production Stack: All Decorators Composed!
        // ─────────────────────────────────────────────────────────────
        System.out.println("─── Stage 5: Full Production Pipeline ───");
        System.out.println("Pipeline Composition: [Logging] -> [RateLimiter] -> [Cache] -> [Retry] -> [Auth] -> [BaseHttp]");

        HttpClient fullProductionClient =
                new LoggingDecorator(
                        new RateLimitingDecorator(
                                new CachingDecorator(
                                        new RetryDecorator(
                                                new AuthenticationDecorator(
                                                        new BaseHttpClient(),
                                                        "prod-api-key-998877"
                                                ),
                                                3,
                                                25
                                        ),
                                        10000
                                ),
                                5, // 5 requests per 1000ms
                                1000
                        ),
                        "PROD-GATEWAY"
                );

        HttpRequest prodReq = new HttpRequest("POST", "https://api.example.com/v1/orders", new HashMap<>(), "{\"item\":\"laptop\",\"qty\":1}");
        HttpResponse prodResp = fullProductionClient.send(prodReq);
        System.out.println("\nFinal Production Pipeline Response: " + prodResp);
        System.out.println("\n✓ Decorator Pattern demonstration complete.");
    }
}
