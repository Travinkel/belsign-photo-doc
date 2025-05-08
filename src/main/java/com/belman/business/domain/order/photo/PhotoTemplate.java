package com.belman.business.domain.order.photo;

/**
 * Value object representing a photo template for workers to follow.
 * This replaces the concept of angles with predefined templates
 * that describe the required photo positions in an intuitive way.
 */
public record PhotoTemplate(String name, String description) {

    // Templates for standard views
    public static final PhotoTemplate TOP_VIEW_OF_JOINT = new PhotoTemplate("TOP_VIEW_OF_JOINT", "Take a photo from above the joint.");
    public static final PhotoTemplate SIDE_VIEW_OF_WELD = new PhotoTemplate("SIDE_VIEW_OF_WELD", "Take a photo from the side of the weld.");
    public static final PhotoTemplate FRONT_VIEW_OF_ASSEMBLY = new PhotoTemplate("FRONT_VIEW_OF_ASSEMBLY", "Take a photo from the front of the assembly.");
    public static final PhotoTemplate BACK_VIEW_OF_ASSEMBLY = new PhotoTemplate("BACK_VIEW_OF_ASSEMBLY", "Take a photo from the back of the assembly.");
    public static final PhotoTemplate LEFT_VIEW_OF_ASSEMBLY = new PhotoTemplate("LEFT_VIEW_OF_ASSEMBLY", "Take a photo from the left side of the assembly.");
    public static final PhotoTemplate RIGHT_VIEW_OF_ASSEMBLY = new PhotoTemplate("RIGHT_VIEW_OF_ASSEMBLY", "Take a photo from the right side of the assembly.");
    public static final PhotoTemplate BOTTOM_VIEW_OF_ASSEMBLY = new PhotoTemplate("BOTTOM_VIEW_OF_ASSEMBLY", "Take a photo from below the assembly.");

    // Templates for detailed views
    public static final PhotoTemplate CLOSE_UP_OF_WELD = new PhotoTemplate("CLOSE_UP_OF_WELD", "Take a close-up photo of the weld.");
    public static final PhotoTemplate ANGLED_VIEW_OF_JOINT = new PhotoTemplate("ANGLED_VIEW_OF_JOINT", "Take a photo of the joint from an angled perspective.");
    public static final PhotoTemplate OVERVIEW_OF_ASSEMBLY = new PhotoTemplate("OVERVIEW_OF_ASSEMBLY", "Take an overview photo of the entire assembly.");

    // Templates for custom or additional requirements
    public static final PhotoTemplate CUSTOM = new PhotoTemplate("CUSTOM", "Follow specific instructions provided for this photo.");

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
