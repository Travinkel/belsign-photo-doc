package com.belman.unit.domain.valueobjects;

import com.belman.business.domain.order.photo.PhotoTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PhotoTemplate value object.
 * 
 * Note: This class was previously testing PhotoAngle, but has been updated to test PhotoTemplate
 * as PhotoAngle has been replaced by PhotoTemplate in the system. This change was made because
 * production workers will get a list of pictures that need to be taken, such that taking a picture
 * will simply have them go to the next screen.
 */
public class PhotoTemplateTest {

    @Test
    void constructor_withValidNameAndDescription_shouldCreatePhotoTemplate() {
        // Act
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description");

        // Assert
        assertEquals("TEST_TEMPLATE", template.name());
        assertEquals("Test description", template.description());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void constructor_withInvalidName_shouldThrowException(String name) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PhotoTemplate(name, "Valid description");
        });

        assertEquals("Template name must not be null or blank.", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void constructor_withInvalidDescription_shouldThrowException(String description) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new PhotoTemplate("VALID_NAME", description);
        });

        assertEquals("Template description must not be null or blank.", exception.getMessage());
    }

    @Test
    void predefinedTemplates_shouldHaveCorrectValues() {
        // Assert for TOP_VIEW_OF_JOINT
        assertEquals("TOP_VIEW_OF_JOINT", PhotoTemplate.TOP_VIEW_OF_JOINT.name());
        assertTrue(PhotoTemplate.TOP_VIEW_OF_JOINT.description().contains("above"));

        // Assert for FRONT_VIEW_OF_ASSEMBLY
        assertEquals("FRONT_VIEW_OF_ASSEMBLY", PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY.name());
        assertTrue(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY.description().contains("front"));

        // Assert for BACK_VIEW_OF_ASSEMBLY
        assertEquals("BACK_VIEW_OF_ASSEMBLY", PhotoTemplate.BACK_VIEW_OF_ASSEMBLY.name());
        assertTrue(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY.description().contains("back"));

        // Assert for LEFT_VIEW_OF_ASSEMBLY
        assertEquals("LEFT_VIEW_OF_ASSEMBLY", PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY.name());
        assertTrue(PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY.description().contains("left"));

        // Assert for RIGHT_VIEW_OF_ASSEMBLY
        assertEquals("RIGHT_VIEW_OF_ASSEMBLY", PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY.name());
        assertTrue(PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY.description().contains("right"));
    }

    @Test
    void toString_shouldReturnNameAndDescription() {
        // Arrange
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description");

        // Act
        String result = template.toString();

        // Assert
        assertEquals("TEST_TEMPLATE: Test description", result);
    }

    @Test
    void equals_withSameValues_shouldBeEqual() {
        // Arrange
        PhotoTemplate template1 = new PhotoTemplate("TEST_TEMPLATE", "Test description");
        PhotoTemplate template2 = new PhotoTemplate("TEST_TEMPLATE", "Test description");

        // Act & Assert
        assertEquals(template1, template2);
        assertEquals(template1.hashCode(), template2.hashCode());
    }

    @Test
    void equals_withDifferentValues_shouldNotBeEqual() {
        // Arrange
        PhotoTemplate template1 = new PhotoTemplate("TEMPLATE_1", "Description 1");
        PhotoTemplate template2 = new PhotoTemplate("TEMPLATE_2", "Description 2");

        // Act & Assert
        assertNotEquals(template1, template2);
    }

    @Test
    void equals_withDifferentDescriptions_shouldNotBeEqual() {
        // Arrange
        PhotoTemplate template1 = new PhotoTemplate("SAME_NAME", "Description 1");
        PhotoTemplate template2 = new PhotoTemplate("SAME_NAME", "Description 2");

        // Act & Assert
        assertNotEquals(template1, template2);
    }
}
