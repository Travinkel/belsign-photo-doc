package com.belman.unit.be.businessobjects;

import com.belman.domain.common.Timestamp;
import com.belman.domain.customer.CustomerBusiness;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.Photo;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.order.photo.PhotoTemplate;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportFormat;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportStatus;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ReportBusiness class.
 */
public class ReportBusinessTest {

    private OrderId orderId;
    private List<PhotoDocument> approvedPhotos;
    private UserBusiness generatedBy;
    private Timestamp generatedAt;
    private CustomerBusiness recipient;

    @BeforeEach
    void setUp() {
        // Create test data
        orderId = OrderId.newId();
        approvedPhotos = new ArrayList<>();

        // Create a photo document
        PhotoId photoId = PhotoId.newId();
        PhotoTemplate template = PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
        Photo photo = new Photo("/path/to/test/image.jpg");
        Timestamp uploadedAt = new Timestamp(Instant.now());

        // Create a user
        UserId userId = UserId.newId();
        Username username = new Username("testuser");
        generatedBy = new UserBusiness.Builder()
                .id(userId)
                .username(username)
                .build();

        // Create a photo document
        PhotoDocument photoDoc = PhotoDocument.builder()
                .photoId(photoId)
                .template(template)
                .imagePath(photo)
                .uploadedBy(generatedBy)
                .uploadedAt(uploadedAt)
                .build();

        approvedPhotos.add(photoDoc);

        // Set timestamp
        generatedAt = new Timestamp(Instant.now());

        // Create recipient
        CustomerId customerId = CustomerId.newId();
        recipient = new CustomerBusiness.Builder()
                .withId(customerId)
                .build();
    }

    @Test
    @DisplayName("Should create a report with required fields")
    void testCreateReportWithRequiredFields() {
        // Act
        ReportBusiness report = new ReportBusiness(orderId, approvedPhotos, generatedBy, generatedAt);

        // Assert
        assertNotNull(report.getId());
        assertEquals(orderId, report.getOrderId());
        assertEquals(approvedPhotos, report.getApprovedPhotos());
        assertEquals(generatedBy, report.getGeneratedBy());
        assertEquals(generatedAt, report.getGeneratedAt());
        assertEquals(ReportStatus.PENDING, report.getStatus());
        assertEquals(1, report.getVersion());
        assertTrue(report.isDraft());
        assertFalse(report.isFinal());
        assertFalse(report.isSent());
        assertFalse(report.isArchived());
    }

    @Test
    @DisplayName("Should create a report using the Builder pattern")
    void testCreateReportUsingBuilder() {
        // Act
        ReportBusiness report = ReportBusiness.builder()
                .orderId(orderId)
                .approvedPhotos(approvedPhotos)
                .generatedBy(generatedBy)
                .generatedAt(generatedAt)
                .recipient(recipient)
                .format(ReportFormat.PDF)
                .status(ReportStatus.PENDING)
                .comments("Test comments")
                .build();

        // Assert
        assertNotNull(report.getId());
        assertEquals(orderId, report.getOrderId());
        assertEquals(approvedPhotos, report.getApprovedPhotos());
        assertEquals(generatedBy, report.getGeneratedBy());
        assertEquals(generatedAt, report.getGeneratedAt());
        assertEquals(recipient, report.getRecipient());
        assertEquals(ReportFormat.PDF, report.getFormat());
        assertEquals(ReportStatus.PENDING, report.getStatus());
        assertEquals("Test comments", report.getComments());
        assertEquals(1, report.getVersion());
        assertTrue(report.isDraft());
        assertFalse(report.isFinal());
        assertFalse(report.isSent());
        assertFalse(report.isArchived());
    }

    @Test
    @DisplayName("Should finalize a report")
    void testFinalizeReport() {
        // Arrange
        ReportBusiness report = new ReportBusiness(orderId, approvedPhotos, generatedBy, generatedAt);
        report.setRecipient(recipient);
        report.setFormat(ReportFormat.PDF);

        // Act
        report.finalizeReport();

        // Assert
        assertEquals(ReportStatus.GENERATED, report.getStatus());
        assertEquals(2, report.getVersion());
        assertFalse(report.isDraft());
        assertTrue(report.isFinal());
        assertFalse(report.isSent());
        assertFalse(report.isArchived());
    }

    @Test
    @DisplayName("Should mark a report as sent")
    void testMarkReportAsSent() throws MalformedURLException {
        // Arrange
        ReportBusiness report = new ReportBusiness(orderId, approvedPhotos, generatedBy, generatedAt);
        report.setRecipient(recipient);
        report.setFormat(ReportFormat.PDF);
        report.finalizeReport();
        URL fileUrl = new URL("https://example.com/reports/report.pdf");

        // Act
        report.markAsSent(fileUrl);

        // Assert
        assertEquals(ReportStatus.DELIVERED, report.getStatus());
        assertEquals(3, report.getVersion());
        assertFalse(report.isDraft());
        assertFalse(report.isFinal());
        assertTrue(report.isSent());
        assertFalse(report.isArchived());
    }

    @Test
    @DisplayName("Should archive a report")
    void testArchiveReport() throws MalformedURLException {
        // Arrange
        ReportBusiness report = new ReportBusiness(orderId, approvedPhotos, generatedBy, generatedAt);
        report.setRecipient(recipient);
        report.setFormat(ReportFormat.PDF);
        report.finalizeReport();
        URL fileUrl = new URL("https://example.com/reports/report.pdf");
        report.markAsSent(fileUrl);

        // Act
        report.archive();

        // Assert
        assertEquals(ReportStatus.ARCHIVED, report.getStatus());
        assertEquals(4, report.getVersion());
        assertFalse(report.isDraft());
        assertFalse(report.isFinal());
        assertFalse(report.isSent());
        assertTrue(report.isArchived());
    }

    @Test
    @DisplayName("Should update report properties")
    void testUpdateReportProperties() {
        // Arrange
        ReportBusiness report = new ReportBusiness(orderId, approvedPhotos, generatedBy, generatedAt);

        // Act
        report.setRecipient(recipient);
        report.setFormat(ReportFormat.PDF);
        report.setComments("Updated comments");

        // Assert
        assertEquals(recipient, report.getRecipient());
        assertEquals(ReportFormat.PDF, report.getFormat());
        assertEquals("Updated comments", report.getComments());
    }

    @Test
    @DisplayName("Should increment version")
    void testIncrementVersion() {
        // Arrange
        ReportBusiness report = new ReportBusiness(orderId, approvedPhotos, generatedBy, generatedAt);
        int initialVersion = report.getVersion();

        // Act
        report.incrementVersion();

        // Assert
        assertEquals(initialVersion + 1, report.getVersion());
    }
}
