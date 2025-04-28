package com.belman.belsign.domain.model.order.photodocument;

/**
 * Value object representing the file system path to an image.
 */
public final class ImagePath {
    private final String path;

    public ImagePath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Image path cannot be null or blank");
        }
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImagePath imagePath = (ImagePath) o;
        return path.equals(imagePath.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
