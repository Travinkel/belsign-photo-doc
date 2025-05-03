package com.belman.unit.backbone.state;

import com.belman.backbone.core.state.StateSchema;
import com.belman.backbone.core.state.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StateSchema class.
 */
public class StateSchemaTest {

    @Test
    void validate_withCorrectType_shouldReturnSuccess() {
        // Arrange
        StateSchema<String> schema = new StateSchema<>(String.class);

        // Act
        ValidationResult result = schema.validate("test");

        // Assert
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void validate_withIncorrectType_shouldReturnFailure() {
        // Arrange
        StateSchema<String> schema = new StateSchema<>(String.class);

        // Act
        ValidationResult result = schema.validate(123);

        // Assert
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).contains("must be of type String"));
    }

    @Test
    void validate_withNullValue_shouldReturnSuccess() {
        // Arrange
        StateSchema<String> schema = new StateSchema<>(String.class);

        // Act
        ValidationResult result = schema.validate(null);

        // Assert
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void validate_withRequiredNullValue_shouldReturnFailure() {
        // Arrange
        StateSchema<String> schema = new StateSchema<>(String.class).required();

        // Act
        ValidationResult result = schema.validate(null);

        // Assert
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).contains("required"));
    }

    @Test
    void validate_withCustomRule_shouldValidateRule() {
        // Arrange
        StateSchema<String> schema = new StateSchema<>(String.class)
                .addRule(s -> s != null && s.length() >= 3, "String must be at least 3 characters long");

        // Act
        ValidationResult result1 = schema.validate("ab");
        ValidationResult result2 = schema.validate("abc");

        // Assert
        assertFalse(result1.isValid());
        assertEquals(1, result1.getErrors().size());
        assertTrue(result1.getErrors().get(0).contains("at least 3 characters"));

        assertTrue(result2.isValid());
        assertTrue(result2.getErrors().isEmpty());
    }

    @Test
    void validate_withMultipleRules_shouldValidateAllRules() {
        // Arrange
        StateSchema<String> schema = new StateSchema<>(String.class)
                .addRule(s -> s != null && s.length() >= 3, "String must be at least 3 characters long")
                .addRule(s -> s != null && s.length() <= 10, "String must be at most 10 characters long")
                .addRule(s -> s != null && s.matches("[a-zA-Z]+"), "String must contain only letters");

        // Act
        ValidationResult result1 = schema.validate("ab");
        ValidationResult result2 = schema.validate("abcdefghijk");
        ValidationResult result3 = schema.validate("abc123");
        ValidationResult result4 = schema.validate("abcdef");

        // Assert
        assertFalse(result1.isValid());
        assertEquals(1, result1.getErrors().size());
        assertTrue(result1.getErrors().get(0).contains("at least 3 characters"));

        assertFalse(result2.isValid());
        assertEquals(1, result2.getErrors().size());
        assertTrue(result2.getErrors().get(0).contains("at most 10 characters"));

        assertFalse(result3.isValid());
        assertEquals(1, result3.getErrors().size());
        assertTrue(result3.getErrors().get(0).contains("only letters"));

        assertTrue(result4.isValid());
        assertTrue(result4.getErrors().isEmpty());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void validate_withNestedSchema_shouldValidateNestedValue() {
        // Arrange
        StateSchema<String> citySchema = new StateSchema<>(String.class)
                .required()
                .addRule(s -> s != null && s.length() >= 2, "City name must be at least 2 characters long");

        StateSchema<Map<String, Object>> addressSchema = new StateSchema<>((Class) Map.class)
                .required()
                .addNestedSchema("city", citySchema);

        StateSchema<Map<String, Object>> userSchema = new StateSchema<>((Class) Map.class)
                .required()
                .addNestedSchema("address", addressSchema);

        // Create a valid user
        Map<String, Object> address1 = new HashMap<>();
        address1.put("city", "New York");

        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "John");
        user1.put("address", address1);

        // Create an invalid user (city too short)
        Map<String, Object> address2 = new HashMap<>();
        address2.put("city", "A");

        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "Jane");
        user2.put("address", address2);

        // Create an invalid user (missing city)
        Map<String, Object> address3 = new HashMap<>();

        Map<String, Object> user3 = new HashMap<>();
        user3.put("name", "Bob");
        user3.put("address", address3);

        // Create an invalid user (missing address)
        Map<String, Object> user4 = new HashMap<>();
        user4.put("name", "Alice");

        // Act
        ValidationResult result1 = userSchema.validate(user1);
        ValidationResult result2 = userSchema.validate(user2);
        ValidationResult result3 = userSchema.validate(user3);
        ValidationResult result4 = userSchema.validate(user4);

        // Assert
        assertTrue(result1.isValid());
        assertTrue(result1.getErrors().isEmpty());

        assertFalse(result2.isValid());
        assertEquals(1, result2.getErrors().size());
        assertTrue(result2.getErrors().get(0).contains("address.city"));

        assertFalse(result3.isValid());
        assertEquals(1, result3.getErrors().size());
        assertTrue(result3.getErrors().get(0).contains("address.city"));

        assertFalse(result4.isValid());
        assertEquals(1, result4.getErrors().size());
        assertTrue(result4.getErrors().get(0).contains("address"));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void builder_shouldCreateSchemaWithAllOptions() {
        // Arrange
        StateSchema<String> citySchema = StateSchema.builder(String.class)
                .required()
                .addRule(s -> s != null && s.length() >= 2, "City name must be at least 2 characters long")
                .build();

        StateSchema<Map<String, Object>> addressSchema = StateSchema.builder((Class) Map.class)
                .required()
                .addNestedSchema("city", citySchema)
                .build();

        StateSchema<Map<String, Object>> userSchema = StateSchema.builder((Class) Map.class)
                .required()
                .addNestedSchema("address", addressSchema)
                .addRule(u -> u != null && ((Map<String, Object>) u).containsKey("name"), "User must have a name")
                .build();

        // Create a valid user
        Map<String, Object> address = new HashMap<>();
        address.put("city", "New York");

        Map<String, Object> user = new HashMap<>();
        user.put("name", "John");
        user.put("address", address);

        // Act
        ValidationResult result = userSchema.validate(user);

        // Assert
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void validate_withNullSchema_shouldThrowException() {
        // Arrange
        StateSchema<Map<String, Object>> schema = new StateSchema<>((Class) Map.class);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            schema.addNestedSchema("test", null);
        });

        assertTrue(exception.getMessage().contains("Schema cannot be null"));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void validate_withEmptyPath_shouldThrowException() {
        // Arrange
        StateSchema<Map<String, Object>> schema = new StateSchema<>((Class) Map.class);
        StateSchema<String> nestedSchema = new StateSchema<>(String.class);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            schema.addNestedSchema("", nestedSchema);
        });

        assertTrue(exception.getMessage().contains("Property path cannot be null or empty"));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void validate_withNullPath_shouldThrowException() {
        // Arrange
        StateSchema<Map<String, Object>> schema = new StateSchema<>((Class) Map.class);
        StateSchema<String> nestedSchema = new StateSchema<>(String.class);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            schema.addNestedSchema(null, nestedSchema);
        });

        assertTrue(exception.getMessage().contains("Property path cannot be null or empty"));
    }

    @Test
    void validate_withNullRule_shouldThrowException() {
        // Arrange
        StateSchema<String> schema = new StateSchema<>(String.class);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            schema.addRule(null, "Error message");
        });

        assertTrue(exception.getMessage().contains("Rule cannot be null"));
    }

    @Test
    void validate_withEmptyErrorMessage_shouldThrowException() {
        // Arrange
        StateSchema<String> schema = new StateSchema<>(String.class);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            schema.addRule(s -> true, "");
        });

        assertTrue(exception.getMessage().contains("Error message cannot be null or empty"));
    }

    @Test
    void validate_withNullErrorMessage_shouldThrowException() {
        // Arrange
        StateSchema<String> schema = new StateSchema<>(String.class);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            schema.addRule(s -> true, null);
        });

        assertTrue(exception.getMessage().contains("Error message cannot be null or empty"));
    }

    @Test
    void validate_withNullType_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new StateSchema<>(null);
        });

        assertTrue(exception.getMessage().contains("Type cannot be null"));
    }
}
