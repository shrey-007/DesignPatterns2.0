/**
 * CONCRETE OBSERVER 5: AuditLogService
 *
 * Implements an immutable compliance and security audit log.
 * Registered globally to record every state change.
 */
public class AuditLogService implements OrderObserver {

    @Override
    public void onOrderEvent(OrderEvent event) {
        Order order = event.getOrder();
        System.out.printf("  [AuditLog] [TIMESTAMP: %s] EVENT=%s | ORDER_ID=%s | USER=%s | AMOUNT=%.2f | DESC=\"%s\"%n",
                event.getTimestamp(), event.getEventType(), order.getOrderId(),
                order.getCustomerId(), order.getTotalAmount(), event.getDescription());
    }

    @Override
    public String getObserverName() {
        return "AuditLogService";
    }
}
