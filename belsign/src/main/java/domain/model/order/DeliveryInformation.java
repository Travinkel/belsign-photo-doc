package domain.model.order;

import domain.model.shared.Timestamp;

import java.util.Objects;

/**
 * Value object representing delivery information for an order.
 */
public record DeliveryInformation(String address, Timestamp estimatedDelivery, String specialInstructions) {
    /**
     * Creates a DeliveryInformation with the specified address, estimated delivery time, and special instructions.
     * 
     * @param address the delivery address
     * @param estimatedDelivery the estimated delivery time
     * @param specialInstructions any special delivery instructions (can be null)
     * @throws IllegalArgumentException if address is blank
     * @throws NullPointerException if estimatedDelivery is null
     */
    public DeliveryInformation {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Delivery address cannot be empty");
        }
        Objects.requireNonNull(estimatedDelivery, "Estimated delivery time must not be null");
    }
    
    /**
     * Creates a DeliveryInformation with just an address and estimated delivery time.
     * 
     * @param address the delivery address
     * @param estimatedDelivery the estimated delivery time
     * @return a new DeliveryInformation with the specified address and estimated delivery time
     */
    public static DeliveryInformation basic(String address, Timestamp estimatedDelivery) {
        return new DeliveryInformation(address, estimatedDelivery, null);
    }
    
    /**
     * @return true if there are special delivery instructions
     */
    public boolean hasSpecialInstructions() {
        return specialInstructions != null && !specialInstructions.isBlank();
    }
    
    /**
     * @return a string representation of the delivery information
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Delivery to: ").append(address);
        sb.append(", estimated: ").append(estimatedDelivery);
        if (hasSpecialInstructions()) {
            sb.append(", instructions: ").append(specialInstructions);
        }
        return sb.toString();
    }
}