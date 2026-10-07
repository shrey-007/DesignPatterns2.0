/**
 * CONCRETE DECORATOR 1: LoggingDecorator
 *
 * Adds structured request/response logging and latency tracking around the HTTP call.
 */
public class LoggingDecorator extends HttpClientDecorator {

    private final String clientTag;

    public LoggingDecorator(HttpClient delegate, String clientTag) {
        super(delegate);
        this.clientTag = clientTag;
    }

    public LoggingDecorator(HttpClient delegate) {
        this(delegate, "HTTP-CLIENT");
    }

    @Override
    public HttpResponse send(HttpRequest request) throws Exception {
        System.out.printf("[%s] >>> Sending: %s %s%n", clientTag, request.getMethod(), request.getUrl());
        if (!request.getHeaders().isEmpty()) {
            System.out.printf("[%s]     Headers: %s%n", clientTag, request.getHeaders().keySet());
        }

        long start = System.currentTimeMillis();
        try {
            HttpResponse response = super.send(request);
            long totalDuration = System.currentTimeMillis() - start;

            System.out.printf("[%s] <<< Received [%d] in %dms (Cached: %b)%n",
                    clientTag, response.getStatusCode(), totalDuration, response.isServedFromCache());
            return response;
        } catch (Exception e) {
            long totalDuration = System.currentTimeMillis() - start;
            System.out.printf("[%s] <<< FAILED after %dms: %s%n", clientTag, totalDuration, e.getMessage());
            throw e;
        }
    }
}
