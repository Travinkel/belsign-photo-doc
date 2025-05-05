package com.belman.unit.domain.valueobjects;

import com.belman.domain.valueobjects.PhotoAngle;
import com.belman.domain.valueobjects.PhotoAngle.NamedAngle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PhotoAngle value object.
 */
public class PhotoAngleTest {

    @Test
    void constructor_withValidDegrees_shouldCreatePhotoAngle() {
        // Act
        PhotoAngle angle = new PhotoAngle(45.0);
        
        // Assert
        assertEquals(45.0, angle.degrees());
        assertNull(angle.namedAngle());
        assertFalse(angle.isNamedAngle());
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -0.1, 360.0, 361.0})
    void constructor_withInvalidDegrees_shouldThrowException(double degrees) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PhotoAngle(degrees);
        });
        
        assertEquals("Photo angle must be in [0, 360)", exception.getMessage());
    }
    
    @Test
    void constructor_withNamedAngle_shouldCreatePhotoAngle() {
        // Act
        PhotoAngle angle = new PhotoAngle(NamedAngle.FRONT);
        
        // Assert
        assertEquals(0.0, angle.degrees());
        assertEquals(NamedAngle.FRONT, angle.namedAngle());
        assertTrue(angle.isNamedAngle());
    }
    
    @Test
    void constructor_withNullNamedAngle_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () -> {
            new PhotoAngle((NamedAngle) null);
        });
        
        assertEquals("Named angle cannot be null", exception.getMessage());
    }
    
    @Test
    void constructor_withDegreesAndMatchingNamedAngle_shouldCreatePhotoAngle() {
        // Act
        PhotoAngle angle = new PhotoAngle(90.0, NamedAngle.RIGHT);
        
        // Assert
        assertEquals(90.0, angle.degrees());
        assertEquals(NamedAngle.RIGHT, angle.namedAngle());
        assertTrue(angle.isNamedAngle());
    }
    
    @Test
    void constructor_withDegreesAndMismatchedNamedAngle_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PhotoAngle(45.0, NamedAngle.FRONT);
        });
        
        assertEquals("Degrees must match the named angle's degrees", exception.getMessage());
    }
    
    @Test
    void getDisplayName_withCustomAngle_shouldReturnDegreesWithSymbol() {
        // Arrange
        PhotoAngle angle = new PhotoAngle(45.0);
        
        // Act
        String displayName = angle.getDisplayName();
        
        // Assert
        assertEquals("45.0°", displayName);
    }
    
    @Test
    void getDisplayName_withNamedAngle_shouldReturnAngleName() {
        // Arrange
        PhotoAngle angle = new PhotoAngle(NamedAngle.BACK);
        
        // Act
        String displayName = angle.getDisplayName();
        
        // Assert
        assertEquals("BACK", displayName);
    }
    
    @Test
    void namedAngleEnum_shouldHaveCorrectDegrees() {
        // Assert
        assertEquals(0.0, NamedAngle.FRONT.getDegrees());
        assertEquals(90.0, NamedAngle.RIGHT.getDegrees());
        assertEquals(180.0, NamedAngle.BACK.getDegrees());
        assertEquals(270.0, NamedAngle.LEFT.getDegrees());
    }
    
    @Test
    void equals_withSameValues_shouldBeEqual() {
        // Arrange
        PhotoAngle angle1 = new PhotoAngle(45.0);
        PhotoAngle angle2 = new PhotoAngle(45.0);
        
        // Act & Assert
        assertEquals(angle1, angle2);
        assertEquals(angle1.hashCode(), angle2.hashCode());
    }
    
    @Test
    void equals_withDifferentValues_shouldNotBeEqual() {
        // Arrange
        PhotoAngle angle1 = new PhotoAngle(45.0);
        PhotoAngle angle2 = new PhotoAngle(90.0);
        
        // Act & Assert
        assertNotEquals(angle1, angle2);
    }
    
    @Test
    void equals_withSameNamedAngle_shouldBeEqual() {
        // Arrange
        PhotoAngle angle1 = new PhotoAngle(NamedAngle.FRONT);
        PhotoAngle angle2 = new PhotoAngle(NamedAngle.FRONT);
        
        // Act & Assert
        assertEquals(angle1, angle2);
        assertEquals(angle1.hashCode(), angle2.hashCode());
    }
    
    @Test
    void equals_withDifferentNamedAngle_shouldNotBeEqual() {
        // Arrange
        PhotoAngle angle1 = new PhotoAngle(NamedAngle.FRONT);
        PhotoAngle angle2 = new PhotoAngle(NamedAngle.BACK);
        
        // Act & Assert
        assertNotEquals(angle1, angle2);
    }
    
    @Test
    void equals_withSameDegreesButDifferentNamedAngle_shouldNotBeEqual() {
        // Arrange
        PhotoAngle angle1 = new PhotoAngle(0.0);
        PhotoAngle angle2 = new PhotoAngle(NamedAngle.FRONT);
        
        // Act & Assert
        assertNotEquals(angle1, angle2);
    }
}