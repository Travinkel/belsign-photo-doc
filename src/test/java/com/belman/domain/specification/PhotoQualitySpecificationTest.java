package com.belman.domain.specification;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.*;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.security.HashedPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PhotoQualitySpecification class, focusing on metadata validation.
 */
public class PhotoQualitySpecificationTest {

    private PhotoQualitySpecification specification;
    private PhotoDocument validPhotoDocument;
    private PhotoMetadata validMetadata;
    private PhotoAnnotation testAnnotation;

    @BeforeEach
    void setUp() {
        // Create the specification
        specification = PhotoQualitySpecification.create();

        // Create a valid photo metadata
        validMetadata = new PhotoMetadata(
                1920, 1080, 500 * 1024, // 500KB
                "JPEG", "RGB", 300);

        // Create a test annotation
        testAnnotation = new PhotoAnnotation(
                "anno1", 0.1, 0.2, "Test annotation",
                PhotoAnnotation.AnnotationType.MEASUREMENT);

        // Create a valid photo document
        PhotoId photoId = new PhotoId(UUID.randomUUID().toString());
        PhotoTemplate template = PhotoTemplate.TOP_VIEW_OF_JOINT;
        Photo photo = new Photo("test/path/image.jpg");
        UserBusiness uploadedBy = new UserBusiness.Builder()
                .id(new UserId(UUID.randomUUID().toString()))
                .username(new Username("testuser"))
                .password(new HashedPassword("hashedpassword123"))
                .email(new EmailAddress("test@example.com"))
                .build();
        Timestamp uploadedAt = new Timestamp(Instant.now());
        OrderId orderId = new OrderId("ORD123");

        List<PhotoAnnotation> annotations = new ArrayList<>();
        annotations.add(testAnnotation);

        validPhotoDocument = PhotoDocument.builder()
                .photoId(photoId)
                .template(template)
                .imagePath(photo)
                .uploadedBy(uploadedBy)
                .uploadedAt(uploadedAt)
                .orderId(orderId)
                .annotations(annotations)
                .metadata(validMetadata)
                .build();
    }

    @Test
    void testValidPhotoDocument() {
        // Test with a valid photo document
        boolean result = specification.isSatisfiedBy(validPhotoDocument);
        assertTrue(result, "Valid photo document should satisfy the specification");
        assertTrue(specification.getValidationMessages().isEmpty(),
                "No validation messages should be present for valid photo");
    }

    @Test
    void testMissingMetadata() {
        // Create a photo document without metadata
        PhotoDocument photoWithoutMetadata = PhotoDocument.builder()
                .photoId(validPhotoDocument.getId())
                .template(validPhotoDocument.getTemplate())
                .imagePath(validPhotoDocument.getImagePath())
                .uploadedBy(validPhotoDocument.getUploadedBy())
                .uploadedAt(validPhotoDocument.getUploadedAt())
                .orderId(validPhotoDocument.getOrderId())
                .annotations(validPhotoDocument.getAnnotations())
                .build();

        boolean result = specification.isSatisfiedBy(photoWithoutMetadata);
        assertFalse(result, "Photo document without metadata should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertEquals(1, messages.size(), "Should have one validation message");
        assertTrue(messages.get(0).contains("metadata must be provided"),
                "Validation message should mention missing metadata");
    }

    @Test
    void testLowResolution() {
        // Create metadata with low resolution
        PhotoMetadata lowResMetadata = new PhotoMetadata(
                800, 600, 500 * 1024,
                "JPEG", "RGB", 300);

        // Update the photo document with low resolution metadata
        validPhotoDocument.setMetadata(lowResMetadata);

        boolean result = specification.isSatisfiedBy(validPhotoDocument);
        assertFalse(result, "Photo document with low resolution should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("resolution must be at least")),
                "Validation message should mention resolution requirement");
    }

    @Test
    void testSmallFileSize() {
        // Create metadata with small file size
        PhotoMetadata smallSizeMetadata = new PhotoMetadata(
                1920, 1080, 50 * 1024, // 50KB (below 100KB minimum)
                "JPEG", "RGB", 300);

        // Update the photo document with small file size metadata
        validPhotoDocument.setMetadata(smallSizeMetadata);

        boolean result = specification.isSatisfiedBy(validPhotoDocument);
        assertFalse(result, "Photo document with small file size should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("file size must be at least")),
                "Validation message should mention minimum file size");
    }

    @Test
    void testLargeFileSize() {
        // Create metadata with large file size
        PhotoMetadata largeSizeMetadata = new PhotoMetadata(
                1920, 1080, 15 * 1024 * 1024, // 15MB (above 10MB maximum)
                "JPEG", "RGB", 300);

        // Update the photo document with large file size metadata
        validPhotoDocument.setMetadata(largeSizeMetadata);

        boolean result = specification.isSatisfiedBy(validPhotoDocument);
        assertFalse(result, "Photo document with large file size should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("file size must not exceed")),
                "Validation message should mention maximum file size");
    }

    @Test
    void testInvalidFormat() {
        // Create metadata with invalid format
        PhotoMetadata invalidFormatMetadata = new PhotoMetadata(
                1920, 1080, 500 * 1024,
                "GIF", "RGB", 300); // GIF is not allowed

        // Update the photo document with invalid format metadata
        validPhotoDocument.setMetadata(invalidFormatMetadata);

        boolean result = specification.isSatisfiedBy(validPhotoDocument);
        assertFalse(result, "Photo document with invalid format should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("format must be JPEG or PNG")),
                "Validation message should mention format requirements");
    }

    @Test
    void testInvalidColorSpace() {
        // Create metadata with invalid color space
        PhotoMetadata invalidColorSpaceMetadata = new PhotoMetadata(
                1920, 1080, 500 * 1024,
                "JPEG", "CMYK", 300); // CMYK is not allowed

        // Update the photo document with invalid color space metadata
        validPhotoDocument.setMetadata(invalidColorSpaceMetadata);

        boolean result = specification.isSatisfiedBy(validPhotoDocument);
        assertFalse(result, "Photo document with invalid color space should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("color space must be RGB")),
                "Validation message should mention color space requirements");
    }

    @Test
    void testLowDpi() {
        // Create metadata with low DPI
        PhotoMetadata lowDpiMetadata = new PhotoMetadata(
                1920, 1080, 500 * 1024,
                "JPEG", "RGB", 50); // 50 DPI is below minimum

        // Update the photo document with low DPI metadata
        validPhotoDocument.setMetadata(lowDpiMetadata);

        boolean result = specification.isSatisfiedBy(validPhotoDocument);
        assertFalse(result, "Photo document with low DPI should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("DPI must be at least")),
                "Validation message should mention DPI requirements");
    }

    @Test
    void testNullDpi() {
        // Create metadata with null DPI (optional field)
        PhotoMetadata nullDpiMetadata = new PhotoMetadata(
                1920, 1080, 500 * 1024,
                "JPEG", "RGB", null);

        // Update the photo document with null DPI metadata
        validPhotoDocument.setMetadata(nullDpiMetadata);

        boolean result = specification.isSatisfiedBy(validPhotoDocument);
        assertTrue(result, "Photo document with null DPI should satisfy the specification");
    }

    @Test
    void testMultipleValidationFailures() {
        // Create metadata with multiple issues
        PhotoMetadata badMetadata = new PhotoMetadata(
                800, 600, 50 * 1024,
                "GIF", "CMYK", 50);

        // Update the photo document with bad metadata
        validPhotoDocument.setMetadata(badMetadata);

        boolean result = specification.isSatisfiedBy(validPhotoDocument);
        assertFalse(result, "Photo document with multiple issues should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.size() >= 4, "Should have at least 4 validation messages");
    }
}