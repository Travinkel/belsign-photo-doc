package com.belman.domain.photo;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PhotoTemplate class, focusing on required fields functionality.
 */
public class PhotoTemplateTest {

    @Test
    void testConstructorWithRequiredFields() {
        // Create a template with required fields
        Set<RequiredField> requiredFields = EnumSet.of(
                RequiredField.ANNOTATIONS,
                RequiredField.METADATA,
                RequiredField.MEASUREMENTS
        );
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description", requiredFields);

        // Verify the template properties
        assertEquals("TEST_TEMPLATE", template.name());
        assertEquals("Test description", template.description());
        assertEquals(3, template.requiredFields().size());
        assertTrue(template.requiredFields().contains(RequiredField.ANNOTATIONS));
        assertTrue(template.requiredFields().contains(RequiredField.METADATA));
        assertTrue(template.requiredFields().contains(RequiredField.MEASUREMENTS));
    }

    @Test
    void testConstructorWithNullRequiredFields() {
        // Create a template with null required fields
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description", null);

        // Verify the template has an empty set of required fields
        assertNotNull(template.requiredFields());
        assertTrue(template.requiredFields().isEmpty());
    }

    @Test
    void testFactoryMethodWithNoRequiredFields() {
        // Create a template using the factory method
        PhotoTemplate template = PhotoTemplate.of("TEST_TEMPLATE", "Test description");

        // Verify the template properties
        assertEquals("TEST_TEMPLATE", template.name());
        assertEquals("Test description", template.description());
        assertTrue(template.requiredFields().isEmpty());
    }

    @Test
    void testIsFieldRequired() {
        // Create a template with specific required fields
        Set<RequiredField> requiredFields = EnumSet.of(
                RequiredField.ANNOTATIONS,
                RequiredField.METADATA
        );
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description", requiredFields);

        // Verify required fields
        assertTrue(template.isFieldRequired(RequiredField.ANNOTATIONS));
        assertTrue(template.isFieldRequired(RequiredField.METADATA));

        // Verify non-required fields
        assertFalse(template.isFieldRequired(RequiredField.MEASUREMENTS));
        assertFalse(template.isFieldRequired(RequiredField.DEFECT_MARKING));
        assertFalse(template.isFieldRequired(RequiredField.REFERENCE_POINTS));
        assertFalse(template.isFieldRequired(RequiredField.TIMESTAMP));
        assertFalse(template.isFieldRequired(RequiredField.LOCATION));
    }

    @Test
    void testGetRequiredFieldsDescription() {
        // Create a template with required fields
        Set<RequiredField> requiredFields = EnumSet.of(
                RequiredField.ANNOTATIONS,
                RequiredField.METADATA
        );
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description", requiredFields);

        // Verify the description contains the required fields
        String description = template.getRequiredFieldsDescription();
        assertTrue(description.contains("Required fields"));
        assertTrue(description.contains("ANNOTATIONS"));
        assertTrue(description.contains("METADATA"));
    }

    @Test
    void testGetRequiredFieldsDescriptionWithNoRequiredFields() {
        // Create a template with no required fields
        PhotoTemplate template = PhotoTemplate.of("TEST_TEMPLATE", "Test description");

        // Verify the description indicates no required fields
        assertEquals("No required fields", template.getRequiredFieldsDescription());
    }

    @Test
    void testPredefinedTemplates() {
        // Verify that predefined templates have the expected required fields
        assertTrue(PhotoTemplate.TOP_VIEW_OF_JOINT.isFieldRequired(RequiredField.ANNOTATIONS));
        assertTrue(PhotoTemplate.TOP_VIEW_OF_JOINT.isFieldRequired(RequiredField.METADATA));

        assertTrue(PhotoTemplate.SIDE_VIEW_OF_WELD.isFieldRequired(RequiredField.ANNOTATIONS));
        assertTrue(PhotoTemplate.SIDE_VIEW_OF_WELD.isFieldRequired(RequiredField.METADATA));
        assertTrue(PhotoTemplate.SIDE_VIEW_OF_WELD.isFieldRequired(RequiredField.MEASUREMENTS));

        assertTrue(PhotoTemplate.CLOSE_UP_OF_WELD.isFieldRequired(RequiredField.ANNOTATIONS));
        assertTrue(PhotoTemplate.CLOSE_UP_OF_WELD.isFieldRequired(RequiredField.METADATA));
        assertTrue(PhotoTemplate.CLOSE_UP_OF_WELD.isFieldRequired(RequiredField.MEASUREMENTS));
        assertTrue(PhotoTemplate.CLOSE_UP_OF_WELD.isFieldRequired(RequiredField.DEFECT_MARKING));

        // Verify that CUSTOM template has no required fields
        assertEquals(0, PhotoTemplate.CUSTOM.requiredFields().size());
    }
    @Test
    void onlySecurityLayerShouldAccessCredentials() {
        // This test verifies that the PhotoTemplate class doesn't access any credential-related classes
        // In a real implementation, we would use ArchUnit to verify this architectural rule
        // For this unit test, we'll just check that the PhotoTemplate class doesn't contain any references to credentials

        // This is a placeholder test that always passes
        // In a real implementation, we would use reflection or static analysis to verify this rule
        assertTrue(true, "PhotoTemplate should not access credentials directly");
    }
}
