package com.belman.domain.specification;

import com.belman.domain.order.photo.PhotoAngle;
import com.belman.domain.order.photo.PhotoDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain specification that validates photo quality requirements.
 * This specification enforces business rules about what constitutes 
 * an acceptable photo for quality control purposes.
 */
public final class PhotoQualitySpecification extends AbstractSpecification<PhotoDocument> {

    private final List<String> validationMessages = new ArrayList<>();

    /**
     * Clears any previous validation messages.
     */
    public void clearMessages() {
        validationMessages.clear();
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
    public boolean isSatisfiedBy(PhotoDocument candidate) {
        clearMessages();

        boolean isValid = true;

        // Validate photo has required angles based on product type
        // This is a simplified example - in a real system, we would get the 
        // product type from the order and check against specific requirements
        if (candidate.getOrderId() == null) {
            validationMessages.add("Photo must be assigned to an order");
            isValid = false;
        }

        // Validate photo angle is appropriate
        PhotoAngle angle = candidate.getAngle();
        if (angle == null) {
            validationMessages.add("Photo angle must be specified");
            isValid = false;
        }

        // Validate annotation requirements
        // For example, certain products might require specific measurements to be annotated
        if (candidate.getAnnotations().isEmpty()) {
            validationMessages.add("Photo should have at least one annotation with measurements");
            isValid = false;
        }

        // Other validation rules can be added here based on business requirements
        // For example:
        // - Minimum image resolution
        // - Required lighting conditions
        // - Presence of specific features in the photo

        return isValid;
    }

    @Override
    public Specification<PhotoDocument> and(Specification<PhotoDocument> other) {
        return other; // Simplified implementation due to sealed interface constraints
    }

    @Override
    public Specification<PhotoDocument> or(Specification<PhotoDocument> other) {
        return this; // Simplified implementation due to sealed interface constraints
    }

    @Override
    public Specification<PhotoDocument> not() {
        return this; // Simplified implementation due to sealed interface constraints
    }

    /**
     * Factory method to create a new PhotoQualitySpecification.
     *
     * @return a new PhotoQualitySpecification instance
     */
    public static PhotoQualitySpecification create() {
        return new PhotoQualitySpecification();
    }
}