import java.util.HashMap;
import java.util.Map;

/**
 * CONCRETE OBSERVER 1: InventoryService
 *
 * Responds to:
 * - ORDER_CREATED: Reserves warehouse stock for the ordered items.
 * - ORDER_CANCELLED: Releases the previously reserved stock back to inventory.
 */
public class InventoryService implements OrderObserver {

    private final Map<String, Integer> stockLevels = new HashMap<>();

    public InventoryService() {
        stockLevels.put("MacBook Pro 16\"", 25);
        stockLevels.put("Mechanical Keyboard", 100);
        stockLevels.put("USB-C Hub", 150);
        stockLevels.put("Wireless Mouse", 80);
    }

    @Override
    public void onOrderEvent(OrderEvent event) {
        Order order = event.getOrder();

        if (event.getEventType() == OrderEventType.ORDER_CREATED) {
            System.out.printf("  [InventoryService] Reserving stock for Order #%s (%d items)...%n",
                    order.getOrderId(), order.getItems().size());
            for (String item : order.getItems()) {
                stockLevels.computeIfPresent(item, (k, current) -> current - 1);
                System.out.printf("    - Reserved 1x '%s'. Remaining stock: %d%n",
                        item, stockLevels.getOrDefault(item, 0));
            }
        } else if (event.getEventType() == OrderEventType.ORDER_CANCELLED) {
            System.out.printf("  [InventoryService] Releasing reserved stock for cancelled Order #%s...%n",
                    order.getOrderId());
            for (String item : order.getItems()) {
                stockLevels.computeIfPresent(item, (k, current) -> current + 1);
                System.out.printf("    - Restored 1x '%s'. Stock now: %d%n",
                        item, stockLevels.getOrDefault(item, 0));
            }
        }
    }

    @Override
    public String getObserverName() {
        return "InventoryService";
    }
}
