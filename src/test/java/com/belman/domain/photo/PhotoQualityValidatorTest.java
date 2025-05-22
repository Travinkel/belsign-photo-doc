package com.belman.domain.photo;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PhotoQualityValidator class.
 */
public class PhotoQualityValidatorTest {

    @Test
    void testValidPhotoMetadata() {
        // Create metadata that meets all quality standards
        PhotoMetadata validMetadata = new PhotoMetadata(
                2000, 1500, // 3 megapixels
                5 * 1024 * 1024, // 5 MB
                "JPEG",
                "RGB",
                300 // 300 DPI
        );
        
        // Validate the metadata
        List<String> validationErrors = PhotoQualityValidator.validate(validMetadata);
        
        // Assert that there are no validation errors
        assertTrue(validationErrors.isEmpty(), "Valid metadata should have no validation errors");
        assertTrue(PhotoQualityValidator.isValid(validMetadata), "Valid metadata should be considered valid");
    }
    
    @Test
    void testLowResolutionPhoto() {
        // Create metadata with low resolution
        PhotoMetadata lowResMetadata = new PhotoMetadata(
                1000, 800, // 0.8 megapixels (below 2.0 minimum)
                5 * 1024 * 1024,
                "JPEG",
                "RGB",
                300
        );
        
        // Validate the metadata
        List<String> validationErrors = PhotoQualityValidator.validate(lowResMetadata);
        
        // Assert that there is a validation error for low resolution
        assertEquals(1, validationErrors.size(), "Should have one validation error");
        assertTrue(validationErrors.get(0).contains("Resolution too low"), 
                "Error message should mention low resolution");
        assertFalse(PhotoQualityValidator.isValid(lowResMetadata), 
                "Low resolution metadata should be considered invalid");
    }
    
    @Test
    void testLargeFileSize() {
        // Create metadata with large file size
        PhotoMetadata largeFileMetadata = new PhotoMetadata(
                2000, 1500,
                15 * 1024 * 1024, // 15 MB (above 10 MB maximum)
                "JPEG",
                "RGB",
                300
        );
        
        // Validate the metadata
        List<String> validationErrors = PhotoQualityValidator.validate(largeFileMetadata);
        
        // Assert that there is a validation error for large file size
        assertEquals(1, validationErrors.size(), "Should have one validation error");
        assertTrue(validationErrors.get(0).contains("File size too large"), 
                "Error message should mention large file size");
        assertFalse(PhotoQualityValidator.isValid(largeFileMetadata), 
                "Large file metadata should be considered invalid");
    }
    
    @Test
    void testLowDpi() {
        // Create metadata with low DPI
        PhotoMetadata lowDpiMetadata = new PhotoMetadata(
                2000, 1500,
                5 * 1024 * 1024,
                "JPEG",
                "RGB",
                50 // 50 DPI (below 72 minimum)
        );
        
        // Validate the metadata
        List<String> validationErrors = PhotoQualityValidator.validate(lowDpiMetadata);
        
        // Assert that there is a validation error for low DPI
        assertEquals(1, validationErrors.size(), "Should have one validation error");
        assertTrue(validationErrors.get(0).contains("DPI too low"), 
                "Error message should mention low DPI");
        assertFalse(PhotoQualityValidator.isValid(lowDpiMetadata), 
                "Low DPI metadata should be considered invalid");
    }
    
    @Test
    void testUnsupportedImageFormat() {
        // Create metadata with unsupported image format
        PhotoMetadata unsupportedFormatMetadata = new PhotoMetadata(
                2000, 1500,
                5 * 1024 * 1024,
                "BMP", // Unsupported format
                "RGB",
                300
        );
        
        // Validate the metadata
        List<String> validationErrors = PhotoQualityValidator.validate(unsupportedFormatMetadata);
        
        // Assert that there is a validation error for unsupported format
        assertEquals(1, validationErrors.size(), "Should have one validation error");
        assertTrue(validationErrors.get(0).contains("Unsupported image format"), 
                "Error message should mention unsupported format");
        assertFalse(PhotoQualityValidator.isValid(unsupportedFormatMetadata), 
                "Unsupported format metadata should be considered invalid");
    }
    
    @Test
    void testUnsupportedColorSpace() {
        // Create metadata with unsupported color space
        PhotoMetadata unsupportedColorSpaceMetadata = new PhotoMetadata(
                2000, 1500,
                5 * 1024 * 1024,
                "JPEG",
                "CMYK", // Unsupported color space
                300
        );
        
        // Validate the metadata
        List<String> validationErrors = PhotoQualityValidator.validate(unsupportedColorSpaceMetadata);
        
        // Assert that there is a validation error for unsupported color space
        assertEquals(1, validationErrors.size(), "Should have one validation error");
        assertTrue(validationErrors.get(0).contains("Unsupported color space"), 
                "Error message should mention unsupported color space");
        assertFalse(PhotoQualityValidator.isValid(unsupportedColorSpaceMetadata), 
                "Unsupported color space metadata should be considered invalid");
    }
    
    @Test
    void testMultipleValidationErrors() {
        // Create metadata with multiple issues
        PhotoMetadata multipleIssuesMetadata = new PhotoMetadata(
                1000, 800, // Low resolution
                15 * 1024 * 1024, // Large file size
                "BMP", // Unsupported format
                "CMYK", // Unsupported color space
                50 // Low DPI
        );
        
        // Validate the metadata
        List<String> validationErrors = PhotoQualityValidator.validate(multipleIssuesMetadata);
        
        // Assert that there are multiple validation errors
        assertEquals(5, validationErrors.size(), "Should have five validation errors");
        assertFalse(PhotoQualityValidator.isValid(multipleIssuesMetadata), 
                "Metadata with multiple issues should be considered invalid");
    }
    
    @Test
    void testNullMetadata() {
        // Assert that validate() throws NullPointerException for null metadata
        assertThrows(NullPointerException.class, () -> {
            PhotoQualityValidator.validate(null);
        }, "validate() should throw NullPointerException for null metadata");
        
        // Assert that isValid() throws NullPointerException for null metadata
        assertThrows(NullPointerException.class, () -> {
            PhotoQualityValidator.isValid(null);
        }, "isValid() should throw NullPointerException for null metadata");
    }
}