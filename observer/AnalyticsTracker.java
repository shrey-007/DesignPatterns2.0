/**
 * CONCRETE OBSERVER 4: AnalyticsTracker
 *
 * Tracks sales revenue, gross merchandise value (GMV), and cancellation rates.
 */
public class AnalyticsTracker implements OrderObserver {

    private double totalGrossRevenue = 0.0;
    private int completedOrdersCount = 0;
    private int cancelledOrdersCount = 0;

    @Override
    public void onOrderEvent(OrderEvent event) {
        Order order = event.getOrder();

        if (event.getEventType() == OrderEventType.PAYMENT_RECEIVED) {
            totalGrossRevenue += order.getTotalAmount();
            completedOrdersCount++;
            System.out.printf("  [Analytics] GMV recorded: +$%.2f. Total Revenue to date: $%.2f (Orders: %d)%n",
                    order.getTotalAmount(), totalGrossRevenue, completedOrdersCount);
        } else if (event.getEventType() == OrderEventType.ORDER_CANCELLED) {
            totalGrossRevenue -= order.getTotalAmount();
            cancelledOrdersCount++;
            System.out.printf("  [Analytics] GMV adjusted: -$%.2f. Total Revenue: $%.2f (Cancellations: %d)%n",
                    order.getTotalAmount(), totalGrossRevenue, cancelledOrdersCount);
        }
    }

    @Override
    public String getObserverName() {
        return "AnalyticsTracker";
    }
}
