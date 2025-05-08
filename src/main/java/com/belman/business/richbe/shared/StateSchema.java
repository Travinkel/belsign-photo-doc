package com.belman.business.richbe.shared;

import com.belman.business.richbe.common.validation.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A schema for validating state values.
 * <p>
 * This class allows for defining validation rules for state values and ensuring
 * that state values conform to a specific schema. It supports both simple and
 * complex validation rules, including type checking, required fields, and custom
 * validation functions.
 *
 * @param <T> the type of the state value
 */
public class StateSchema<T> {
    private final Class<T> type;
    private final List<ValidationRule<T>> rules = new ArrayList<>();
    private final Map<String, StateSchema<?>> nestedSchemas = new HashMap<>();
    private boolean required = false;

    /**
     * Creates a new StateSchema with the specified type.
     *
     * @param type the expected type of the state value
     */
    public StateSchema(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.type = type;
    }

    /**
     * Gets the expected type of the state value.
     *
     * @return the expected type
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Marks the state value as required.
     *
     * @return this schema for method chaining
     */
    public StateSchema<T> required() {
        this.required = true;
        return this;
    }

    /**
     * Adds a validation rule to this schema.
     *
     * @param rule the validation rule
     * @param errorMessage the error message to display if validation fails
     * @return this schema for method chaining
     */
    public StateSchema<T> addRule(Predicate<T> rule, String errorMessage) {
        if (rule == null) {
            throw new IllegalArgumentException("Rule cannot be null");
        }
        if (errorMessage == null || errorMessage.isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be null or empty");
        }
        rules.add(new ValidationRule<>(rule, errorMessage));
        return this;
    }

    /**
     * Adds a nested schema for a property of the state value.
     *
     * @param propertyPath the path to the property (e.g., "address.city")
     * @param schema the schema for the property
     * @param <V> the type of the property
     * @return this schema for method chaining
     */
    public <V> StateSchema<T> addNestedSchema(String propertyPath, StateSchema<V> schema) {
        if (propertyPath == null || propertyPath.isEmpty()) {
            throw new IllegalArgumentException("Property path cannot be null or empty");
        }
        if (schema == null) {
            throw new IllegalArgumentException("Schema cannot be null");
        }
        nestedSchemas.put(propertyPath, schema);
        return this;
    }

    /**
     * Validates a state value against this schema.
     *
     * @param value the state value to validate
     * @return a validation result
     */
    public ValidationResult validate(Object value) {
        List<String> errors = new ArrayList<>();

        // Check if the value is required
        if (required && value == null) {
            errors.add("Value is required");
            return new ValidationResult(false, errors);
        }

        // If the value is null and not required, it's valid
        if (value == null) {
            return ValidationResult.success();
        }

        // Check the type
        if (!type.isInstance(value)) {
            errors.add("Value must be of type " + type.getSimpleName());
            return new ValidationResult(false, errors);
        }

        // Apply validation rules
        T typedValue = type.cast(value);
        for (ValidationRule<T> rule : rules) {
            if (!rule.test(typedValue)) {
                errors.add(rule.getErrorMessage());
            }
        }

        // Validate nested schemas
        for (Map.Entry<String, StateSchema<?>> entry : nestedSchemas.entrySet()) {
            String propertyPath = entry.getKey();
            StateSchema<?> schema = entry.getValue();

            // Get the nested value
            Object nestedValue = getNestedValue(typedValue, propertyPath);

            // Validate the nested value
            ValidationResult nestedResult = schema.validate(nestedValue);
            if (!nestedResult.isValid()) {
                // Add the property path to the error messages
                for (String error : nestedResult.getErrors()) {
                    errors.add(propertyPath + ": " + error);
                }
            }
        }

        return errors.isEmpty() ? ValidationResult.success() : new ValidationResult(false, errors);
    }

    /**
     * Gets a nested value from an object.
     *
     * @param obj the object to get the nested value from
     * @param path the path to the nested value (e.g., "address.city")
     * @return the nested value, or null if not found
     */
    @SuppressWarnings("unchecked")
    private Object getNestedValue(Object obj, String path) {
        if (obj == null || path == null || path.isEmpty()) {
            return null;
        }

        String[] parts = path.split("\\.");
        Object value = obj;

        for (String part : parts) {
            if (value == null) {
                return null;
            }

            // Handle maps
            if (value instanceof Map) {
                value = ((Map<String, Object>) value).get(part);
                continue;
            }

            // Handle lists and arrays with numeric indices
            if (value instanceof List && part.matches("\\d+")) {
                int index = Integer.parseInt(part);
                List<Object> list = (List<Object>) value;
                if (index >= 0 && index < list.size()) {
                    value = list.get(index);
                } else {
                    return null;
                }
                continue;
            }

            // Handle JavaBean properties using reflection
            try {
                // Try to find a getter method
                String getterName = "get" + part.substring(0, 1).toUpperCase() + part.substring(1);
                java.lang.reflect.Method getter = value.getClass().getMethod(getterName);
                value = getter.invoke(value);
            } catch (Exception e) {
                try {
                    // Try to find a boolean getter method (isXxx)
                    String isGetterName = "is" + part.substring(0, 1).toUpperCase() + part.substring(1);
                    java.lang.reflect.Method isGetter = value.getClass().getMethod(isGetterName);
                    value = isGetter.invoke(value);
                } catch (Exception e2) {
                    try {
                        // Try to access the field directly
                        java.lang.reflect.Field field = value.getClass().getDeclaredField(part);
                        field.setAccessible(true);
                        value = field.get(value);
                    } catch (Exception e3) {
                        // Property not found
                        return null;
                    }
                }
            }
        }

        return value;
    }

    /**
     * A validation rule consisting of a predicate and an error message.
     *
     * @param <T> the type of the value being validated
     */
    private static class ValidationRule<T> {
        private final Predicate<T> rule;
        private final String errorMessage;

        /**
         * Creates a new ValidationRule with the specified predicate and error message.
         *
         * @param rule the validation rule predicate
         * @param errorMessage the error message to display if validation fails
         */
        public ValidationRule(Predicate<T> rule, String errorMessage) {
            this.rule = rule;
            this.errorMessage = errorMessage;
        }

        /**
         * Tests the value against this validation rule.
         *
         * @param value the value to validate
         * @return true if the value passes validation, false otherwise
         */
        public boolean test(T value) {
            return rule.test(value);
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

    /**
     * Creates a builder for creating a StateSchema.
     *
     * @param type the expected type of the state value
     * @param <T> the type of the state value
     * @return a new StateSchemaBuilder
     */
    public static <T> StateSchemaBuilder<T> builder(Class<T> type) {
        return new StateSchemaBuilder<>(type);
    }

    /**
     * A builder for creating a StateSchema.
     *
     * @param <T> the type of the state value
     */
    public static class StateSchemaBuilder<T> {
        private final StateSchema<T> schema;

        /**
         * Creates a new StateSchemaBuilder with the specified type.
         *
         * @param type the expected type of the state value
         */
        private StateSchemaBuilder(Class<T> type) {
            this.schema = new StateSchema<>(type);
        }

        /**
         * Marks the state value as required.
         *
         * @return this builder for method chaining
         */
        public StateSchemaBuilder<T> required() {
            schema.required();
            return this;
        }

        /**
         * Adds a validation rule to the schema.
         *
         * @param rule the validation rule
         * @param errorMessage the error message to display if validation fails
         * @return this builder for method chaining
         */
        public StateSchemaBuilder<T> addRule(Predicate<T> rule, String errorMessage) {
            schema.addRule(rule, errorMessage);
            return this;
        }

        /**
         * Adds a nested schema for a property of the state value.
         *
         * @param propertyPath the path to the property (e.g., "address.city")
         * @param nestedSchema the schema for the property
         * @param <V> the type of the property
         * @return this builder for method chaining
         */
        public <V> StateSchemaBuilder<T> addNestedSchema(String propertyPath, StateSchema<V> nestedSchema) {
            schema.addNestedSchema(propertyPath, nestedSchema);
            return this;
        }

        /**
         * Builds the StateSchema.
         *
         * @return the built StateSchema
         */
        public StateSchema<T> build() {
            return schema;
        }
    }
}