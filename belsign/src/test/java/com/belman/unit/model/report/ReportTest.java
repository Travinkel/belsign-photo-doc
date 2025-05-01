package com.belman.unit.model.report;


import com.belman.domain.entities.Customer;
import com.belman.domain.valueobjects.CustomerId;
import com.belman.domain.valueobjects.PersonName;
import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.valueobjects.ImagePath;
import com.belman.domain.valueobjects.PhotoAngle;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.valueobjects.PhotoId;
import com.belman.domain.entities.Report;
import com.belman.domain.enums.ReportFormat;
import com.belman.domain.valueobjects.ReportId;
import com.belman.domain.enums.ReportStatus;
import com.belman.domain.valueobjects.Timestamp;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.aggregates.User;
import com.belman.domain.valueobjects.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    private OrderId orderId;
    private List<PhotoDocument> approvedPhotos;
    private User user;
    private Timestamp timestamp;

    @BeforeEach
    void setUp() {
        // Create test data
        orderId = new OrderId(UUID.randomUUID());
        user = new User(new Username("testuser"), new HashedPassword("hashedPassword123"), new EmailAddress("test@example.com"));
        timestamp = Timestamp.now();

        // Create a list of approved photos
        approvedPhotos = new ArrayList<>();
        PhotoDocument photo1 = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.FRONT),
                new ImagePath("test1.jpg"),
                user,
                timestamp
        );
        photo1.approve(user, timestamp);

        PhotoDocument photo2 = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.BACK),
                new ImagePath("test2.jpg"),
                user,
                timestamp
        );
        photo2.approve(user, timestamp);

        approvedPhotos.add(photo1);
        approvedPhotos.add(photo2);
    }

    @Test
    void reportShouldBeCreatedWithValidData() {
        // Create a report
        Report report = new Report(orderId, approvedPhotos, user, timestamp);

        // Verify the report properties
        assertEquals(orderId, report.getOrderId());
        assertEquals(approvedPhotos, report.getApprovedPhotos());
        assertEquals(user, report.getGeneratedBy());
        assertEquals(timestamp, report.getGeneratedAt());
    }

    @Test
    void reportShouldRejectNullValues() {
        // This test verifies that the Report constructor rejects null values,
        // which is a good practice to prevent NullPointerExceptions

        // Verify that null values are rejected
        assertThrows(NullPointerException.class, () -> new Report(null, approvedPhotos, user, timestamp));
        assertThrows(NullPointerException.class, () -> new Report(orderId, null, user, timestamp));
        assertThrows(NullPointerException.class, () -> new Report(orderId, approvedPhotos, null, timestamp));
        assertThrows(NullPointerException.class, () -> new Report(orderId, approvedPhotos, user, null));
    }

    @Test
    void approvedPhotosShouldBeUnmodifiable() {
        // This test verifies that the approvedPhotos list is protected from modification,
        // which is a good practice to maintain encapsulation

        // Create a report
        Report report = new Report(orderId, approvedPhotos, user, timestamp);

        // Get the approvedPhotos list and try to modify it
        List<PhotoDocument> photos = report.getApprovedPhotos();

        // Verify that the list cannot be modified
        assertThrows(UnsupportedOperationException.class, () -> {
            photos.add(new PhotoDocument(
                    new PhotoId(UUID.randomUUID()),
                    new PhotoAngle(PhotoAngle.NamedAngle.LEFT),
                    new ImagePath("test3.jpg"),
                    user,
                    timestamp
            ));
        });
    }

    @Test
    void builderShouldCreateReportWithAllProperties() {
        // This test demonstrates the use of the Builder pattern to create a Report

        // Create a report using the Builder pattern
        ReportId reportId = ReportId.newId();
        Customer recipient = Customer.individual(
                CustomerId.newId(),
                new PersonName("John", "Doe"),
                new EmailAddress("john.doe@example.com")
        );

        Report report = Report.builder()
                .id(reportId)
                .orderId(orderId)
                .approvedPhotos(approvedPhotos)
                .generatedBy(user)
                .generatedAt(timestamp)
                .recipient(recipient)
                .format(ReportFormat.PDF)
                .status(ReportStatus.DRAFT)
                .comments("Test comments")
                .version(1)
                .build();

        // Verify all properties
        assertEquals(reportId, report.getId());
        assertEquals(orderId, report.getOrderId());
        assertEquals(approvedPhotos, report.getApprovedPhotos());
        assertEquals(user, report.getGeneratedBy());
        assertEquals(timestamp, report.getGeneratedAt());
        assertEquals(recipient, report.getRecipient());
        assertEquals(ReportFormat.PDF, report.getFormat());
        assertEquals(ReportStatus.DRAFT, report.getStatus());
        assertEquals("Test comments", report.getComments());
        assertEquals(1, report.getVersion());
    }
}
