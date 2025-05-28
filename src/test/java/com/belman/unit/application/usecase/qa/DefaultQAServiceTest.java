package com.belman.unit.application.usecase.qa;

import com.belman.application.usecase.qa.DefaultQAService;
import com.belman.domain.photo.PhotoAnnotation;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the DefaultQAService class.
 * Tests the annotation-related methods to ensure they work correctly.
 */
class DefaultQAServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    // Using a real instance instead of a mock due to Java 23 + Mockito limitations with final classes
    private PhotoDocument testPhoto;

    private DefaultQAService qaService;
    private PhotoId testPhotoId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        qaService = new DefaultQAService(photoRepository);

        // Create test data
        testPhotoId = new PhotoId(UUID.randomUUID().toString());

        // Create a real PhotoDocument instance instead of a mock
        // We need to create a minimal valid instance with required fields
        com.belman.domain.user.UserBusiness testUser = new com.belman.domain.user.UserBusiness.Builder()
            .id(new com.belman.domain.user.UserId("test-user"))
            .username(new com.belman.domain.user.Username("testuser"))
            .password(new com.belman.domain.security.HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new com.belman.domain.common.valueobjects.EmailAddress("test@example.com"))
            .approvalState(com.belman.domain.user.ApprovalState.createApproved())
            .build();

        com.belman.domain.photo.PhotoTemplate template = new com.belman.domain.photo.PhotoTemplate("Test Template", "Test description", java.util.Collections.emptySet());
        com.belman.domain.photo.Photo photo = new com.belman.domain.photo.Photo("test.jpg");

        testPhoto = com.belman.domain.photo.PhotoDocument.builder()
            .photoId(testPhotoId)
            .template(template)
            .imagePath(photo)
            .uploadedBy(testUser)
            .uploadedAt(new com.belman.domain.common.valueobjects.Timestamp(java.time.Instant.now()))
            .build();

        // Configure the repository mock to return our real photo instance
        when(photoRepository.findById(testPhotoId)).thenReturn(Optional.of(testPhoto));
    }

    @Test
    void getAnnotations_shouldReturnAnnotationsFromPhoto() {
        // Arrange
        PhotoAnnotation annotation = new PhotoAnnotation("1", 0.5, 0.5, "Test annotation", PhotoAnnotation.AnnotationType.NOTE);
        testPhoto.addAnnotation(annotation);
        List<PhotoAnnotation> expectedAnnotations = Collections.singletonList(annotation);

        // Act
        List<PhotoAnnotation> actualAnnotations = qaService.getAnnotations(testPhotoId);

        // Assert
        assertEquals(expectedAnnotations, actualAnnotations);
        verify(photoRepository).findById(testPhotoId);
    }

    @Test
    void getAnnotations_shouldReturnEmptyListWhenPhotoNotFound() {
        // Arrange
        PhotoId nonExistentPhotoId = new PhotoId(UUID.randomUUID().toString());
        when(photoRepository.findById(nonExistentPhotoId)).thenReturn(Optional.empty());

        // Act
        List<PhotoAnnotation> annotations = qaService.getAnnotations(nonExistentPhotoId);

        // Assert
        assertTrue(annotations.isEmpty());
        verify(photoRepository).findById(nonExistentPhotoId);
    }

    @Test
    void createAnnotation_shouldCreateAndReturnAnnotation() {
        // Arrange
        double x = 0.5;
        double y = 0.5;
        String text = "Test annotation";
        PhotoAnnotation.AnnotationType type = PhotoAnnotation.AnnotationType.NOTE;

        // Act
        PhotoAnnotation annotation = qaService.createAnnotation(testPhotoId, x, y, text, type);

        // Assert
        assertNotNull(annotation);
        assertEquals(x, annotation.getX());
        assertEquals(y, annotation.getY());
        assertEquals(text, annotation.getText());
        assertEquals(type, annotation.getType());

        // Verify the annotation was added to the photo
        assertTrue(testPhoto.getAnnotations().contains(annotation));

        verify(photoRepository).findById(testPhotoId);
        verify(photoRepository).save(testPhoto);
    }

    @Test
    void createAnnotation_shouldReturnNullWhenPhotoNotFound() {
        // Arrange
        PhotoId nonExistentPhotoId = new PhotoId(UUID.randomUUID().toString());
        when(photoRepository.findById(nonExistentPhotoId)).thenReturn(Optional.empty());

        // Act
        PhotoAnnotation annotation = qaService.createAnnotation(
                nonExistentPhotoId, 0.5, 0.5, "Test", PhotoAnnotation.AnnotationType.NOTE);

        // Assert
        assertNull(annotation);
        verify(photoRepository).findById(nonExistentPhotoId);
        verify(photoRepository, never()).save(any());
    }

    @Test
    void updateAnnotation_shouldUpdateAndReturnTrue() {
        // Arrange
        String annotationId = "1";
        PhotoAnnotation originalAnnotation = new PhotoAnnotation(
                annotationId, 0.5, 0.5, "Original annotation", PhotoAnnotation.AnnotationType.NOTE);
        testPhoto.addAnnotation(originalAnnotation);

        PhotoAnnotation updatedAnnotation = new PhotoAnnotation(
                annotationId, 0.7, 0.7, "Updated annotation", PhotoAnnotation.AnnotationType.ISSUE);

        // Act
        boolean result = qaService.updateAnnotation(testPhotoId, updatedAnnotation);

        // Assert
        assertTrue(result);

        // Verify the annotation was updated
        List<PhotoAnnotation> annotations = testPhoto.getAnnotations();
        assertEquals(1, annotations.size());
        PhotoAnnotation retrievedAnnotation = annotations.get(0);
        assertEquals(annotationId, retrievedAnnotation.getId());
        assertEquals(0.7, retrievedAnnotation.getX());
        assertEquals(0.7, retrievedAnnotation.getY());
        assertEquals("Updated annotation", retrievedAnnotation.getText());
        assertEquals(PhotoAnnotation.AnnotationType.ISSUE, retrievedAnnotation.getType());

        verify(photoRepository).findById(testPhotoId);
        verify(photoRepository).save(testPhoto);
    }

    @Test
    void updateAnnotation_shouldReturnFalseWhenPhotoNotFound() {
        // Arrange
        PhotoId nonExistentPhotoId = new PhotoId(UUID.randomUUID().toString());
        PhotoAnnotation annotation = new PhotoAnnotation(
                "1", 0.5, 0.5, "Test", PhotoAnnotation.AnnotationType.NOTE);

        when(photoRepository.findById(nonExistentPhotoId)).thenReturn(Optional.empty());

        // Act
        boolean result = qaService.updateAnnotation(nonExistentPhotoId, annotation);

        // Assert
        assertFalse(result);
        verify(photoRepository).findById(nonExistentPhotoId);
        verify(photoRepository, never()).save(any());
    }

    @Test
    void deleteAnnotation_shouldDeleteAndReturnTrue() {
        // Arrange
        String annotationId = "1";
        PhotoAnnotation annotation = new PhotoAnnotation(annotationId, 0.5, 0.5, "Test annotation", PhotoAnnotation.AnnotationType.NOTE);
        testPhoto.addAnnotation(annotation);

        // Act
        boolean result = qaService.deleteAnnotation(testPhotoId, annotationId);

        // Assert
        assertTrue(result);
        assertFalse(testPhoto.getAnnotations().contains(annotation));
        verify(photoRepository).findById(testPhotoId);
        verify(photoRepository).save(testPhoto);
    }

    @Test
    void deleteAnnotation_shouldReturnFalseWhenPhotoNotFound() {
        // Arrange
        PhotoId nonExistentPhotoId = new PhotoId(UUID.randomUUID().toString());
        String annotationId = "1";

        when(photoRepository.findById(nonExistentPhotoId)).thenReturn(Optional.empty());

        // Act
        boolean result = qaService.deleteAnnotation(nonExistentPhotoId, annotationId);

        // Assert
        assertFalse(result);
        verify(photoRepository).findById(nonExistentPhotoId);
        verify(photoRepository, never()).save(any());
    }
}
