import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CONCRETE SUBJECT / EVENT BROKER
 *
 * Maintains observer registrations and broadcasts events.
 * Uses thread-safe collections (CopyOnWriteArrayList) to prevent ConcurrentModificationException
 * if an observer attaches or detaches during an event broadcast.
 */
public class OrderEventManager implements OrderSubject {

    // Global listeners subscribed to every event
    private final List<OrderObserver> globalObservers = new CopyOnWriteArrayList<>();

    // Event-specific listeners
    private final Map<OrderEventType, List<OrderObserver>> typedObservers = new EnumMap<>(OrderEventType.class);

    public OrderEventManager() {
        for (OrderEventType type : OrderEventType.values()) {
            typedObservers.put(type, new CopyOnWriteArrayList<>());
        }
    }

    @Override
    public void attach(OrderObserver observer) {
        if (observer != null && !globalObservers.contains(observer)) {
            globalObservers.add(observer);
            System.out.printf("  [EventBroker] Attached '%s' globally.%n", observer.getObserverName());
        }
    }

    @Override
    public void attach(OrderEventType eventType, OrderObserver observer) {
        if (observer != null && eventType != null) {
            List<OrderObserver> list = typedObservers.get(eventType);
            if (!list.contains(observer)) {
                list.add(observer);
                System.out.printf("  [EventBroker] Attached '%s' to event [%s].%n", observer.getObserverName(), eventType);
            }
        }
    }

    @Override
    public void detach(OrderObserver observer) {
        if (observer != null) {
            globalObservers.remove(observer);
            for (List<OrderObserver> list : typedObservers.values()) {
                list.remove(observer);
            }
            System.out.printf("  [EventBroker] Detached '%s' from all subscriptions.%n", observer.getObserverName());
        }
    }

    @Override
    public void detach(OrderEventType eventType, OrderObserver observer) {
        if (observer != null && eventType != null) {
            typedObservers.get(eventType).remove(observer);
            System.out.printf("  [EventBroker] Detached '%s' from event [%s].%n", observer.getObserverName(), eventType);
        }
    }

    @Override
    public void notifyObservers(OrderEvent event) {
        System.out.printf("%n>>> [EventBroker] Broadcasting Event: [%s] for Order #%s <<<%n",
                event.getEventType(), event.getOrder().getOrderId());

        // Notify global observers first
        for (OrderObserver observer : globalObservers) {
            try {
                observer.onOrderEvent(event);
            } catch (Exception e) {
                System.err.printf("  [!] Error in observer %s: %s%n", observer.getObserverName(), e.getMessage());
            }
        }

        // Notify event-specific observers
        List<OrderObserver> specificList = typedObservers.get(event.getEventType());
        if (specificList != null) {
            for (OrderObserver observer : specificList) {
                // Prevent duplicate invocation if observer was already registered globally
                if (!globalObservers.contains(observer)) {
                    try {
                        observer.onOrderEvent(event);
                    } catch (Exception e) {
                        System.err.printf("  [!] Error in observer %s: %s%n", observer.getObserverName(), e.getMessage());
                    }
                }
            }
        }
    }
}
