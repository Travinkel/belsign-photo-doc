package com.belman.domain.photo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for validating photo quality based on metadata.
 * <p>
 * This class defines quality standards for photos in the BelSign system and
 * provides methods to check if a photo meets these standards. It is used to
 * ensure that photos uploaded to the system meet minimum quality requirements
 * for documentation purposes.
 */
public final class PhotoQualityValidator {

    // Minimum acceptable resolution in megapixels
    private static final double MIN_MEGAPIXELS = 2.0;
    
    // Maximum acceptable file size in bytes (10 MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // Minimum acceptable DPI (dots per inch)
    private static final int MIN_DPI = 72;
    
    // Acceptable image formats
    private static final List<String> ACCEPTABLE_FORMATS = Arrays.asList(
            "JPEG", "JPG", "PNG", "TIFF", "TIF");
    
    // Acceptable color spaces
    private static final List<String> ACCEPTABLE_COLOR_SPACES = Arrays.asList(
            "RGB", "sRGB", "Adobe RGB");

    // Private constructor to prevent instantiation
    private PhotoQualityValidator() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Validates the quality of a photo based on its metadata.
     * 
     * @param metadata the metadata to validate
     * @return a list of validation errors, or an empty list if the photo meets all quality standards
     * @throws NullPointerException if metadata is null
     */
    public static List<String> validate(PhotoMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        
        List<String> validationErrors = new ArrayList<>();
        
        // Check resolution
        if (metadata.getMegapixels() < MIN_MEGAPIXELS) {
            validationErrors.add(String.format(
                    "Resolution too low: %.1f MP (minimum: %.1f MP)",
                    metadata.getMegapixels(), MIN_MEGAPIXELS));
        }
        
        // Check file size
        if (metadata.getFileSize() > MAX_FILE_SIZE) {
            validationErrors.add(String.format(
                    "File size too large: %d bytes (maximum: %d bytes)",
                    metadata.getFileSize(), MAX_FILE_SIZE));
        }
        
        // Check DPI if available
        if (metadata.getDpi() != null && metadata.getDpi() < MIN_DPI) {
            validationErrors.add(String.format(
                    "DPI too low: %d (minimum: %d)",
                    metadata.getDpi(), MIN_DPI));
        }
        
        // Check image format
        String normalizedFormat = metadata.getImageFormat().toUpperCase();
        if (!ACCEPTABLE_FORMATS.contains(normalizedFormat)) {
            validationErrors.add(String.format(
                    "Unsupported image format: %s (supported: %s)",
                    metadata.getImageFormat(), String.join(", ", ACCEPTABLE_FORMATS)));
        }
        
        // Check color space
        if (!ACCEPTABLE_COLOR_SPACES.contains(metadata.getColorSpace())) {
            validationErrors.add(String.format(
                    "Unsupported color space: %s (supported: %s)",
                    metadata.getColorSpace(), String.join(", ", ACCEPTABLE_COLOR_SPACES)));
        }
        
        return validationErrors;
    }
    
    /**
     * Checks if a photo meets all quality standards based on its metadata.
     * 
     * @param metadata the metadata to validate
     * @return true if the photo meets all quality standards, false otherwise
     * @throws NullPointerException if metadata is null
     */
    public static boolean isValid(PhotoMetadata metadata) {
        return validate(metadata).isEmpty();
    }
}