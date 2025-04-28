package unit.domain.specification;

import com.belman.belsign.domain.model.order.Order;
import com.belman.belsign.domain.model.order.OrderId;
import com.belman.belsign.domain.model.order.photodocument.ImagePath;
import com.belman.belsign.domain.model.order.photodocument.PhotoAngle;
import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import com.belman.belsign.domain.model.order.photodocument.PhotoId;
import com.belman.belsign.domain.model.shared.Timestamp;
import com.belman.belsign.domain.model.user.EmailAddress;
import com.belman.belsign.domain.model.user.User;
import com.belman.belsign.domain.model.user.Username;
import com.belman.belsign.domain.specification.MinPhotosSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
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
