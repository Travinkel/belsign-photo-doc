package com.belman.unit.be.valueobjects;

import com.belman.domain.common.valueobjects.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PhoneNumber value object.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "+4512345678", // Danish format with country code
            "12345678", // Danish format without country code
            "+1 (555) 123-4567", // US format with country code
            "555-123-4567", // US format without country code
            "+44 7911 123456", // UK format with country code
            "07911 123456" // UK format without country code
    })
    void constructor_withValidPhoneNumber_shouldCreatePhoneNumber(String phoneNumberStr) {
        // Act
        PhoneNumber phoneNumber = new PhoneNumber(phoneNumberStr);

        // Assert
        assertEquals(phoneNumberStr, phoneNumber.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "123", // Too short
            "abcdefghij", // Contains letters
            "+123456789012345678901", // Too long
            "!@#$%^&*()" // Special characters
    })
    void constructor_withInvalidPhoneNumber_shouldThrowException(String phoneNumberStr) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PhoneNumber(phoneNumberStr);
        });

        assertEquals("Invalid phone number format", exception.getMessage());
    }

    @Test
    void getNormalized_shouldReturnNormalizedPhoneNumber() {
        // Arrange
        PhoneNumber phoneNumber = new PhoneNumber("+4512345678");

        // Act
        String normalized = phoneNumber.getNormalized();

        // Assert
        assertEquals("4512345678", normalized);
    }

    @Test
    void toString_shouldReturnPhoneNumberValue() {
        // Arrange
        PhoneNumber phoneNumber = new PhoneNumber("+4512345678");

        // Act
        String result = phoneNumber.toString();

        // Assert
        assertEquals("+4512345678", result);
    }

    @Test
    void equals_withSameValues_shouldBeEqual() {
        // Arrange
        PhoneNumber phoneNumber1 = new PhoneNumber("+4512345678");
        PhoneNumber phoneNumber2 = new PhoneNumber("+4512345678");

        // Act & Assert
        assertEquals(phoneNumber1, phoneNumber2);
        assertEquals(phoneNumber1.hashCode(), phoneNumber2.hashCode());
    }

    @Test
    void equals_withDifferentValues_shouldNotBeEqual() {
        // Arrange
        PhoneNumber phoneNumber1 = new PhoneNumber("+4512345678");
        PhoneNumber phoneNumber2 = new PhoneNumber("+4587654321");

        // Act & Assert
        assertNotEquals(phoneNumber1, phoneNumber2);
    }
}
