package com.belman.ui.validation;

/**
 * Interface for validators.
 *
 * @param <T> the type of the value to validate
 */
@FunctionalInterface
public interface Validator<T> {
    /**
     * Creates a validator that validates that a string is not null or empty.
     *
     * @return a validator that validates that a string is not null or empty
     */
    static Validator<String> notEmpty() {
        return value -> {
            if (value == null || value.isEmpty()) {
                return ValidationResult.failure("Value cannot be empty");
            }
            return ValidationResult.success();
        };
    }

    /**
     * Creates a validator that validates that a string has a minimum length.
     *
     * @param minLength the minimum length
     * @return a validator that validates that a string has a minimum length
     */
    static Validator<String> minLength(int minLength) {
        return value -> {
            if (value == null || value.length() < minLength) {
                return ValidationResult.failure("Value must be at least " + minLength + " characters long");
            }
            return ValidationResult.success();
        };
    }

    /**
     * Creates a validator that validates that a string has a maximum length.
     *
     * @param maxLength the maximum length
     * @return a validator that validates that a string has a maximum length
     */
    static Validator<String> maxLength(int maxLength) {
        return value -> {
            if (value != null && value.length() > maxLength) {
                return ValidationResult.failure("Value must be at most " + maxLength + " characters long");
            }
            return ValidationResult.success();
        };
    }

    /**
     * Creates a validator that validates that a string is a valid email address.
     *
     * @return a validator that validates that a string is a valid email address
     */
    static Validator<String> email() {
        return regex("^[A-Za-z0-9+_.-]+@(.+)$", "Invalid email address");
    }

    /**
     * Creates a validator that validates that a string matches a regular expression.
     *
     * @param regex        the regular expression
     * @param errorMessage the error message
     * @return a validator that validates that a string matches a regular expression
     */
    static Validator<String> regex(String regex, String errorMessage) {
        return value -> {
            if (value == null || !value.matches(regex)) {
                return ValidationResult.failure(errorMessage);
            }
            return ValidationResult.success();
        };
    }

    /**
     * Creates a validator that validates that a number is greater than a minimum value.
     *
     * @param min the minimum value
     * @return a validator that validates that a number is greater than a minimum value
     */
    static Validator<Number> min(Number min) {
        return value -> {
            if (value == null || value.doubleValue() < min.doubleValue()) {
                return ValidationResult.failure("Value must be at least " + min);
            }
            return ValidationResult.success();
        };
    }

    /**
     * Creates a validator that validates that a number is less than a maximum value.
     *
     * @param max the maximum value
     * @return a validator that validates that a number is less than a maximum value
     */
    static Validator<Number> max(Number max) {
        return value -> {
            if (value == null || value.doubleValue() > max.doubleValue()) {
                return ValidationResult.failure("Value must be at most " + max);
            }
            return ValidationResult.success();
        };
    }

    /**
     * Combines this validator with another validator.
     *
     * @param other the other validator
     * @return a new validator that applies both validators
     */
    default Validator<T> and(Validator<T> other) {
        return value -> {
            ValidationResult result = this.validate(value);
            return result.isValid() ? other.validate(value) : result;
        };
    }

    /**
     * Validates a value.
     *
     * @param value the value to validate
     * @return the validation result
     */
    ValidationResult validate(T value);
}