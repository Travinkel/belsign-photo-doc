package com.belman.unit.specification;


import com.belman.domain.aggregates.Order;
import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.valueobjects.ImagePath;
import com.belman.domain.valueobjects.PhotoAngle;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.valueobjects.PhotoId;
import com.belman.domain.valueobjects.Timestamp;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.aggregates.User;
import com.belman.domain.valueobjects.Username;
import com.belman.domain.specification.PendingOrdersSpecification;
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
                new HashedPassword("hashedPassword123"),
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
