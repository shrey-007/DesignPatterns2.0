/**
 * SUBJECT INTERFACE (Publisher / Observable)
 *
 * Defines methods for attaching, detaching, and notifying observers.
 * Supports both global observers and event-specific observers.
 */
public interface OrderSubject {

    /**
     * Subscribes an observer to ALL order events.
     */
    void attach(OrderObserver observer);

    /**
     * Subscribes an observer to a SPECIFIC event type.
     */
    void attach(OrderEventType eventType, OrderObserver observer);

    /**
     * Unsubscribes an observer completely.
     */
    void detach(OrderObserver observer);

    /**
     * Unsubscribes an observer from a specific event type.
     */
    void detach(OrderEventType eventType, OrderObserver observer);

    /**
     * Dispatches an event to all interested observers.
     */
    void notifyObservers(OrderEvent event);
}
