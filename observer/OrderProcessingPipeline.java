/**
 * BUSINESS SERVICE
 *
 * Coordinates business actions on orders and triggers events via the OrderSubject.
 * Notice: OrderProcessingPipeline has ZERO direct dependency on Inventory, Notification,
 * Warehouse, Analytics, or AuditLog classes! It is completely decoupled.
 */
public class OrderProcessingPipeline {

    private final OrderSubject eventBroker;

    public OrderProcessingPipeline(OrderSubject eventBroker) {
        this.eventBroker = eventBroker;
    }

    public void createOrder(Order order) {
        order.setStatus("CREATED");
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_CREATED, order, "Customer placed order");
        eventBroker.notifyObservers(event);
    }

    public void processPayment(Order order, String transactionId) {
        order.setStatus("PAID");
        OrderEvent event = new OrderEvent(OrderEventType.PAYMENT_RECEIVED, order, "Payment captured successfully");
        event.addMetadata("transactionId", transactionId);
        eventBroker.notifyObservers(event);
    }

    public void packOrder(Order order) {
        order.setStatus("PACKED");
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_PACKED, order, "Items packed in box");
        eventBroker.notifyObservers(event);
    }

    public void shipOrder(Order order, String trackingNumber) {
        order.setStatus("SHIPPED");
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_SHIPPED, order, "Package handed to courier");
        event.addMetadata("trackingNumber", trackingNumber);
        eventBroker.notifyObservers(event);
    }

    public void deliverOrder(Order order) {
        order.setStatus("DELIVERED");
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_DELIVERED, order, "Package delivered to customer address");
        eventBroker.notifyObservers(event);
    }

    public void cancelOrder(Order order, String reason) {
        order.setStatus("CANCELLED");
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_CANCELLED, order, "Cancelled: " + reason);
        eventBroker.notifyObservers(event);
    }
}
