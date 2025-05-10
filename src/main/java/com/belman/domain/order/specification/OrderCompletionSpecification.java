package com.belman.domain.order.specification;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.specification.AbstractSpecification;

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
public class OrderCompletionSpecification extends AbstractSpecification<OrderBusiness> {

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

    @Override
    public boolean isSatisfiedBy(OrderBusiness orderBusiness) {
        clearMessages();

        boolean isValid = true;

        // Check that the orderBusiness is in PRODUCTION status
        if (orderBusiness.getStatus() != OrderStatus.IN_PROGRESS) {
            validationMessages.add("OrderBusiness must be in IN_PROGRESS status to be completed");
            isValid = false;
        }

        // Check that all required photos have been taken and approved
        List<PhotoDocument> approvedPhotos = orderBusiness.getPhotos().stream()
                .filter(photo -> photo.getStatus() == PhotoDocument.ApprovalStatus.APPROVED)
                .toList();

        if (approvedPhotos.size() < requiredPhotoCount) {
            validationMessages.add(String.format(
                    "OrderBusiness requires at least %d approved photos (found %d)",
                    requiredPhotoCount, approvedPhotos.size()));
            isValid = false;
        }

        // Check that photos from all required angles have been taken and approved
        // This would typically be a more complex check based on product type
        // For simplicity, we'll just check the count here

        return isValid;
    }

    /**
     * Clears any previous validation messages.
     */
    private void clearMessages() {
        validationMessages.clear();
    }
}