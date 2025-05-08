package com.belman.unit.domain.valueobjects;

import com.belman.business.domain.common.PersonName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PersonName value object.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PersonNameTest {

    @Test
    void constructor_withValidNames_shouldCreatePersonName() {
        // Act
        PersonName name = new PersonName("John", "Doe");

        // Assert
        assertEquals("John", name.firstName());
        assertEquals("Doe", name.lastName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void constructor_withInvalidFirstName_shouldThrowException(String firstName) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PersonName(firstName, "Doe");
        });

        assertEquals("First name must not be null or blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void constructor_withInvalidLastName_shouldThrowException(String lastName) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PersonName("John", lastName);
        });

        assertEquals("Last name must not be null or blank", exception.getMessage());
    }

    @Test
    void getFullName_shouldReturnConcatenatedNames() {
        // Arrange
        PersonName name = new PersonName("John", "Doe");

        // Act
        String fullName = name.getFullName();

        // Assert
        assertEquals("John Doe", fullName);
    }

    @Test
    void toString_shouldReturnFullName() {
        // Arrange
        PersonName name = new PersonName("John", "Doe");

        // Act
        String result = name.toString();

        // Assert
        assertEquals("John Doe", result);
    }

    @Test
    void equals_withSameValues_shouldBeEqual() {
        // Arrange
        PersonName name1 = new PersonName("John", "Doe");
        PersonName name2 = new PersonName("John", "Doe");

        // Act & Assert
        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void equals_withDifferentValues_shouldNotBeEqual() {
        // Arrange
        PersonName name1 = new PersonName("John", "Doe");
        PersonName name2 = new PersonName("Jane", "Doe");

        // Act & Assert
        assertNotEquals(name1, name2);
    }
}
