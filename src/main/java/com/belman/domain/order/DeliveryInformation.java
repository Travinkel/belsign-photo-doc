package com.belman.domain.order;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.base.ValueObject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Value object representing delivery information for an order.
 * This is specific to the order bounded context.
 */
public record DeliveryInformation(String address, LocalDate estimatedDeliveryDate, String contactName,
                                  EmailAddress contactEmail, String specialInstructions) implements ValueObject {

    /**
     * Creates a new DeliveryInformation with the specified details.
     *
     * @param address               the delivery address
     * @param estimatedDeliveryDate the estimated delivery date
     * @param contactName           the name of the contact person for delivery
     * @param contactEmail          the email address of the contact person
     * @param specialInstructions   any special instructions for delivery (can be null)
     * @throws IllegalArgumentException if address, estimatedDeliveryDate, or contactName is null or empty
     * @throws NullPointerException     if contactEmail is null
     */
    public DeliveryInformation {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Delivery address must not be null or blank");
        }

        Objects.requireNonNull(estimatedDeliveryDate, "Estimated delivery date must not be null");

        if (contactName == null || contactName.isBlank()) {
            throw new IllegalArgumentException("Contact name must not be null or blank");
        }

        Objects.requireNonNull(contactEmail, "Contact email must not be null");

        // specialInstructions can be null
    }
}
