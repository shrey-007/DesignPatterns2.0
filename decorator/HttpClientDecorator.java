/**
 * BASE DECORATOR (Abstract Class)
 *
 * Implements the component interface (HttpClient) and holds a reference
 * to a wrapped HttpClient object.
 *
 * Default behavior delegates all calls to the wrapped instance.
 * Subclasses override methods to intercept and augment behavior.
 */
public abstract class HttpClientDecorator implements HttpClient {

    // The wrapped component — can be BaseHttpClient or another decorator!
    protected final HttpClient delegate;

    public HttpClientDecorator(HttpClient delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Wrapped delegate HttpClient cannot be null");
        }
        this.delegate = delegate;
    }

    @Override
    public HttpResponse send(HttpRequest request) throws Exception {
        return delegate.send(request);
    }
}
