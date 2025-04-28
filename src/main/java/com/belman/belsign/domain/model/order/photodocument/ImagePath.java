package com.belman.belsign.domain.model.order.photodocument;

import java.nio.file.Path;

public record ImagePath(Path path) {

    public ImagePath(Path path) {
        if (path == null) {
            throw new NullPointerException("Path must not be null.");
        }
        this.path = path;
    }

    public String filename() {
        // Check if the path is valid and not a directory ending with "/"
        Path fileName = path.getFileName();
        if (fileName == null || fileName.toString().isEmpty()) {
            return null; // Handle directories or root paths
        }
        return fileName.toString();
    }

    @Override
    public String toString() {
        return path.toString();
    }
}