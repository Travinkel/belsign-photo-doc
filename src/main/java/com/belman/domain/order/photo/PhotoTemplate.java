package com.belman.domain.order.photo;

/**
 * Value object representing a photo template for workers to follow.
 * This replaces the concept of angles with predefined templates
 * that describe the required photo positions in an intuitive way.
 */
public record PhotoTemplate(String name, String description) {

    /**
     * Template: Top view of the joint.
     */
    public static final PhotoTemplate TOP_VIEW_OF_JOINT = new PhotoTemplate("TOP_VIEW_OF_JOINT", "Take a photo from above the joint.");

    /**
     * Template: Side view of the weld.
     */
    public static final PhotoTemplate SIDE_VIEW_OF_WELD = new PhotoTemplate("SIDE_VIEW_OF_WELD", "Take a photo from the side of the weld.");

    /**
     * Template: Front view of the assembly.
     */
    public static final PhotoTemplate FRONT_VIEW_OF_ASSEMBLY = new PhotoTemplate("FRONT_VIEW_OF_ASSEMBLY", "Take a photo from the front of the assembly.");

    /**
     * Template: Back view of the assembly.
     */
    public static final PhotoTemplate BACK_VIEW_OF_ASSEMBLY = new PhotoTemplate("BACK_VIEW_OF_ASSEMBLY", "Take a photo from the back of the assembly.");

    /**
     * Template: Custom template for specific instructions.
     */
    public static final PhotoTemplate CUSTOM = new PhotoTemplate("CUSTOM", "Follow specific instructions provided.");

    /**
     * Creates a new PhotoTemplate with the specified name and description.
     *
     * @param name        the name of the template (e.g., "TOP_VIEW_OF_JOINT")
     * @param description a description of the template for workers
     * @throws IllegalArgumentException if name or description is null or blank
     */
    public PhotoTemplate {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Template name must not be null or blank.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Template description must not be null or blank.");
        }
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
}
