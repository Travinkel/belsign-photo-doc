package com.belman.domain.common.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a validation operation, indicating whether it was successful
 * and providing a list of associated error messages if applicable.
 */
public class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    /**
     * Creates a new ValidationResult with the specified validity and errors.
     *
     * @param valid  whether the validation succeeded
     * @param errors the list of error messages (must not be null)
     */
    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = new ArrayList<>(errors);
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

    @Override
    public String toString() {
        return "ValidationResult{" +
               "valid=" + valid +
               ", errors=" + errors +
               '}';
    }
}