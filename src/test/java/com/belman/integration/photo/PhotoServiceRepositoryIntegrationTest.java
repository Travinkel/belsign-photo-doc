package com.belman.integration.photo;

import com.belman.application.usecase.photo.DefaultPhotoService;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.dataaccess.repository.memory.InMemoryPhotoRepository;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration test for the PhotoService and PhotoRepository.
 * This test verifies that the PhotoService correctly interacts with the PhotoRepository.
 * 
 * This test class demonstrates:
 * - Integration testing between service and repository
 * - AAA (Arrange-Act-Assert) pattern
 * - Mockito for mocking dependencies
 */
@ExtendWith(MockitoExtension.class)
public class PhotoServiceRepositoryIntegrationTest {

    private PhotoService photoService;
    private PhotoRepository photoRepository;
    
    @Mock
    private LoggerFactory loggerFactory;
    
    @Mock
    private Logger logger;
    
    @Mock
    private UserBusiness mockUser;

    private OrderId testOrderId;
    private PhotoTemplate testTemplate;
    private Photo testPhoto;

    @BeforeEach
    void setUp() {
        // Set up logger
        when(loggerFactory.getLogger(any())).thenReturn(logger);
        
        // Set up mock user
        when(mockUser.getId()).thenReturn(new UserId("test-user-id"));
        when(mockUser.getUsername()).thenReturn(new Username("test-user"));
        
        // Create a real repository with mocked logger
        photoRepository = new InMemoryPhotoRepository(loggerFactory);
        
        // Create the service with the real repository
        photoService = new DefaultPhotoService(photoRepository);
        
        // Set up test data
        testOrderId = new OrderId("test-order-id");
        testTemplate = PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
        testPhoto = new Photo("/path/to/test/image.jpg");
    }

    /**
     * Test that a photo can be uploaded and then retrieved from the repository.
     */
    @Test
    @DisplayName("Photo can be uploaded and retrieved")
    void testUploadAndRetrievePhoto() {
        // Arrange
        Timestamp uploadTime = new Timestamp(Instant.now());
        
        // Act - Upload the photo
        PhotoDocument uploadedPhoto = photoService.uploadPhoto(
                testPhoto, 
                testOrderId, 
                testTemplate, 
                mockUser, 
                uploadTime);
        
        // Assert - Verify the uploaded photo
        assertNotNull(uploadedPhoto, "Uploaded photo should not be null");
        assertEquals(testOrderId, uploadedPhoto.getOrderId(), "Order ID should match");
        assertEquals(testTemplate, uploadedPhoto.getTemplate(), "Template should match");
        assertEquals(testPhoto, uploadedPhoto.getImagePath(), "Image path should match");
        assertEquals(mockUser, uploadedPhoto.getUploadedBy(), "Uploader should match");
        assertEquals(uploadTime, uploadedPhoto.getUploadedAt(), "Upload time should match");
        
        // Act - Retrieve the photo by ID
        Optional<PhotoDocument> retrievedPhotoOpt = photoService.getPhotoById(uploadedPhoto.getId());
        
        // Assert - Verify the retrieved photo
        assertTrue(retrievedPhotoOpt.isPresent(), "Photo should be retrievable by ID");
        PhotoDocument retrievedPhoto = retrievedPhotoOpt.get();
        assertEquals(uploadedPhoto, retrievedPhoto, "Retrieved photo should match uploaded photo");
    }

    /**
     * Test that photos can be retrieved by order ID.
     */
    @Test
    @DisplayName("Photos can be retrieved by order ID")
    void testGetPhotosByOrderId() {
        // Arrange - Upload multiple photos for the same order
        Timestamp uploadTime = new Timestamp(Instant.now());
        
        PhotoDocument photo1 = photoService.uploadPhoto(
                testPhoto, 
                testOrderId, 
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY, 
                mockUser, 
                uploadTime);
        
        PhotoDocument photo2 = photoService.uploadPhoto(
                new Photo("/path/to/test/image2.jpg"), 
                testOrderId, 
                PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY, 
                mockUser, 
                uploadTime);
        
        PhotoDocument photo3 = photoService.uploadPhoto(
                new Photo("/path/to/test/image3.jpg"), 
                testOrderId, 
                PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY, 
                mockUser, 
                uploadTime);
        
        // Upload a photo for a different order
        PhotoDocument differentOrderPhoto = photoService.uploadPhoto(
                new Photo("/path/to/test/image4.jpg"), 
                new OrderId("different-order-id"), 
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY, 
                mockUser, 
                uploadTime);
        
        // Act - Retrieve photos by order ID
        List<PhotoDocument> orderPhotos = photoService.getPhotosByOrderId(testOrderId);
        
        // Assert - Verify the retrieved photos
        assertEquals(3, orderPhotos.size(), "Should retrieve 3 photos for the test order");
        assertTrue(orderPhotos.contains(photo1), "Should contain the first photo");
        assertTrue(orderPhotos.contains(photo2), "Should contain the second photo");
        assertTrue(orderPhotos.contains(photo3), "Should contain the third photo");
        assertFalse(orderPhotos.contains(differentOrderPhoto), "Should not contain the photo from a different order");
    }

    /**
     * Test that a photo can be deleted.
     */
    @Test
    @DisplayName("Photo can be deleted")
    void testDeletePhoto() {
        // Arrange - Upload a photo
        Timestamp uploadTime = new Timestamp(Instant.now());
        
        PhotoDocument uploadedPhoto = photoService.uploadPhoto(
                testPhoto, 
                testOrderId, 
                testTemplate, 
                mockUser, 
                uploadTime);
        
        PhotoId photoId = uploadedPhoto.getId();
        
        // Verify the photo exists
        assertTrue(photoService.getPhotoById(photoId).isPresent(), "Photo should exist before deletion");
        
        // Act - Delete the photo
        boolean deleted = photoService.deletePhoto(photoId);
        
        // Assert - Verify the photo was deleted
        assertTrue(deleted, "deletePhoto should return true for successful deletion");
        assertFalse(photoService.getPhotoById(photoId).isPresent(), "Photo should not exist after deletion");
    }

    /**
     * Test that a photo's approval status can be updated.
     */
    @Test
    @DisplayName("Photo approval status can be updated")
    void testUpdatePhotoApprovalStatus() {
        // Arrange - Upload a photo
        Timestamp uploadTime = new Timestamp(Instant.now());
        
        PhotoDocument uploadedPhoto = photoService.uploadPhoto(
                testPhoto, 
                testOrderId, 
                testTemplate, 
                mockUser, 
                uploadTime);
        
        // Initial status should be PENDING
        assertEquals(PhotoDocument.ApprovalStatus.PENDING, uploadedPhoto.getStatus(), 
                "Initial status should be PENDING");
        
        // Act - Approve the photo
        boolean approved = photoService.approvePhoto(
                uploadedPhoto.getId(), 
                mockUser, 
                new Timestamp(Instant.now()));
        
        // Assert - Verify the photo was approved
        assertTrue(approved, "approvePhoto should return true for successful approval");
        
        // Retrieve the updated photo
        Optional<PhotoDocument> updatedPhotoOpt = photoService.getPhotoById(uploadedPhoto.getId());
        assertTrue(updatedPhotoOpt.isPresent(), "Photo should still exist after approval");
        
        PhotoDocument updatedPhoto = updatedPhotoOpt.get();
        assertEquals(PhotoDocument.ApprovalStatus.APPROVED, updatedPhoto.getStatus(), 
                "Status should be APPROVED after approval");
    }
}