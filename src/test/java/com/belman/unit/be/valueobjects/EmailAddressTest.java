package com.belman.unit.be.valueobjects;

import com.belman.domain.common.EmailAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the EmailAddress value object.
 */
public class EmailAddressTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "test@example.com",
        "user.name@example.com",
        "user+tag@example.com",
        "user@subdomain.example.com",
        "user@example.co.uk"
    })
    void constructor_withValidEmail_shouldCreateEmailAddress(String email) {
        // Act
        EmailAddress emailAddress = new EmailAddress(email);

        // Assert
        assertEquals(email, emailAddress.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "invalid",
        "invalid@",
        "@example.com",
        "user@.com",
        "user@example.",
        "user@example",
        "user name@example.com",
        "user@example com"
    })
    void constructor_withInvalidEmail_shouldThrowException(String email) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new EmailAddress(email);
        });

        assertEquals("Invalid email address", exception.getMessage());
    }

    @Test
    void constructor_withNullEmail_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new EmailAddress(null);
        });

        assertEquals("Invalid email address", exception.getMessage());
    }

    @Test
    void value_shouldReturnEmailValue() {
        // Arrange
        String email = "test@example.com";
        EmailAddress emailAddress = new EmailAddress(email);

        // Act
        String value = emailAddress.value();

        // Assert
        assertEquals(email, value);
    }

    @Test
    void toString_shouldReturnEmailValue() {
        // Arrange
        String email = "test@example.com";
        EmailAddress emailAddress = new EmailAddress(email);

        // Act
        String stringValue = emailAddress.toString();

        // Assert
        assertEquals(email, stringValue);
    }

    @Test
    void equals_withSameEmail_shouldBeEqual() {
        // Arrange
        EmailAddress email1 = new EmailAddress("test@example.com");
        EmailAddress email2 = new EmailAddress("test@example.com");

        // Act & Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void equals_withDifferentEmail_shouldNotBeEqual() {
        // Arrange
        EmailAddress email1 = new EmailAddress("test1@example.com");
        EmailAddress email2 = new EmailAddress("test2@example.com");

        // Act & Assert
        assertNotEquals(email1, email2);
    }
}
