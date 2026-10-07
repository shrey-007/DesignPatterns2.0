/**
 * OBSERVER INTERFACE (Subscriber / Listener)
 *
 * All services interested in reacting to order lifecycle events must implement
 * this interface. The Subject notifies observers through onOrderEvent().
 */
public interface OrderObserver {

    /**
     * Callback invoked whenever a relevant order event is published.
     *
     * @param event the event payload containing order details and event metadata
     */
    void onOrderEvent(OrderEvent event);

    /**
     * Unique identifier or human-readable name of the observer.
     */
    String getObserverName();
}
