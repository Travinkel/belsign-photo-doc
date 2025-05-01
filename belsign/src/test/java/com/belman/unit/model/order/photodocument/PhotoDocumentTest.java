package com.belman.unit.model.order.photodocument;


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
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PhotoDocumentTest {

    @Test
    void photoDocumentShouldStoreAngleCorrectly() {
        PhotoAngle angle = new PhotoAngle(90.0);
        ImagePath path = new ImagePath("/images/photo.jpg");
        Username username = new Username("worker1");
        HashedPassword password = new HashedPassword("hashedPassword123");
        EmailAddress email = new EmailAddress("worker1@belman.dk");
        User uploader = new User(username, password, email);
        Timestamp uploadedAt = new Timestamp(Instant.now());
        PhotoId photoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photo = new PhotoDocument(photoId, angle, path, uploader, uploadedAt);

        assertEquals(angle, photo.getAngle());
        assertEquals(90.0, photo.getAngle().getDegrees());
    }

    @Test
    void invalidPhotoAngleShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PhotoAngle(-1.0));
        assertThrows(IllegalArgumentException.class, () -> new PhotoAngle(360.0));
    }

    @Test
    void photoDocumentShouldBeCreatedWithPendingStatus() {
        PhotoAngle angle = new PhotoAngle(90.0);
        ImagePath path = new ImagePath("/images/photo.jpg");
        Username username = new Username("worker1");
        HashedPassword password = new HashedPassword("hashedPassword123");
        EmailAddress email = new EmailAddress("worker1@belman.dk");
        User uploader = new User(username, password, email);
        Timestamp uploadedAt = new Timestamp(Instant.now());
        PhotoId photoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photo = new PhotoDocument(photoId, angle, path, uploader, uploadedAt);

        assertTrue(photo.isPending());
        assertFalse(photo.isApproved());
        assertEquals(PhotoDocument.ApprovalStatus.PENDING, photo.getStatus());
    }

    @Test
    void photoDocumentShouldBeAssignedToOrder() {
        PhotoAngle angle = new PhotoAngle(90.0);
        ImagePath path = new ImagePath("/images/photo.jpg");
        Username username = new Username("worker1");
        HashedPassword password = new HashedPassword("hashedPassword123");
        EmailAddress email = new EmailAddress("worker1@belman.dk");
        User uploader = new User(username, password, email);
        Timestamp uploadedAt = new Timestamp(Instant.now());
        PhotoId photoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photo = new PhotoDocument(photoId, angle, path, uploader, uploadedAt);

        OrderId orderId = new OrderId(UUID.randomUUID());
        photo.assignToOrder(orderId);

        assertEquals(orderId, photo.getOrderId());
    }

    @Test
    void photoDocumentShouldBeApproved() {
        PhotoAngle angle = new PhotoAngle(90.0);
        ImagePath path = new ImagePath("/images/photo.jpg");
        Username username = new Username("worker1");
        HashedPassword password = new HashedPassword("hashedPassword123");
        EmailAddress email = new EmailAddress("worker1@belman.dk");
        User uploader = new User(username, password, email);
        Timestamp uploadedAt = new Timestamp(Instant.now());
        PhotoId photoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photo = new PhotoDocument(photoId, angle, path, uploader, uploadedAt);

        User reviewer = new User(new Username("qa1"), new HashedPassword("hashedPassword123"), new EmailAddress("qa1@belman.dk"));
        Timestamp reviewedAt = new Timestamp(Instant.now());
        photo.approve(reviewer, reviewedAt);

        assertTrue(photo.isApproved());
        assertFalse(photo.isPending());
        assertEquals(PhotoDocument.ApprovalStatus.APPROVED, photo.getStatus());
        assertEquals(reviewer, photo.getReviewedBy());
        assertEquals(reviewedAt, photo.getReviewedAt());
    }

    @Test
    void photoDocumentShouldBeRejected() {
        PhotoAngle angle = new PhotoAngle(90.0);
        ImagePath path = new ImagePath("/images/photo.jpg");
        Username username = new Username("worker1");
        HashedPassword password = new HashedPassword("hashedPassword123");
        EmailAddress email = new EmailAddress("worker1@belman.dk");
        User uploader = new User(username, password, email);
        Timestamp uploadedAt = new Timestamp(Instant.now());
        PhotoId photoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photo = new PhotoDocument(photoId, angle, path, uploader, uploadedAt);

        User reviewer = new User(new Username("qa1"), new HashedPassword("hashedPassword123"), new EmailAddress("qa1@belman.dk"));
        Timestamp reviewedAt = new Timestamp(Instant.now());
        String reason = "Image is blurry";
        photo.reject(reviewer, reviewedAt, reason);

        assertFalse(photo.isApproved());
        assertFalse(photo.isPending());
        assertEquals(PhotoDocument.ApprovalStatus.REJECTED, photo.getStatus());
        assertEquals(reviewer, photo.getReviewedBy());
        assertEquals(reviewedAt, photo.getReviewedAt());
        assertEquals(reason, photo.getReviewComment());
    }

    @Test
    void approvedPhotoCannotBeApprovedAgain() {
        PhotoAngle angle = new PhotoAngle(90.0);
        ImagePath path = new ImagePath("/images/photo.jpg");
        Username username = new Username("worker1");
        HashedPassword password = new HashedPassword("hashedPassword123");
        EmailAddress email = new EmailAddress("worker1@belman.dk");
        User uploader = new User(username, password, email);
        Timestamp uploadedAt = new Timestamp(Instant.now());
        PhotoId photoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photo = new PhotoDocument(photoId, angle, path, uploader, uploadedAt);

        User reviewer = new User(new Username("qa1"), new HashedPassword("hashedPassword123"), new EmailAddress("qa1@belman.dk"));
        Timestamp reviewedAt = new Timestamp(Instant.now());
        photo.approve(reviewer, reviewedAt);

        assertThrows(IllegalStateException.class, () -> photo.approve(reviewer, reviewedAt));
    }
}
