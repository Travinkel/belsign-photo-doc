package com.belman.domain.common.validation;

import com.belman.domain.order.OrderAggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the result of a validation operation, indicating whether it was successful
 * and providing a list of associated error messages if applicable.
 */
public class ValidationResult {

    private boolean valid;
    private final List<String> errors;
    private final List<String> warnings;

    public ValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = new ArrayList<>(errors);
        this.warnings = new ArrayList<>();
    }

    /**
     * Creates a new ValidationResult with the specified validity, errors, and warnings.
     *
     * @param valid    whether the validation succeeded
     * @param errors   the list of error messages (must not be null)
     * @param warnings the list of warning messages (must not be null)
     */
    public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
        this.valid = valid;
        this.errors = new ArrayList<>(errors);
        this.warnings = new ArrayList<>(warnings);
    }

    /**
     * Adds an error message to the ValidationResult.
     *
     * @param error the error message to add
     */
    public void addError(String error) {
        Objects.requireNonNull(error, "error must not be null");
        this.errors.add(error);
        this.valid = false;
    }

    /**
     * Adds a warning message to the ValidationResult.
     *
     * @param warning the warning message to add
     */
    public void addWarning(String warning) {
        Objects.requireNonNull(warning, "warning must not be null");
        this.warnings.add(warning);
    }

    /**
     * Combines this ValidationResult with another ValidationResult.
     *
     * @param other the other ValidationResult to combine with
     */
    public void combine(ValidationResult other) {
        Objects.requireNonNull(other, "other ValidationResult must not be null");
        this.errors.addAll(other.errors);
        this.warnings.addAll(other.warnings);
        this.valid = this.valid && other.valid;
    }

    /**
     * Creates a successful ValidationResult.
     *
     * @return a ValidationResult indicating success
     */
    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    /**
     * Creates a failed ValidationResult with a single error message.
     *
     * @param errorMessage the error message
     * @return a ValidationResult indicating failure
     */
    public static ValidationResult failure(String errorMessage) {
        return new ValidationResult(false, List.of(errorMessage));
    }

    /**
     * Creates a failed ValidationResult with multiple error messages.
     *
     * @param errors the list of error messages
     * @return a ValidationResult indicating failure
     */
    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    /**
     * Indicates whether the validation was successful.
     *
     * @return true if validation succeeded, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Returns the list of error messages associated with this ValidationResult.
     *
     * @return the list of error messages (empty if valid)
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Returns the list of warning messages associated with this ValidationResult.
     *
     * @return the list of warning messages (empty if no warnings)
     */
    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
               "valid=" + valid +
               ", errors=" + errors +
               ", warnings=" + warnings +
               '}';
    }

    public boolean hasRequiredApprovedPhotos(OrderAggregate orderAggregate) {
        Objects.requireNonNull(orderAggregate, "orderAggregate must not be null");

        ValidationResult result = validateOrderPhotos(orderAggregate);
        return result.isValid();
    }

    private ValidationResult validateOrderPhotos(OrderAggregate orderAggregate) {
        if (orderAggregate.getPhotos().size() < 3) {
            return ValidationResult.failure("Order must have at least 3 approved photos.");
        }
        return ValidationResult.success();
    }
}