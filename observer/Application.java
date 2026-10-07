import java.util.Arrays;

/**
 * CLIENT APPLICATION
 *
 * Demonstrates the Observer Pattern in action:
 * 1. Observers can subscribe globally or selectively to specific events.
 * 2. OrderProcessingPipeline emits events without knowing about concrete listeners.
 * 3. Adding or removing listeners happens dynamically at runtime without affecting the core workflow.
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          OBSERVER PATTERN: E-COMMERCE ORDER EVENT BROKER       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // 1. Create the Subject / Event Broker
        OrderEventManager broker = new OrderEventManager();

        // 2. Create the Concrete Observers
        AuditLogService auditLog = new AuditLogService();
        InventoryService inventory = new InventoryService();
        CustomerNotificationService notifications = new CustomerNotificationService();
        WarehouseFulfillmentService warehouse = new WarehouseFulfillmentService();
        AnalyticsTracker analytics = new AnalyticsTracker();

        // 3. Register subscriptions
        System.out.println("━━━ Registering Observers ━━━");
        // AuditLog subscribes globally to every event
        broker.attach(auditLog);

        // Inventory only cares about ORDER_CREATED and ORDER_CANCELLED
        broker.attach(OrderEventType.ORDER_CREATED, inventory);
        broker.attach(OrderEventType.ORDER_CANCELLED, inventory);

        // Notifications listen to customer-relevant events
        broker.attach(OrderEventType.ORDER_CREATED, notifications);
        broker.attach(OrderEventType.PAYMENT_RECEIVED, notifications);
        broker.attach(OrderEventType.ORDER_SHIPPED, notifications);
        broker.attach(OrderEventType.ORDER_DELIVERED, notifications);
        broker.attach(OrderEventType.ORDER_CANCELLED, notifications);

        // Warehouse only listens to PAYMENT_RECEIVED and ORDER_PACKED
        broker.attach(OrderEventType.PAYMENT_RECEIVED, warehouse);
        broker.attach(OrderEventType.ORDER_PACKED, warehouse);

        // Analytics tracks revenue on PAYMENT_RECEIVED and ORDER_CANCELLED
        broker.attach(OrderEventType.PAYMENT_RECEIVED, analytics);
        broker.attach(OrderEventType.ORDER_CANCELLED, analytics);

        // 4. Initialize the business pipeline
        OrderProcessingPipeline pipeline = new OrderProcessingPipeline(broker);

        // ─────────────────────────────────────────────────────────────
        // SCENARIO 1: Happy Path - Complete Order Lifecycle
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n════════════════════════════════════════════════════════════════");
        System.out.println(" SCENARIO 1: Full Happy Path Order Lifecycle");
        System.out.println("════════════════════════════════════════════════════════════════");

        Order order1 = new Order(
                "ORD-9801",
                "CUST-101",
                "alice@example.com",
                "+1-555-0199",
                Arrays.asList("MacBook Pro 16\"", "Mechanical Keyboard", "USB-C Hub"),
                2749.99
        );

        pipeline.createOrder(order1);
        pipeline.processPayment(order1, "TXN_STRIPE_778899");
        pipeline.packOrder(order1);
        pipeline.shipOrder(order1, "1Z9999999999999999");
        pipeline.deliverOrder(order1);

        // ─────────────────────────────────────────────────────────────
        // SCENARIO 2: Order Cancellation & Runtime Observer Detach
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n════════════════════════════════════════════════════════════════");
        System.out.println(" SCENARIO 2: Order Cancellation & Dynamic Unsubscription");
        System.out.println("════════════════════════════════════════════════════════════════");

        Order order2 = new Order(
                "ORD-9802",
                "CUST-202",
                "bob@example.com",
                "+1-555-0244",
                Arrays.asList("Wireless Mouse"),
                79.99
        );

        pipeline.createOrder(order2);
        pipeline.processPayment(order2, "TXN_PAYPAL_334455");

        // Dynamically unsubscribe customer notifications for this customer
        System.out.println("\n>> Detaching customer notification service dynamically...");
        broker.detach(OrderEventType.ORDER_CANCELLED, notifications);

        // Cancel order — inventory releases stock, analytics adjusts GMV, audit logs, but customer gets no SMS/Email
        pipeline.cancelOrder(order2, "Customer changed mind");

        System.out.println("\n✓ Observer Pattern demonstration complete.");
    }
}
