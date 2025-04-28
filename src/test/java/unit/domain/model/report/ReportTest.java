package unit.domain.model.report;

import com.belman.belsign.domain.model.order.OrderId;
import com.belman.belsign.domain.model.order.photodocument.ImagePath;
import com.belman.belsign.domain.model.order.photodocument.PhotoAngle;
import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import com.belman.belsign.domain.model.order.photodocument.PhotoId;
import com.belman.belsign.domain.model.report.Report;
import com.belman.belsign.domain.model.shared.Timestamp;
import com.belman.belsign.domain.model.user.EmailAddress;
import com.belman.belsign.domain.model.user.User;
import com.belman.belsign.domain.model.user.Username;
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
        user = new User(new Username("testuser"), new EmailAddress("test@example.com"));
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
    void reportShouldAllowNullValues() {
        // This test demonstrates that the Report constructor currently allows null values,
        // which is a potential issue that should be fixed
        
        // Create a report with null values
        Report report = new Report(null, null, null, null);
        
        // Verify that null values are accepted
        assertNull(report.getOrderId());
        assertNull(report.getApprovedPhotos());
        assertNull(report.getGeneratedBy());
        assertNull(report.getGeneratedAt());
    }

    @Test
    void approvedPhotosShouldBeModifiable() {
        // This test demonstrates that the approvedPhotos list is not protected from modification,
        // which is a potential issue that should be fixed
        
        // Create a report
        Report report = new Report(orderId, approvedPhotos, user, timestamp);
        
        // Get the approvedPhotos list and try to modify it
        List<PhotoDocument> photos = report.getApprovedPhotos();
        
        // Verify that the list can be modified
        assertDoesNotThrow(() -> {
            photos.add(new PhotoDocument(
                    new PhotoId(UUID.randomUUID()),
                    new PhotoAngle(PhotoAngle.NamedAngle.LEFT),
                    new ImagePath("test3.jpg"),
                    user,
                    timestamp
            ));
        });
    }
}