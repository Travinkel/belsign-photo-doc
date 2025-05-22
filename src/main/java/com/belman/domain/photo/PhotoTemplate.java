package com.belman.domain.photo;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Value object representing a photo template for workers to follow.
 * This replaces the concept of angles with predefined templates
 * that describe the required photo positions in an intuitive way.
 * Each template can specify required fields that must be present in photos
 * taken using this template.
 */
public record PhotoTemplate(String name, String description, Set<RequiredField> requiredFields) {

    // Templates for standard views
    public static final PhotoTemplate TOP_VIEW_OF_JOINT = new PhotoTemplate("TOP_VIEW_OF_JOINT",
            "Take a photo from above the joint.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA));
    public static final PhotoTemplate SIDE_VIEW_OF_WELD = new PhotoTemplate("SIDE_VIEW_OF_WELD",
            "Take a photo from the side of the weld.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA, RequiredField.MEASUREMENTS));
    public static final PhotoTemplate FRONT_VIEW_OF_ASSEMBLY = new PhotoTemplate("FRONT_VIEW_OF_ASSEMBLY",
            "Take a photo from the front of the assembly.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA));
    public static final PhotoTemplate BACK_VIEW_OF_ASSEMBLY = new PhotoTemplate("BACK_VIEW_OF_ASSEMBLY",
            "Take a photo from the back of the assembly.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA));
    public static final PhotoTemplate LEFT_VIEW_OF_ASSEMBLY = new PhotoTemplate("LEFT_VIEW_OF_ASSEMBLY",
            "Take a photo from the left side of the assembly.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA));
    public static final PhotoTemplate RIGHT_VIEW_OF_ASSEMBLY = new PhotoTemplate("RIGHT_VIEW_OF_ASSEMBLY",
            "Take a photo from the right side of the assembly.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA));
    public static final PhotoTemplate BOTTOM_VIEW_OF_ASSEMBLY = new PhotoTemplate("BOTTOM_VIEW_OF_ASSEMBLY",
            "Take a photo from below the assembly.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA));

    // Templates for detailed views
    public static final PhotoTemplate CLOSE_UP_OF_WELD = new PhotoTemplate("CLOSE_UP_OF_WELD",
            "Take a close-up photo of the weld.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA, RequiredField.MEASUREMENTS, RequiredField.DEFECT_MARKING));
    public static final PhotoTemplate ANGLED_VIEW_OF_JOINT = new PhotoTemplate("ANGLED_VIEW_OF_JOINT",
            "Take a photo of the joint from an angled perspective.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA, RequiredField.MEASUREMENTS));
    public static final PhotoTemplate OVERVIEW_OF_ASSEMBLY = new PhotoTemplate("OVERVIEW_OF_ASSEMBLY",
            "Take an overview photo of the entire assembly.",
            EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA));

    // Templates for custom or additional requirements
    public static final PhotoTemplate CUSTOM = new PhotoTemplate("CUSTOM",
            "Follow specific instructions provided for this photo.",
            EnumSet.noneOf(RequiredField.class));

    /**
     * Creates a new PhotoTemplate with the specified name, description, and required fields.
     *
     * @param name           the name of the template (e.g., "TOP_VIEW_OF_JOINT")
     * @param description    a description of the template for workers
     * @param requiredFields a set of fields that are required for photos using this template
     * @throws IllegalArgumentException if name or description is null or blank
     */
    public PhotoTemplate {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Template name must not be null or blank.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Template description must not be null or blank.");
        }
        if (requiredFields == null) {
            requiredFields = EnumSet.noneOf(RequiredField.class);
        }
        // Make the set unmodifiable to ensure immutability
        requiredFields = Collections.unmodifiableSet(requiredFields);
    }

    /**
     * Creates a new PhotoTemplate with the specified name and description, but no required fields.
     *
     * @param name        the name of the template
     * @param description a description of the template for workers
     * @return a new PhotoTemplate with no required fields
     */
    public static PhotoTemplate of(String name, String description) {
        return new PhotoTemplate(name, description, EnumSet.noneOf(RequiredField.class));
    }

    /**
     * Checks if a specific field is required for this template.
     *
     * @param field the field to check
     * @return true if the field is required, false otherwise
     */
    public boolean isFieldRequired(RequiredField field) {
        return requiredFields.contains(field);
    }

    /**
     * Returns a string representation of this photo template.
     *
     * @return a string representation of this photo template
     */
    @Override
    public String toString() {
        return name + ": " + description;
    }

    /**
     * Returns a string representation of the required fields for this template.
     *
     * @return a string representation of the required fields
     */
    public String getRequiredFieldsDescription() {
        if (requiredFields.isEmpty()) {
            return "No required fields";
        }
        return "Required fields: " + requiredFields;
    }
}
