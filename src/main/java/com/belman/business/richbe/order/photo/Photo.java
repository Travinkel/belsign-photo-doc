package com.belman.business.richbe.order.photo;

/**
 * Value object representing the path to an image file.
 * This is specific to the photo bounded context.
 */
public record Photo(String value) {

    /**
     * Creates a new ImagePath with the specified value.
     *
     * @param value the image path value
     * @throws IllegalArgumentException if the value is null or empty
     */
    public Photo {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Image path must not be null or blank");
        }
    }

    public String path() {
        return null;
    }
}