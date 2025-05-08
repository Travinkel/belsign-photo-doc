package com.belman.business.commands.validation;

import com.belman.business.richbe.common.validation.ValidationResult;
import com.belman.business.richbe.shared.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * Command for validating input data.
 * <p>
 * This command validates input data against a set of validation rules
 * and returns a validation result.
 *
 * @param <T> the type of data being validated
 */
public class ValidationCommand<T> implements Command<ValidationResult> {
    private final T data;
    private final List<ValidationRule<T>> validationRules;
    private final String description;

    /**
     * Creates a new ValidationCommand with the specified data.
     *
     * @param data        the data to validate
     * @param description a description of the validation operation
     */
    public ValidationCommand(T data, String description) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        
        this.data = data;
        this.validationRules = new ArrayList<>();
        this.description = description != null ? description : "Validate data";
    }

    /**
     * Adds a validation rule to this command.
     *
     * @param rule      the validation rule predicate
     * @param errorMessage the error message to display if validation fails
     * @return this command for method chaining
     */
    public ValidationCommand<T> addRule(Predicate<T> rule, String errorMessage) {
        if (rule == null) {
            throw new IllegalArgumentException("Validation rule cannot be null");
        }
        if (errorMessage == null || errorMessage.isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be null or empty");
        }
        
        validationRules.add(new ValidationRule<>(rule, errorMessage));
        return this;
    }

    @Override
    public CompletableFuture<ValidationResult> execute() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> errors = new ArrayList<>();
            
            // Apply each validation rule
            for (ValidationRule<T> rule : validationRules) {
                if (!rule.test(data)) {
                    errors.add(rule.getErrorMessage());
                }
            }
            
            return new ValidationResult(errors.isEmpty(), errors);
        });
    }

    @Override
    public CompletableFuture<Void> undo() {
        // Validation cannot be undone
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("Validation cannot be undone"));
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * A validation rule consisting of a predicate and an error message.
     *
     * @param <T> the type of data being validated
     */
    private static class ValidationRule<T> {
        private final Predicate<T> rule;
        private final String errorMessage;

        /**
         * Creates a new ValidationRule with the specified predicate and error message.
         *
         * @param rule         the validation rule predicate
         * @param errorMessage the error message to display if validation fails
         */
        public ValidationRule(Predicate<T> rule, String errorMessage) {
            this.rule = rule;
            this.errorMessage = errorMessage;
        }

        /**
         * Tests the data against this validation rule.
         *
         * @param data the data to validate
         * @return true if the data passes validation, false otherwise
         */
        public boolean test(T data) {
            return rule.test(data);
        }

        /**
         * Gets the error message for this validation rule.
         *
         * @return the error message
         */
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}