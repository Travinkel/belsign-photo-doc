package unit.domain.model.order;

import com.belman.belsign.domain.model.order.Order;
import com.belman.belsign.domain.model.order.OrderId;
import com.belman.belsign.domain.model.order.OrderNumber;
import com.belman.belsign.domain.model.order.photodocument.ImagePath;
import com.belman.belsign.domain.model.order.photodocument.PhotoAngle;
import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import com.belman.belsign.domain.model.order.photodocument.PhotoId;
import com.belman.belsign.domain.model.shared.Timestamp;
import com.belman.belsign.domain.model.user.EmailAddress;
import com.belman.belsign.domain.model.user.User;
import com.belman.belsign.domain.model.user.Username;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

class OrderTest {

    @Test
    void createOrderWithValidData() {
        OrderId orderId = OrderId.newId();
        Username username = new Username("worker1");
        EmailAddress email = new EmailAddress("worker1@belman.dk");
        User creator = new User(username, email);
        Timestamp createdAt = new Timestamp(Instant.now());

        Order order = new Order(orderId, creator, createdAt);

        assertEquals(orderId, order.getId());
        assertEquals(creator, order.getCreatedBy());
        assertEquals(createdAt, order.getCreatedAt());
        assertTrue(order.getPhotos().isEmpty());
    }

    @Test
    void addPhotoToOrderShouldAssignOrderId() {
        OrderId orderId = OrderId.newId();
        Username username = new Username("worker2");
        EmailAddress email = new EmailAddress("worker2@belman.dk");
        User creator = new User(username, email);
        Timestamp createdAt = new Timestamp(Instant.now());
        Order order = new Order(orderId, creator, createdAt);

        // Opret billede med PhotoAngle
        PhotoAngle angle = new PhotoAngle(90.0);
        ImagePath imagePath = new ImagePath("/images/welding1.jpg");
        User uploader = creator; // for nemheds skyld
        Timestamp uploadedAt = new Timestamp(Instant.now());
        PhotoId photoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photo = new PhotoDocument(photoId, angle, imagePath, uploader, uploadedAt);

        order.addPhoto(photo);

        assertEquals(1, order.getPhotos().size());
        assertEquals(orderId, photo.getOrderId());
        assertEquals(angle, photo.getAngle());
    }

    @Test
    void getApprovedPhotosShouldReturnOnlyApproved() {
        OrderId orderId = OrderId.newId();
        Username username = new Username("qa1");
        EmailAddress email = new EmailAddress("qa1@belman.dk");
        User qaUser = new User(username, email);
        Timestamp now = new Timestamp(Instant.now());
        Order order = new Order(orderId, qaUser, now);

        // Godkendt billede
        PhotoAngle angle1 = new PhotoAngle(0.0);
        ImagePath path1 = new ImagePath("/images/approved.jpg");
        PhotoId photoId1 = new PhotoId(UUID.randomUUID());
        PhotoDocument approvedPhoto = new PhotoDocument(photoId1, angle1, path1, qaUser, now);
        approvedPhoto.approve(qaUser, new Timestamp(Instant.now()));
        order.addPhoto(approvedPhoto);

        // Ikke godkendt billede (pending)
        PhotoAngle angle2 = new PhotoAngle(180.0);
        ImagePath path2 = new ImagePath("/images/pending.jpg");
        PhotoId photoId2 = new PhotoId(UUID.randomUUID());
        PhotoDocument pendingPhoto = new PhotoDocument(photoId2, angle2, path2, qaUser, now);
        order.addPhoto(pendingPhoto);

        List<PhotoDocument> approvedPhotos = order.getApprovedPhotos();

        assertEquals(1, approvedPhotos.size());
        assertTrue(approvedPhotos.get(0).isApproved());
    }

    @Test
    void getPendingPhotosShouldReturnOnlyPending() {
        OrderId orderId = OrderId.newId();
        Username username = new Username("qa2");
        EmailAddress email = new EmailAddress("qa2@belman.dk");
        User qaUser = new User(username, email);
        Timestamp now = new Timestamp(Instant.now());
        Order order = new Order(orderId, qaUser, now);

        // Godkendt billede
        PhotoAngle angle1 = new PhotoAngle(270.0);
        ImagePath path1 = new ImagePath("/images/approved.jpg");
        PhotoId photoId1 = new PhotoId(UUID.randomUUID());
        PhotoDocument approvedPhoto = new PhotoDocument(photoId1, angle1, path1, qaUser, now);
        approvedPhoto.approve(qaUser, new Timestamp(Instant.now()));
        order.addPhoto(approvedPhoto);

        // Ikke godkendt billede (pending)
        PhotoAngle angle2 = new PhotoAngle(90.0);
        ImagePath path2 = new ImagePath("/images/pending.jpg");
        PhotoId photoId2 = new PhotoId(UUID.randomUUID());
        PhotoDocument pendingPhoto = new PhotoDocument(photoId2, angle2, path2, qaUser, now);
        order.addPhoto(pendingPhoto);

        List<PhotoDocument> pendingPhotos = order.getPendingPhotos();

        assertEquals(1, pendingPhotos.size());
        assertTrue(pendingPhotos.get(0).isPending());
    }

    @Test
    void createOrderWithOrderNumber() {
        OrderId orderId = OrderId.newId();
        OrderNumber orderNumber = new OrderNumber("12/34-567890-12345678");
        Username username = new Username("worker3");
        EmailAddress email = new EmailAddress("worker3@belman.dk");
        User creator = new User(username, email);
        Timestamp createdAt = new Timestamp(Instant.now());

        Order order = new Order(orderId, orderNumber, creator, createdAt);

        assertEquals(orderId, order.getId());
        assertEquals(orderNumber, order.getOrderNumber());
        assertEquals(creator, order.getCreatedBy());
        assertEquals(createdAt, order.getCreatedAt());
        assertTrue(order.getPhotos().isEmpty());
    }

    @Test
    void setOrderNumberOnExistingOrder() {
        OrderId orderId = OrderId.newId();
        Username username = new Username("worker4");
        EmailAddress email = new EmailAddress("worker4@belman.dk");
        User creator = new User(username, email);
        Timestamp createdAt = new Timestamp(Instant.now());

        Order order = new Order(orderId, creator, createdAt);
        assertNull(order.getOrderNumber());

        OrderNumber orderNumber = new OrderNumber("12/34-567890-12345678");
        order.setOrderNumber(orderNumber);

        assertEquals(orderNumber, order.getOrderNumber());
    }

    @Test
    void setOrderNumberShouldRejectNull() {
        OrderId orderId = OrderId.newId();
        Username username = new Username("worker5");
        EmailAddress email = new EmailAddress("worker5@belman.dk");
        User creator = new User(username, email);
        Timestamp createdAt = new Timestamp(Instant.now());

        Order order = new Order(orderId, creator, createdAt);

        assertThrows(NullPointerException.class, () -> order.setOrderNumber(null));
    }
}
