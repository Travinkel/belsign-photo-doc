package com.belman.unit.domain.report.service;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.common.valueobjects.EmailAddress;

import java.time.Instant;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.photo.Photo;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportStatus;
import com.belman.domain.report.service.PhotoReportGenerationService;
import com.belman.domain.services.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PhotoReportGenerationService class.
 * These tests verify that the service correctly generates photo documentation reports.
 */
public class PhotoReportGenerationServiceTest {

    @Mock
    private LoggerFactory loggerFactory;

    @Mock
    private PhotoRepository photoRepository;

    private PhotoReportGenerationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PhotoReportGenerationService(loggerFactory, photoRepository);
    }

    /**
     * Test that generatePhotoDocumentationReport correctly creates a report with approved photos.
     */
    @Test
    void testGeneratePhotoDocumentationReport_CreatesReportWithApprovedPhotos() {
        // Arrange
        OrderBusiness mockOrder = createMockOrder();
        UserReference requester = createMockUserReference();
        List<PhotoDocument> approvedPhotos = createMockApprovedPhotos(mockOrder.getId());

        when(photoRepository.findByOrderIdAndStatus(
                eq(mockOrder.getId()), 
                eq(PhotoDocument.ApprovalStatus.APPROVED)))
                .thenReturn(approvedPhotos);

        // Act
        ReportBusiness report = service.generatePhotoDocumentationReport(mockOrder, requester);

        // Assert
        assertNotNull(report, "Report should not be null");
        assertEquals(mockOrder.getId(), report.getOrderId(), "Report should have the correct order ID");
        assertEquals(ReportStatus.PENDING, report.getStatus(), "Report should have PENDING status");
        assertEquals(approvedPhotos, report.getApprovedPhotos(), "Report should contain the approved photos");

        // Verify that the repository was called
        verify(photoRepository).findByOrderIdAndStatus(mockOrder.getId(), PhotoDocument.ApprovalStatus.APPROVED);
    }

    /**
     * Test that generatePhotoDocumentationReport throws an exception when order is null.
     */
    @Test
    void testGeneratePhotoDocumentationReport_ThrowsException_WhenOrderIsNull() {
        // Arrange
        UserReference requester = createMockUserReference();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            service.generatePhotoDocumentationReport(null, requester);
        }, "Should throw NullPointerException when order is null");
    }

    /**
     * Test that generatePhotoDocumentationReport throws an exception when requester is null.
     */
    @Test
    void testGeneratePhotoDocumentationReport_ThrowsException_WhenRequesterIsNull() {
        // Arrange
        OrderBusiness mockOrder = createMockOrder();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            service.generatePhotoDocumentationReport(mockOrder, null);
        }, "Should throw NullPointerException when requester is null");
    }

    /**
     * Test that generatePhotoDocumentationReport creates a report even when there are no approved photos.
     */
    @Test
    void testGeneratePhotoDocumentationReport_CreatesReport_WhenNoApprovedPhotos() {
        // Arrange
        OrderBusiness mockOrder = createMockOrder();
        UserReference requester = createMockUserReference();

        when(photoRepository.findByOrderIdAndStatus(
                eq(mockOrder.getId()), 
                eq(PhotoDocument.ApprovalStatus.APPROVED)))
                .thenReturn(new ArrayList<>());

        // Act
        ReportBusiness report = service.generatePhotoDocumentationReport(mockOrder, requester);

        // Assert
        assertNotNull(report, "Report should not be null");
        assertEquals(mockOrder.getId(), report.getOrderId(), "Report should have the correct order ID");
        assertEquals(ReportStatus.PENDING, report.getStatus(), "Report should have PENDING status");
        assertTrue(report.getApprovedPhotos().isEmpty(), "Report should have no approved photos");

        // Verify that the repository was called
        verify(photoRepository).findByOrderIdAndStatus(mockOrder.getId(), PhotoDocument.ApprovalStatus.APPROVED);
    }

    /**
     * Helper method to create a real order.
     * 
     * @return a real OrderBusiness instance
     */
    private OrderBusiness createMockOrder() {
        // Create a real OrderId
        OrderId orderId = new OrderId(UUID.randomUUID().toString());

        // Create a real OrderNumber
        OrderNumber orderNumber = new OrderNumber("ORD-78-230101-ABC-0001");

        // Create a user reference for the creator
        UserId userId = new UserId(UUID.randomUUID().toString());
        Username username = new Username("test_user");
        UserReference createdBy = new UserReference(userId, username);

        // Create a timestamp for creation time
        Timestamp createdAt = new Timestamp(Instant.now());

        // Use the constructor to create a real OrderBusiness instance
        return new OrderBusiness(orderId, orderNumber, createdBy, createdAt);
    }

    /**
     * Helper method to create a mock user reference.
     * 
     * @return a mock user reference
     */
    private UserReference createMockUserReference() {
        UserId userId = new UserId(UUID.randomUUID().toString());
        Username username = new Username("qa_user");
        return new UserReference(userId, username);
    }

    /**
     * Helper method to create a list of real approved photos.
     * 
     * @param orderId the order ID
     * @return a list of approved photos
     */
    private List<PhotoDocument> createMockApprovedPhotos(OrderId orderId) {
        List<PhotoDocument> photos = new ArrayList<>();

        // Create 3 real photos
        for (int i = 0; i < 3; i++) {
            PhotoId photoId = new PhotoId(UUID.randomUUID().toString());
            PhotoTemplate template = PhotoTemplate.of("Test Template " + i, "Description " + i);
            Photo photo = new Photo("test/path/image" + i + ".jpg");

            // Create a user reference for the uploader
            UserId userId = new UserId(UUID.randomUUID().toString());
            Username username = new Username("test_user_" + i);
            UserReference userRef = new UserReference(userId, username);

            // Create a user business object
            UserBusiness uploadedBy = new UserBusiness.Builder()
                    .id(userId)
                    .username(username)
                    .password(new HashedPassword("hashedpassword123"))
                    .email(new EmailAddress("test_user_" + i + "@example.com"))
                    .build();

            Timestamp uploadedAt = new Timestamp(Instant.now());

            // Build the photo document
            PhotoDocument photoDoc = PhotoDocument.builder()
                    .photoId(photoId)
                    .template(template)
                    .imagePath(photo)
                    .uploadedBy(uploadedBy)
                    .uploadedAt(uploadedAt)
                    .orderId(orderId)
                    .build();

            // Approve the photo
            photoDoc.approve(userRef, new Timestamp(Instant.now()));

            photos.add(photoDoc);
        }

        return photos;
    }
}
