/**
 * EVENT TYPE ENUM
 *
 * Defines the distinct lifecycle states of an e-commerce order.
 */
public enum OrderEventType {
    ORDER_CREATED,
    PAYMENT_RECEIVED,
    ORDER_PACKED,
    ORDER_SHIPPED,
    ORDER_DELIVERED,
    ORDER_CANCELLED
}
