package unit.domain.service;


import domain.model.order.Order;
import domain.model.order.OrderId;
import domain.model.order.photodocument.ImagePath;
import domain.model.order.photodocument.PhotoAngle;
import domain.model.order.photodocument.PhotoDocument;
import domain.model.order.photodocument.PhotoId;
import domain.model.report.Report;
import domain.model.shared.Timestamp;
import domain.model.user.EmailAddress;
import domain.model.user.User;
import domain.model.user.Username;
import domain.service.ReportBuilderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReportBuilderServiceTest {

    private Order order;
    private User user;
    private Timestamp timestamp;
    private ReportBuilderService reportBuilderService;

    @BeforeEach
    void setUp() {
        // Create test data
        OrderId orderId = new OrderId(UUID.randomUUID());
        user = new User(new Username("testuser"), new EmailAddress("test@example.com"));
        timestamp = Timestamp.now();
        
        // Create an order with approved photos
        order = new Order(orderId, user, timestamp);
        
        // Add some approved photos to the order
        PhotoDocument photo1 = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.FRONT),
                new ImagePath("test1.jpg"),
                user,
                timestamp
        );
        photo1.approve(user, timestamp);
        order.addPhoto(photo1);
        
        PhotoDocument photo2 = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.BACK),
                new ImagePath("test2.jpg"),
                user,
                timestamp
        );
        photo2.approve(user, timestamp);
        order.addPhoto(photo2);
        
        // Add a pending photo to the order
        PhotoDocument photo3 = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.LEFT),
                new ImagePath("test3.jpg"),
                user,
                timestamp
        );
        order.addPhoto(photo3);
        
        // Create a mock implementation of ReportBuilderService
        reportBuilderService = new ReportBuilderService() {
            @Override
            public Report buildReport(Order order, User generatedBy) {
                return new Report(
                        order.getId(),
                        order.getApprovedPhotos(),
                        generatedBy,
                        Timestamp.now()
                );
            }
        };
    }

    @Test
    void buildReportShouldCreateReportWithApprovedPhotos() {
        // Build a report
        Report report = reportBuilderService.buildReport(order, user);
        
        // Verify the report properties
        assertEquals(order.getId(), report.getOrderId());
        assertEquals(user, report.getGeneratedBy());
        assertNotNull(report.getGeneratedAt());
        
        // Verify that only approved photos are included
        assertEquals(2, report.getApprovedPhotos().size());
        assertTrue(report.getApprovedPhotos().stream().allMatch(PhotoDocument::isApproved));
    }

    @Test
    void buildReportShouldHandleOrderWithNoApprovedPhotos() {
        // Create an order with no approved photos
        Order emptyOrder = new Order(new OrderId(UUID.randomUUID()), user, timestamp);
        
        // Build a report
        Report report = reportBuilderService.buildReport(emptyOrder, user);
        
        // Verify the report properties
        assertEquals(emptyOrder.getId(), report.getOrderId());
        assertEquals(user, report.getGeneratedBy());
        assertNotNull(report.getGeneratedAt());
        
        // Verify that no photos are included
        assertEquals(0, report.getApprovedPhotos().size());
    }
}