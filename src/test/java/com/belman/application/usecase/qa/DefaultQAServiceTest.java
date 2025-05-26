package com.belman.application.usecase.qa;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoAnnotation;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
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

    @Mock
    private PhotoDocument testPhoto;

    private DefaultQAService qaService;
    private PhotoId testPhotoId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        qaService = new DefaultQAService(photoRepository);

        // Create test data
        testPhotoId = new PhotoId(UUID.randomUUID().toString());

        // Configure the test photo document mock
        when(testPhoto.getId()).thenReturn(testPhotoId);
        when(photoRepository.findById(testPhotoId)).thenReturn(Optional.of(testPhoto));
    }

    @Test
    void getAnnotations_shouldReturnAnnotationsFromPhoto() {
        // Arrange
        List<PhotoAnnotation> expectedAnnotations = Collections.singletonList(
                new PhotoAnnotation("1", 0.5, 0.5, "Test annotation", PhotoAnnotation.AnnotationType.NOTE)
        );
        when(testPhoto.getAnnotations()).thenReturn(expectedAnnotations);

        // Act
        List<PhotoAnnotation> actualAnnotations = qaService.getAnnotations(testPhotoId);

        // Assert
        assertEquals(expectedAnnotations, actualAnnotations);
        verify(photoRepository).findById(testPhotoId);
        verify(testPhoto).getAnnotations();
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

        when(testPhoto.addAnnotation(any(PhotoAnnotation.class))).thenReturn(true);

        // Act
        PhotoAnnotation annotation = qaService.createAnnotation(testPhotoId, x, y, text, type);

        // Assert
        assertNotNull(annotation);
        assertEquals(x, annotation.getX());
        assertEquals(y, annotation.getY());
        assertEquals(text, annotation.getText());
        assertEquals(type, annotation.getType());

        verify(photoRepository).findById(testPhotoId);
        verify(testPhoto).addAnnotation(any(PhotoAnnotation.class));
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
        PhotoAnnotation annotation = new PhotoAnnotation(
                "1", 0.5, 0.5, "Updated annotation", PhotoAnnotation.AnnotationType.NOTE);

        when(testPhoto.updateAnnotation(annotation)).thenReturn(true);

        // Act
        boolean result = qaService.updateAnnotation(testPhotoId, annotation);

        // Assert
        assertTrue(result);
        verify(photoRepository).findById(testPhotoId);
        verify(testPhoto).updateAnnotation(annotation);
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
        when(testPhoto.removeAnnotation(annotationId)).thenReturn(true);

        // Act
        boolean result = qaService.deleteAnnotation(testPhotoId, annotationId);

        // Assert
        assertTrue(result);
        verify(photoRepository).findById(testPhotoId);
        verify(testPhoto).removeAnnotation(annotationId);
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
