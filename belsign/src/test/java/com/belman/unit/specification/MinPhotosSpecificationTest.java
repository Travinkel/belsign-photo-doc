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
import com.belman.domain.specification.MinPhotosSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MinPhotosSpecificationTest {

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
    void shouldSatisfyWhenOrderHasEnoughPhotos() {
        // Add 3 photos to the order
        for (int i = 0; i < 3; i++) {
            PhotoDocument photo = new PhotoDocument(
                    new PhotoId(UUID.randomUUID()),
                    new PhotoAngle(PhotoAngle.NamedAngle.FRONT),
                    new ImagePath("test" + i + ".jpg"),
                    user,
                    timestamp
            );
            order.addPhoto(photo);
        }

        // Create specification requiring at least 2 photos
        MinPhotosSpecification spec = new MinPhotosSpecification(2);

        // Order should satisfy the specification
        assertTrue(spec.isSatisfiedBy(order));
    }

    @Test
    void shouldNotSatisfyWhenOrderHasTooFewPhotos() {
        // Add 1 photo to the order
        PhotoDocument photo = new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new PhotoAngle(PhotoAngle.NamedAngle.FRONT),
                new ImagePath("test.jpg"),
                user,
                timestamp
        );
        order.addPhoto(photo);

        // Create specification requiring at least 2 photos
        MinPhotosSpecification spec = new MinPhotosSpecification(2);

        // Order should not satisfy the specification
        assertFalse(spec.isSatisfiedBy(order));
    }

    @Test
    void shouldNotSatisfyWhenOrderHasNoPhotos() {
        // Create specification requiring at least 1 photo
        MinPhotosSpecification spec = new MinPhotosSpecification(1);

        // Order should not satisfy the specification
        assertFalse(spec.isSatisfiedBy(order));
    }

    @Test
    void shouldSatisfyWhenMinPhotosIsZero() {
        // Create specification requiring at least 0 photos
        MinPhotosSpecification spec = new MinPhotosSpecification(0);

        // Order should satisfy the specification
        assertTrue(spec.isSatisfiedBy(order));
    }
}
