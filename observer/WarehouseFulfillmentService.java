/**
 * CONCRETE OBSERVER 3: WarehouseFulfillmentService
 *
 * Handles physical order packaging, printing shipping labels, and handover to logistics couriers.
 */
public class WarehouseFulfillmentService implements OrderObserver {

    @Override
    public void onOrderEvent(OrderEvent event) {
        Order order = event.getOrder();

        if (event.getEventType() == OrderEventType.PAYMENT_RECEIVED) {
            System.out.printf("  [Warehouse] Generating warehouse pick list & packing slip for Order #%s...%n",
                    order.getOrderId());
            System.out.printf("  [Warehouse] Assigned to Packing Station #3 for items: %s%n",
                    order.getItems());
        } else if (event.getEventType() == OrderEventType.ORDER_PACKED) {
            System.out.printf("  [Warehouse] Package sealed. Printed shipping label and handed over to FedEx for Order #%s.%n",
                    order.getOrderId());
        }
    }

    @Override
    public String getObserverName() {
        return "WarehouseFulfillmentService";
    }
}
