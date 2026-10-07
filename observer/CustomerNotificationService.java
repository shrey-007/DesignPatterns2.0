/**
 * CONCRETE OBSERVER 2: CustomerNotificationService
 *
 * Sends personalized notifications (Email & SMS) to the customer as the order progresses.
 */
public class CustomerNotificationService implements OrderObserver {

    @Override
    public void onOrderEvent(OrderEvent event) {
        Order order = event.getOrder();

        switch (event.getEventType()) {
            case ORDER_CREATED:
                System.out.printf("  [CustomerNotify] 📧 Email sent to <%s>: Order #%s confirmed! Total: $%.2f%n",
                        order.getCustomerEmail(), order.getOrderId(), order.getTotalAmount());
                break;
            case PAYMENT_RECEIVED:
                System.out.printf("  [CustomerNotify] 📱 SMS sent to <%s>: Payment confirmed for Order #%s.%n",
                        order.getCustomerPhone(), order.getOrderId());
                break;
            case ORDER_SHIPPED:
                System.out.printf("  [CustomerNotify] 📧 Email sent to <%s>: Order #%s has shipped! Tracking: TRK-%s%n",
                        order.getCustomerEmail(), order.getOrderId(), order.getOrderId().hashCode());
                break;
            case ORDER_DELIVERED:
                System.out.printf("  [CustomerNotify] 📱 SMS sent to <%s>: Order #%s has been successfully delivered.%n",
                        order.getCustomerPhone(), order.getOrderId());
                break;
            case ORDER_CANCELLED:
                System.out.printf("  [CustomerNotify] 📧 Email sent to <%s>: Order #%s has been cancelled. Refund initiated.%n",
                        order.getCustomerEmail(), order.getOrderId());
                break;
            default:
                break;
        }
    }

    @Override
    public String getObserverName() {
        return "CustomerNotificationService";
    }
}
