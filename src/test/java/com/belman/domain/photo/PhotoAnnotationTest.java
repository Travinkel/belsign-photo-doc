package com.belman.domain.photo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PhotoAnnotation class.
 * These tests verify that photo annotations can be created, validated, and manipulated correctly.
 * 
 * This test class demonstrates:
 * - AAA (Arrange-Act-Assert) pattern
 * - Parameterized testing
 * - State-based testing
 */
public class PhotoAnnotationTest {

    private static final String VALID_ID = "annotation-123";
    private static final double VALID_X = 0.5;
    private static final double VALID_Y = 0.5;
    private static final String VALID_TEXT = "Test annotation";
    private static final PhotoAnnotation.AnnotationType VALID_TYPE = PhotoAnnotation.AnnotationType.NOTE;

    /**
     * Test that a photo annotation can be created with valid parameters.
     */
    @Test
    @DisplayName("Photo annotation can be created with valid parameters")
    void testCreateAnnotation_WithValidParameters() {
        // Arrange - parameters defined as constants

        // Act
        PhotoAnnotation annotation = new PhotoAnnotation(VALID_ID, VALID_X, VALID_Y, VALID_TEXT, VALID_TYPE);

        // Assert
        assertNotNull(annotation);
        assertEquals(VALID_ID, annotation.getId());
        assertEquals(VALID_X, annotation.getX());
        assertEquals(VALID_Y, annotation.getY());
        assertEquals(VALID_TEXT, annotation.getText());
        assertEquals(VALID_TYPE, annotation.getType());
    }

    /**
     * Test that a photo annotation cannot be created with null ID.
     */
    @Test
    @DisplayName("Photo annotation cannot be created with null ID")
    void testCreateAnnotation_WithNullId_ThrowsException() {
        // Arrange - parameters defined as constants

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new PhotoAnnotation(null, VALID_X, VALID_Y, VALID_TEXT, VALID_TYPE);
        });
    }

    /**
     * Test that a photo annotation cannot be created with null text.
     */
    @Test
    @DisplayName("Photo annotation cannot be created with null text")
    void testCreateAnnotation_WithNullText_ThrowsException() {
        // Arrange - parameters defined as constants

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new PhotoAnnotation(VALID_ID, VALID_X, VALID_Y, null, VALID_TYPE);
        });
    }

    /**
     * Test that a photo annotation cannot be created with null type.
     */
    @Test
    @DisplayName("Photo annotation cannot be created with null type")
    void testCreateAnnotation_WithNullType_ThrowsException() {
        // Arrange - parameters defined as constants

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new PhotoAnnotation(VALID_ID, VALID_X, VALID_Y, VALID_TEXT, null);
        });
    }

    /**
     * Test that a photo annotation cannot be created with invalid coordinates.
     * This test uses parameterized testing to test multiple invalid coordinate combinations.
     */
    @ParameterizedTest
    @MethodSource("invalidCoordinatesProvider")
    @DisplayName("Photo annotation cannot be created with invalid coordinates")
    void testCreateAnnotation_WithInvalidCoordinates_ThrowsException(double x, double y) {
        // Arrange - parameters provided by method source

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new PhotoAnnotation(VALID_ID, x, y, VALID_TEXT, VALID_TYPE);
        });
    }

    /**
     * Test that a photo annotation cannot be created with empty text.
     */
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Photo annotation cannot be created with empty text")
    void testCreateAnnotation_WithEmptyText_ThrowsException(String text) {
        // Arrange - parameters provided by value source

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new PhotoAnnotation(VALID_ID, VALID_X, VALID_Y, text, VALID_TYPE);
        });
    }

    /**
     * Test that PhotoAnnotation is immutable by verifying that its properties cannot be changed.
     */
    @Test
    @DisplayName("PhotoAnnotation is immutable")
    void testPhotoAnnotationIsImmutable() {
        // Arrange
        PhotoAnnotation annotation = new PhotoAnnotation(VALID_ID, VALID_X, VALID_Y, VALID_TEXT, VALID_TYPE);

        // Act & Assert - Verify that the class is final
        assertTrue(PhotoAnnotation.class.getModifiers() == java.lang.reflect.Modifier.PUBLIC + java.lang.reflect.Modifier.FINAL,
                "PhotoAnnotation class should be final");

        // Verify that all fields are final
        java.lang.reflect.Field[] fields = PhotoAnnotation.class.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (!field.getType().isPrimitive() && !field.getType().isEnum()) {
                assertTrue(java.lang.reflect.Modifier.isFinal(field.getModifiers()),
                        "Field " + field.getName() + " should be final");
            }
        }
    }

    /**
     * Test that PhotoAnnotation equals and hashCode methods work correctly.
     */
    @Test
    @DisplayName("PhotoAnnotation equals and hashCode work correctly")
    void testEqualsAndHashCode() {
        // Arrange
        PhotoAnnotation annotation1 = new PhotoAnnotation(VALID_ID, VALID_X, VALID_Y, VALID_TEXT, VALID_TYPE);
        PhotoAnnotation annotation2 = new PhotoAnnotation(VALID_ID, VALID_X, VALID_Y, VALID_TEXT, VALID_TYPE);
        PhotoAnnotation differentId = new PhotoAnnotation("different-id", VALID_X, VALID_Y, VALID_TEXT, VALID_TYPE);
        PhotoAnnotation differentX = new PhotoAnnotation(VALID_ID, 0.75, VALID_Y, VALID_TEXT, VALID_TYPE);
        PhotoAnnotation differentY = new PhotoAnnotation(VALID_ID, VALID_X, 0.75, VALID_TEXT, VALID_TYPE);
        PhotoAnnotation differentText = new PhotoAnnotation(VALID_ID, VALID_X, VALID_Y, "Different text", VALID_TYPE);
        PhotoAnnotation differentType = new PhotoAnnotation(VALID_ID, VALID_X, VALID_Y, VALID_TEXT, PhotoAnnotation.AnnotationType.ISSUE);

        // Act & Assert - Verify equals
        assertEquals(annotation1, annotation2, "Equal annotations should be equal");
        assertNotEquals(annotation1, differentId, "Annotations with different IDs should not be equal");
        assertNotEquals(annotation1, differentX, "Annotations with different X coordinates should not be equal");
        assertNotEquals(annotation1, differentY, "Annotations with different Y coordinates should not be equal");
        assertNotEquals(annotation1, differentText, "Annotations with different text should not be equal");
        assertNotEquals(annotation1, differentType, "Annotations with different types should not be equal");

        // Verify hashCode
        assertEquals(annotation1.hashCode(), annotation2.hashCode(), "Equal annotations should have equal hash codes");
    }

    /**
     * Provides invalid coordinate combinations for parameterized tests.
     */
    private static Stream<Arguments> invalidCoordinatesProvider() {
        return Stream.of(
            Arguments.of(-0.1, 0.5),  // x < 0
            Arguments.of(1.1, 0.5),   // x > 1
            Arguments.of(0.5, -0.1),  // y < 0
            Arguments.of(0.5, 1.1)    // y > 1
        );
    }
}
