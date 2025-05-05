package com.belman.application.commands.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a validation operation.
 * <p>
 * This class contains information about whether the validation was successful
 * and any error messages that were generated during validation.
 */
public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;

    /**
     * Creates a new ValidationResult with the specified validity and errors.
     *
     * @param valid  whether the validation was successful
     * @param errors the list of error messages
     */
    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    /**
     * Creates a successful validation result with no errors.
     *
     * @return a successful validation result
     */
    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    /**
     * Creates a failed validation result with the specified error message.
     *
     * @param errorMessage the error message
     * @return a failed validation result
     */
    public static ValidationResult failure(String errorMessage) {
        List<String> errors = new ArrayList<>();
        errors.add(errorMessage);
        return new ValidationResult(false, errors);
    }

    /**
     * Creates a failed validation result with the specified error messages.
     *
     * @param errors the list of error messages
     * @return a failed validation result
     */
    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    /**
     * Checks if the validation was successful.
     *
     * @return true if the validation was successful, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the list of error messages.
     *
     * @return the list of error messages
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Gets the first error message, or null if there are no errors.
     *
     * @return the first error message, or null if there are no errors
     */
    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }

    /**
     * Gets a comma-separated string of all error messages.
     *
     * @return a comma-separated string of all error messages
     */
    public String getErrorsAsString() {
        return String.join(", ", errors);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errors=" + errors +
                '}';
    }
}