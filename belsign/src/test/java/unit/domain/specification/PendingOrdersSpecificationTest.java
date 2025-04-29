package unit.domain.specification;


import domain.model.order.Order;
import domain.model.order.OrderId;
import domain.model.order.photodocument.ImagePath;
import domain.model.order.photodocument.PhotoAngle;
import domain.model.order.photodocument.PhotoDocument;
import domain.model.order.photodocument.PhotoId;
import domain.model.shared.Timestamp;
import domain.model.user.EmailAddress;
import domain.model.user.User;
import domain.model.user.Username;
import domain.specification.PendingOrdersSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PendingOrdersSpecificationTest {

    private Order order;
    private User user;
    private Timestamp timestamp;

    @BeforeEach
    void setUp() {
        // Create a user
        user = new User(
                new Username("testuser"),
                new EmailAddress("test@example.com")
        );
        
        // Create a timestamp
        timestamp = Timestamp.now();
        
        // Create an order
        order = new Order(
                new OrderId(UUID.randomUUID()),
                user,
                timestamp
        );
    }

    @Test
    void shouldSatisfyWhenOrderHasPendingPhotos() {
        // Add a pending photo to the order
        PhotoDocument photo = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.FRONT),
                new ImagePath("test.jpg"),
                user,
                timestamp
        );
        order.addPhoto(photo);
        
        // Create specification
        PendingOrdersSpecification spec = new PendingOrdersSpecification();
        
        // Order should satisfy the specification
        assertTrue(spec.isSatisfiedBy(order));
    }

    @Test
    void shouldNotSatisfyWhenOrderHasNoPhotos() {
        // Create specification
        PendingOrdersSpecification spec = new PendingOrdersSpecification();
        
        // Order should not satisfy the specification
        assertFalse(spec.isSatisfiedBy(order));
    }

    @Test
    void shouldNotSatisfyWhenAllPhotosAreApproved() {
        // Add a photo to the order
        PhotoDocument photo = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.FRONT),
                new ImagePath("test.jpg"),
                user,
                timestamp
        );
        order.addPhoto(photo);
        
        // Approve the photo
        photo.approve(user, Timestamp.now());
        
        // Create specification
        PendingOrdersSpecification spec = new PendingOrdersSpecification();
        
        // Order should not satisfy the specification
        assertFalse(spec.isSatisfiedBy(order));
    }

    @Test
    void shouldSatisfyWhenSomePhotosArePending() {
        // Add an approved photo to the order
        PhotoDocument approvedPhoto = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.FRONT),
                new ImagePath("approved.jpg"),
                user,
                timestamp
        );
        order.addPhoto(approvedPhoto);
        approvedPhoto.approve(user, Timestamp.now());
        
        // Add a pending photo to the order
        PhotoDocument pendingPhoto = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.BACK),
                new ImagePath("pending.jpg"),
                user,
                timestamp
        );
        order.addPhoto(pendingPhoto);
        
        // Create specification
        PendingOrdersSpecification spec = new PendingOrdersSpecification();
        
        // Order should satisfy the specification
        assertTrue(spec.isSatisfiedBy(order));
    }
}