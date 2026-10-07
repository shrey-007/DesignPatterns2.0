import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Request Model.
 * Represents an immutable outgoing HTTP request with headers, method, URL, and body.
 */
public class HttpRequest {
    private final String method;
    private final String url;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method, String url, Map<String, String> headers, String body) {
        this.method = method;
        this.url = url;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
        this.body = body != null ? body : "";
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return method + " " + url;
    }
}
