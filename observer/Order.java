import java.util.Collections;
import java.util.List;

/**
 * DOMAIN MODEL
 *
 * Represents an e-commerce order containing items, customer contacts,
 * and monetary totals.
 */
public class Order {
    private final String orderId;
    private final String customerId;
    private final String customerEmail;
    private final String customerPhone;
    private final List<String> items;
    private final double totalAmount;
    private String status;

    public Order(String orderId, String customerId, String customerEmail, String customerPhone,
                 List<String> items, double totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.items = items != null ? items : Collections.emptyList();
        this.totalAmount = totalAmount;
        this.status = "CREATED";
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public List<String> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Order[#%s | Customer: %s | Total: $%.2f | Status: %s | Items: %s]",
                orderId, customerEmail, totalAmount, status, items);
    }
}
