/**
 * CONCRETE DECORATOR 2: AuthenticationDecorator
 *
 * Automatically injects authentication credentials (e.g. Bearer Token or API Key)
 * into every outgoing request header before delegating.
 */
public class AuthenticationDecorator extends HttpClientDecorator {

    private final String apiToken;

    public AuthenticationDecorator(HttpClient delegate, String apiToken) {
        super(delegate);
        this.apiToken = apiToken;
    }

    @Override
    public HttpResponse send(HttpRequest request) throws Exception {
        // Intercept and enrich the request with the Auth header
        if (apiToken != null && !apiToken.isEmpty()) {
            request.setHeader("Authorization", "Bearer " + apiToken);
        }

        // Delegate to the wrapped client
        return super.send(request);
    }
}
