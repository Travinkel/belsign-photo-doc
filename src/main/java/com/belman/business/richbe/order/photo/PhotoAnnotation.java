package com.belman.business.richbe.order.photo;

import java.util.Objects;

/**
 * Value object representing an annotation on a photo document.
 * Annotations are used to highlight specific areas of photos and provide
 * additional context or notes about those areas.
 */
public final class PhotoAnnotation {

    private final String id;
    private final double x;
    private final double y;
    private final String text;
    private final AnnotationType type;

    /**
     * Creates a new PhotoAnnotation with the specified details.
     *
     * @param id   unique identifier for this annotation
     * @param x    x-coordinate of the annotation (as percentage of image width, 0.0-1.0)
     * @param y    y-coordinate of the annotation (as percentage of image height, 0.0-1.0)
     * @param text the text content of the annotation
     * @param type the type of annotation
     */
    public PhotoAnnotation(String id, double x, double y, String text, AnnotationType type) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        validateCoordinate(x, "x");
        validateCoordinate(y, "y");
        this.x = x;
        this.y = y;
        this.text = Objects.requireNonNull(text, "text must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
    }

    private void validateCoordinate(double value, String name) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(
                    name + " coordinate must be between 0.0 and 1.0, but was " + value);
        }
    }

    /**
     * Returns the unique identifier for this annotation.
     *
     * @return the annotation ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of this annotation.
     *
     * @return the x-coordinate (as percentage of image width, 0.0-1.0)
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this annotation.
     *
     * @return the y-coordinate (as percentage of image height, 0.0-1.0)
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the text content of this annotation.
     *
     * @return the annotation text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the type of this annotation.
     *
     * @return the annotation type
     */
    public AnnotationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoAnnotation that = (PhotoAnnotation) o;
        return Double.compare(that.x, x) == 0 &&
               Double.compare(that.y, y) == 0 &&
               id.equals(that.id) &&
               text.equals(that.text) &&
               type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, text, type);
    }

    /**
     * Enumeration of annotation types.
     */
    public enum AnnotationType {
        NOTE,       // General note about a part of the photo
        ISSUE,      // Highlights a problem or defect
        HIGHLIGHT,  // Draws attention to an important feature
        MEASUREMENT // Indicates a measurement reference
    }
}