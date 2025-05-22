package com.belman.domain.specification;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.*;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PhotoQualitySpecification class, focusing on required fields validation.
 */
public class PhotoQualitySpecificationRequiredFieldsTest {

    private PhotoQualitySpecification specification;
    private UserBusiness testUser;
    private OrderId testOrderId;
    private PhotoId testPhotoId;
    private Photo testPhoto;
    private Timestamp testTimestamp;

    @BeforeEach
    void setUp() {
        // Create the specification
        specification = PhotoQualitySpecification.create();

        // Create common test objects
        testUser = new UserBusiness.Builder()
                .id(new UserId(UUID.randomUUID().toString()))
                .username(new Username("testuser"))
                .password(new HashedPassword("hashedpassword123"))
                .email(new EmailAddress("test@example.com"))
                .build();
        testOrderId = new OrderId("ORD123");
        testPhotoId = new PhotoId(UUID.randomUUID().toString());
        testPhoto = new Photo("test/path/image.jpg");
        testTimestamp = new Timestamp(Instant.now());
    }

    @Test
    void testPhotoWithAllRequiredFields() {
        // Create a template with required fields
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description",
                EnumSet.of(RequiredField.ANNOTATIONS, RequiredField.METADATA));

        // Create annotations
        List<PhotoAnnotation> annotations = new ArrayList<>();
        annotations.add(new PhotoAnnotation(
                "anno1", 0.1, 0.2, "Test annotation",
                PhotoAnnotation.AnnotationType.NOTE));

        // Create metadata
        PhotoMetadata metadata = new PhotoMetadata(
                1920, 1080, 500 * 1024,
                "JPEG", "RGB", 300);

        // Create a photo document with all required fields
        PhotoDocument photoDocument = PhotoDocument.builder()
                .photoId(testPhotoId)
                .template(template)
                .imagePath(testPhoto)
                .uploadedBy(testUser)
                .uploadedAt(testTimestamp)
                .orderId(testOrderId)
                .annotations(annotations)
                .metadata(metadata)
                .build();

        // Verify that the photo satisfies the specification
        boolean result = specification.isSatisfiedBy(photoDocument);
        assertTrue(result, "Photo with all required fields should satisfy the specification");
        assertTrue(specification.getValidationMessages().isEmpty(),
                "No validation messages should be present for valid photo");
    }

    @Test
    void testPhotoMissingRequiredAnnotations() {
        // Create a template requiring annotations
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description",
                EnumSet.of(RequiredField.ANNOTATIONS));

        // Create metadata
        PhotoMetadata metadata = new PhotoMetadata(
                1920, 1080, 500 * 1024,
                "JPEG", "RGB", 300);

        // Create a photo document without annotations
        PhotoDocument photoDocument = PhotoDocument.builder()
                .photoId(testPhotoId)
                .template(template)
                .imagePath(testPhoto)
                .uploadedBy(testUser)
                .uploadedAt(testTimestamp)
                .orderId(testOrderId)
                .metadata(metadata)
                .build();

        // Verify that the photo does not satisfy the specification
        boolean result = specification.isSatisfiedBy(photoDocument);
        assertFalse(result, "Photo missing required annotations should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("must have at least one annotation")),
                "Validation message should mention missing annotations");
    }

    @Test
    void testPhotoMissingRequiredMetadata() {
        // Create a template requiring metadata
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description",
                EnumSet.of(RequiredField.METADATA));

        // Create annotations
        List<PhotoAnnotation> annotations = new ArrayList<>();
        annotations.add(new PhotoAnnotation(
                "anno1", 0.1, 0.2, "Test annotation",
                PhotoAnnotation.AnnotationType.NOTE));

        // Create a photo document without metadata
        PhotoDocument photoDocument = PhotoDocument.builder()
                .photoId(testPhotoId)
                .template(template)
                .imagePath(testPhoto)
                .uploadedBy(testUser)
                .uploadedAt(testTimestamp)
                .orderId(testOrderId)
                .annotations(annotations)
                .build();

        // Verify that the photo does not satisfy the specification
        boolean result = specification.isSatisfiedBy(photoDocument);
        assertFalse(result, "Photo missing required metadata should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("must include metadata as required by template")),
                "Validation message should mention missing metadata");
    }

    @Test
    void testPhotoMissingRequiredMeasurements() {
        // Create a template requiring measurements
        PhotoTemplate template = new PhotoTemplate("TEST_TEMPLATE", "Test description",
                EnumSet.of(RequiredField.MEASUREMENTS));

        // Create annotations (but not measurement annotations)
        List<PhotoAnnotation> annotations = new ArrayList<>();
        annotations.add(new PhotoAnnotation(
                "anno1", 0.1, 0.2, "Test annotation",
                PhotoAnnotation.AnnotationType.NOTE));

        // Create metadata
        PhotoMetadata metadata = new PhotoMetadata(
                1920, 1080, 500 * 1024,
                "JPEG", "RGB", 300);

        // Create a photo document without measurement annotations
        PhotoDocument photoDocument = PhotoDocument.builder()
                .photoId(testPhotoId)
                .template(template)
                .imagePath(testPhoto)
                .uploadedBy(testUser)
                .uploadedAt(testTimestamp)
                .orderId(testOrderId)
                .annotations(annotations)
                .metadata(metadata)
                .build();

        // Verify that the photo does not satisfy the specification
        boolean result = specification.isSatisfiedBy(photoDocument);
        assertFalse(result, "Photo missing required measurements should not satisfy the specification");
        
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("must include measurement annotations")),
                "Validation message should mention missing measurements");
    }

    @Test
    void testPhotoWithNoRequiredFields() {
        // Create a template with no required fields
        PhotoTemplate template = PhotoTemplate.of("TEST_TEMPLATE", "Test description");

        // Create a photo document with minimal information
        PhotoDocument photoDocument = PhotoDocument.builder()
                .photoId(testPhotoId)
                .template(template)
                .imagePath(testPhoto)
                .uploadedBy(testUser)
                .uploadedAt(testTimestamp)
                .orderId(testOrderId)
                .build();

        // Verify that the photo satisfies the specification regarding required fields
        // Note: It might still fail other validations like metadata quality checks
        boolean result = specification.isSatisfiedBy(photoDocument);
        
        // Even though the template doesn't require any fields, the specification still
        // recommends having metadata
        List<String> messages = specification.getValidationMessages();
        assertTrue(messages.stream().anyMatch(m -> m.contains("metadata is recommended")),
                "Validation message should mention recommended metadata");
    }
}