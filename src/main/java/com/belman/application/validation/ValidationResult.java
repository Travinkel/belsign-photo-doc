package com.belman.application.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a validation operation.
 */
public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;

    /**
     * Creates a new ValidationResult.
     *
     * @param valid  whether the validation was successful
     * @param errors the validation errors
     */
    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    /**
     * Creates a successful validation result.
     *
     * @return a successful validation result
     */
    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    /**
     * Creates a failed validation result with the specified error.
     *
     * @param error the validation error
     * @return a failed validation result
     */
    public static ValidationResult failure(String error) {
        return new ValidationResult(false, Collections.singletonList(error));
    }

    /**
     * Creates a failed validation result with the specified errors.
     *
     * @param errors the validation errors
     * @return a failed validation result
     */
    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    /**
     * Gets whether the validation was successful.
     *
     * @return true if the validation was successful, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the validation errors.
     *
     * @return the validation errors
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Gets the first validation error, or null if there are no errors.
     *
     * @return the first validation error, or null if there are no errors
     */
    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }

    /**
     * Gets a string representation of the validation errors.
     *
     * @return a string representation of the validation errors
     */
    public String getErrorsAsString() {
        if (errors.isEmpty()) {
            return "";
        }
        return String.join("\n", errors);
    }

    /**
     * Combines this validation result with another validation result.
     *
     * @param other the other validation result
     * @return a new validation result that is valid only if both validation results are valid
     */
    public ValidationResult and(ValidationResult other) {
        if (other == null) {
            return this;
        }

        boolean combinedValid = this.valid && other.valid;
        List<String> combinedErrors = new ArrayList<>(this.errors);
        combinedErrors.addAll(other.errors);

        return new ValidationResult(combinedValid, combinedErrors);
    }
}