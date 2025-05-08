package com.belman.business.domain.order.specification;

import com.belman.business.domain.order.OrderAggregate;
import com.belman.business.domain.order.OrderStatus;
import com.belman.business.domain.order.photo.PhotoDocument;
import com.belman.business.domain.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification that determines if an order is ready for completion.
 * <p>
 * An order is considered ready for completion if:
 * 1. It is currently in PRODUCTION status
 * 2. All required photos have been taken and approved
 * 3. Any other order-specific completion criteria are met
 * <p>
 * This specification follows the Specification pattern from Domain-Driven Design.
 */
public class OrderCompletionSpecification extends AbstractSpecification<OrderAggregate> {

    private final List<String> validationMessages = new ArrayList<>();
    private final int requiredPhotoCount;

    /**
     * Creates a new OrderCompletionSpecification with the specified required photo count.
     *
     * @param requiredPhotoCount the minimum number of approved photos required for completion
     */
    public OrderCompletionSpecification(int requiredPhotoCount) {
        if (requiredPhotoCount < 1) {
            throw new IllegalArgumentException("Required photo count must be at least 1");
        }
        this.requiredPhotoCount = requiredPhotoCount;
    }

    /**
     * Returns all validation messages generated during the last check.
     *
     * @return a list of validation messages
     */
    public List<String> getValidationMessages() {
        return new ArrayList<>(validationMessages);
    }

    /**
     * Clears any previous validation messages.
     */
    private void clearMessages() {
        validationMessages.clear();
    }

    @Override
    public boolean isSatisfiedBy(OrderAggregate orderAggregate) {
        clearMessages();

        boolean isValid = true;

        // Check that the orderAggregate is in PRODUCTION status
        if (orderAggregate.getStatus() != OrderStatus.IN_PROGRESS) {
            validationMessages.add("OrderAggregate must be in IN_PROGRESS status to be completed");
            isValid = false;
        }

        // Check that all required photos have been taken and approved
        List<PhotoDocument> approvedPhotos = orderAggregate.getPhotos().stream()
                .filter(photo -> photo.getStatus() == PhotoDocument.ApprovalStatus.APPROVED)
                .toList();

        if (approvedPhotos.size() < requiredPhotoCount) {
            validationMessages.add(String.format(
                    "OrderAggregate requires at least %d approved photos (found %d)",
                    requiredPhotoCount, approvedPhotos.size()));
            isValid = false;
        }

        // Check that photos from all required angles have been taken and approved
        // This would typically be a more complex check based on product type
        // For simplicity, we'll just check the count here

        return isValid;
    }
}