package com.belman.domain.photo;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PhotoDocument class, focusing on annotation functionality.
 */
public class PhotoDocumentTest {

    private PhotoDocument photoDocument;
    private PhotoAnnotation testAnnotation1;
    private PhotoAnnotation testAnnotation2;
    private PhotoAnnotation testAnnotation3;

    @BeforeEach
    void setUp() {
        // Create test annotations
        testAnnotation1 = new PhotoAnnotation(
                "anno1", 0.1, 0.2, "Test annotation 1", 
                PhotoAnnotation.AnnotationType.NOTE);

        testAnnotation2 = new PhotoAnnotation(
                "anno2", 0.3, 0.4, "Test annotation 2", 
                PhotoAnnotation.AnnotationType.ISSUE);

        testAnnotation3 = new PhotoAnnotation(
                "anno3", 0.5, 0.6, "Test annotation 3", 
                PhotoAnnotation.AnnotationType.HIGHLIGHT);

        // Create a photo document with initial annotations
        List<PhotoAnnotation> initialAnnotations = new ArrayList<>();
        initialAnnotations.add(testAnnotation1);

        // Create mock objects needed for PhotoDocument
        PhotoId photoId = new PhotoId(UUID.randomUUID().toString());
        PhotoTemplate template = PhotoTemplate.of("Test Template", "Description");
        Photo photo = new Photo("test/path/image.jpg");
        UserBusiness uploadedBy = new UserBusiness.Builder()
                .id(new UserId(UUID.randomUUID().toString()))
                .username(new Username("testuser"))
                .password(new HashedPassword("hashedpassword123"))
                .email(new EmailAddress("test@example.com"))
                .build();
        Timestamp uploadedAt = new Timestamp(Instant.now());
        OrderId orderId = new OrderId("ORD123");

        // Build the photo document
        photoDocument = PhotoDocument.builder()
                .photoId(photoId)
                .template(template)
                .imagePath(photo)
                .uploadedBy(uploadedBy)
                .uploadedAt(uploadedAt)
                .orderId(orderId)
                .annotations(initialAnnotations)
                .build();
    }

    @Test
    void testGetAnnotations() {
        // Verify initial state
        List<PhotoAnnotation> annotations = photoDocument.getAnnotations();
        assertEquals(1, annotations.size());
        assertEquals("anno1", annotations.get(0).getId());

        // Verify that the returned list is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            annotations.add(testAnnotation2);
        });
    }

    @Test
    void testAddAnnotation() {
        // Add a new annotation
        boolean result = photoDocument.addAnnotation(testAnnotation2);

        // Verify the result and the updated state
        assertTrue(result);
        List<PhotoAnnotation> annotations = photoDocument.getAnnotations();
        assertEquals(2, annotations.size());
        assertTrue(annotations.contains(testAnnotation1));
        assertTrue(annotations.contains(testAnnotation2));

        // Verify that null annotations are rejected
        assertThrows(NullPointerException.class, () -> {
            photoDocument.addAnnotation(null);
        });
    }

    @Test
    void testRemoveAnnotation() {
        // Add annotations for testing removal
        photoDocument.addAnnotation(testAnnotation2);
        photoDocument.addAnnotation(testAnnotation3);

        // Verify initial state
        assertEquals(3, photoDocument.getAnnotations().size());

        // Remove an annotation
        boolean result = photoDocument.removeAnnotation("anno2");

        // Verify the result and the updated state
        assertTrue(result);
        List<PhotoAnnotation> annotations = photoDocument.getAnnotations();
        assertEquals(2, annotations.size());
        assertTrue(annotations.contains(testAnnotation1));
        assertTrue(annotations.contains(testAnnotation3));
        assertFalse(annotations.contains(testAnnotation2));

        // Try to remove a non-existent annotation
        result = photoDocument.removeAnnotation("nonexistent");
        assertFalse(result);
        assertEquals(2, photoDocument.getAnnotations().size());

        // Verify that null IDs are rejected
        assertThrows(NullPointerException.class, () -> {
            photoDocument.removeAnnotation(null);
        });
    }

    @Test
    void testUpdateAnnotation() {
        // Create an updated version of an existing annotation
        PhotoAnnotation updatedAnnotation = new PhotoAnnotation(
                "anno1", 0.7, 0.8, "Updated annotation", 
                PhotoAnnotation.AnnotationType.MEASUREMENT);

        // Update the annotation
        boolean result = photoDocument.updateAnnotation(updatedAnnotation);

        // Verify the result and the updated state
        assertTrue(result);
        List<PhotoAnnotation> annotations = photoDocument.getAnnotations();
        assertEquals(1, annotations.size());
        assertEquals("anno1", annotations.get(0).getId());
        assertEquals(0.7, annotations.get(0).getX());
        assertEquals(0.8, annotations.get(0).getY());
        assertEquals("Updated annotation", annotations.get(0).getText());
        assertEquals(PhotoAnnotation.AnnotationType.MEASUREMENT, annotations.get(0).getType());

        // Try to update a non-existent annotation
        PhotoAnnotation nonExistentAnnotation = new PhotoAnnotation(
                "nonexistent", 0.1, 0.1, "Non-existent", 
                PhotoAnnotation.AnnotationType.NOTE);
        result = photoDocument.updateAnnotation(nonExistentAnnotation);
        assertFalse(result);

        // Verify that null annotations are rejected
        assertThrows(NullPointerException.class, () -> {
            photoDocument.updateAnnotation(null);
        });
    }
}
