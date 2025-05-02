package com.belman.domain.valueobjects;

/**
 * Value object representing the file system path to an image.
 */
public record ImagePath(String path) {
    public ImagePath {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Image path cannot be null or blank");
        }
    }

    public String getValue() {
        return path;
    }
}
