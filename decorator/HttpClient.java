/**
 * COMPONENT INTERFACE
 *
 * Defines the contract implemented by both the raw HTTP client
 * and all decorators wrapping it.
 */
public interface HttpClient {
    /**
     * Executes an HTTP request and returns the resulting HttpResponse.
     *
     * @param request the HTTP request details
     * @return the HTTP response
     * @throws Exception if network or execution error occurs
     */
    HttpResponse send(HttpRequest request) throws Exception;
}
