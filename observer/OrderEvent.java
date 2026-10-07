import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * EVENT OBJECT (Payload)
 *
 * Encapsulates the event details sent from the Subject to all registered Observers.
 */
public class OrderEvent {
    private final OrderEventType eventType;
    private final Order order;
    private final Date timestamp;
    private final String description;
    private final Map<String, String> metadata;

    public OrderEvent(OrderEventType eventType, Order order, String description) {
        this.eventType = eventType;
        this.order = order;
        this.timestamp = new Date();
        this.description = description;
        this.metadata = new HashMap<>();
    }

    public OrderEventType getEventType() {
        return eventType;
    }

    public Order getOrder() {
        return order;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("OrderEvent[%s | Order: %s | At: %s | Info: %s]",
                eventType, order.getOrderId(), timestamp, description);
    }
}
