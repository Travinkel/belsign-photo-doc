package com.belman.unit.domain.aggregates;

import com.belman.domain.aggregates.User;
import com.belman.domain.customer.CustomerAggregate;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.enums.OrderStatus;
import com.belman.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OrderAggregate aggregate.
 */
class OrderAggregateTest {

    private OrderId orderId;
    private OrderNumber orderNumber;
    private User createdBy;
    private Timestamp createdAt;
    private CustomerAggregate customer;
    private ProductDescription productDescription;
    private DeliveryInformation deliveryInformation;
    private PhotoDocument photoDocument;

    @BeforeEach
    void setUp() {
        // Create test data
        orderId = OrderId.newId();
        orderNumber = new OrderNumber("01/23-456789-12345678");

        // Create a user
        UserId userId = UserId.newId();
        Username username = new Username("testuser");
        HashedPassword password = new HashedPassword("hashedpassword123");
        EmailAddress userEmail = new EmailAddress("user@example.com");
        createdBy = new User(userId, username, password, userEmail);

        // Create a timestamp
        createdAt = new Timestamp(Instant.now());

        // Create a customer
        CustomerId customerId = new CustomerId(java.util.UUID.randomUUID());
        EmailAddress customerEmail = new EmailAddress("customer@example.com");
        Company company = Company.withName("Test Company");
        customer = CustomerAggregate.company(customerId, company, customerEmail);

        // Create a product description
        productDescription = new ProductDescription("Test Product", "A test product", "Test specifications");

        // Create delivery information
        deliveryInformation = new DeliveryInformation("123 Test St", new Timestamp(Instant.now().plusSeconds(86400)), "Handle with care");

        // Create a photo document
        PhotoId photoId = PhotoId.newId();
        PhotoAngle angle = new PhotoAngle(PhotoAngle.NamedAngle.FRONT);
        ImagePath imagePath = new ImagePath("/path/to/image.jpg");
        photoDocument = new PhotoDocument(photoId, angle, imagePath, createdBy, createdAt);
    }

    @Test
    void constructor_withValidParameters_shouldCreateOrder() {
        // When
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // Then
        assertEquals(orderId, orderAggregate.getId());
        assertEquals(orderNumber, orderAggregate.getOrderNumber());
        assertEquals(createdBy, orderAggregate.getCreatedBy());
        assertEquals(createdAt, orderAggregate.getCreatedAt());
        assertEquals(OrderStatus.PENDING, orderAggregate.getStatus());
        assertTrue(orderAggregate.getPhotos().isEmpty());
    }

    @Test
    void constructor_withNullId_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderAggregate(null, orderNumber, createdBy, createdAt));
    }

    @Test
    void constructor_withNullOrderNumber_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderAggregate(orderId, null, createdBy, createdAt));
    }

    @Test
    void constructor_withNullCreatedBy_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderAggregate(orderId, orderNumber, null, createdAt));
    }

    @Test
    void constructor_withNullCreatedAt_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderAggregate(orderId, orderNumber, createdBy, null));
    }

    @Test
    void setOrderNumber_withValidOrderNumber_shouldUpdateOrderNumber() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, createdBy, createdAt);

        // When
        orderAggregate.setOrderNumber(orderNumber);

        // Then
        assertEquals(orderNumber, orderAggregate.getOrderNumber());
    }

    @Test
    void setOrderNumber_withNullOrderNumber_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setOrderNumber(null));
    }

    @Test
    void setCustomer_withValidCustomer_shouldUpdateCustomer() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When
        orderAggregate.setCustomer(customer);

        // Then
        assertEquals(customer, orderAggregate.getCustomer());
    }

    @Test
    void setCustomer_withNullCustomer_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setCustomer(null));
    }

    @Test
    void setProductDescription_withValidProductDescription_shouldUpdateProductDescription() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When
        orderAggregate.setProductDescription(productDescription);

        // Then
        assertEquals(productDescription, orderAggregate.getProductDescription());
    }

    @Test
    void setProductDescription_withNullProductDescription_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setProductDescription(null));
    }

    @Test
    void setDeliveryInformation_withValidDeliveryInformation_shouldUpdateDeliveryInformation() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When
        orderAggregate.setDeliveryInformation(deliveryInformation);

        // Then
        assertEquals(deliveryInformation, orderAggregate.getDeliveryInformation());
    }

    @Test
    void setDeliveryInformation_withNullDeliveryInformation_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setDeliveryInformation(null));
    }

    @Test
    void setStatus_withValidStatus_shouldUpdateStatus() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When
        orderAggregate.setStatus(OrderStatus.COMPLETED);

        // Then
        assertEquals(OrderStatus.COMPLETED, orderAggregate.getStatus());
    }

    @Test
    void setStatus_withNullStatus_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setStatus(null));
    }

    @Test
    void addPhoto_withValidPhoto_shouldAddPhotoToOrder() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When
        orderAggregate.addPhoto(photoDocument);

        // Then
        List<PhotoDocument> photos = orderAggregate.getPhotos();
        assertEquals(1, photos.size());
        assertTrue(photos.contains(photoDocument));
        assertEquals(orderId, photoDocument.getOrderId());
    }

    @Test
    void addPhoto_withNullPhoto_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.addPhoto(null));
    }

    @Test
    void getApprovedPhotos_withMixedPhotos_shouldReturnOnlyApprovedPhotos() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // Create an approved photo
        PhotoDocument approvedPhoto = new PhotoDocument(PhotoId.newId(), new PhotoAngle(PhotoAngle.NamedAngle.RIGHT), new ImagePath("/path/to/approved.jpg"), createdBy, createdAt);
        approvedPhoto.approve(createdBy, createdAt);

        // Add both photos
        orderAggregate.addPhoto(photoDocument); // Pending
        orderAggregate.addPhoto(approvedPhoto); // Approved

        // When
        List<PhotoDocument> approvedPhotos = orderAggregate.getApprovedPhotos();

        // Then
        assertEquals(1, approvedPhotos.size());
        assertTrue(approvedPhotos.contains(approvedPhoto));
        assertFalse(approvedPhotos.contains(photoDocument));
    }

    @Test
    void getPendingPhotos_withMixedPhotos_shouldReturnOnlyPendingPhotos() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);

        // Create an approved photo
        PhotoDocument approvedPhoto = new PhotoDocument(PhotoId.newId(), new PhotoAngle(PhotoAngle.NamedAngle.LEFT), new ImagePath("/path/to/approved.jpg"), createdBy, createdAt);
        approvedPhoto.approve(createdBy, createdAt);

        // Add both photos
        orderAggregate.addPhoto(photoDocument); // Pending
        orderAggregate.addPhoto(approvedPhoto); // Approved

        // When
        List<PhotoDocument> pendingPhotos = orderAggregate.getPendingPhotos();

        // Then
        assertEquals(1, pendingPhotos.size());
        assertTrue(pendingPhotos.contains(photoDocument));
        assertFalse(pendingPhotos.contains(approvedPhoto));
    }

    @Test
    void isReadyForQaReview_withCompletedStatusAndPhotos_shouldReturnTrue() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);
        orderAggregate.setStatus(OrderStatus.COMPLETED);
        orderAggregate.addPhoto(photoDocument);

        // When/Then
        assertTrue(orderAggregate.isReadyForQaReview());
    }

    @Test
    void isReadyForQaReview_withCompletedStatusButNoPhotos_shouldReturnFalse() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);
        orderAggregate.setStatus(OrderStatus.COMPLETED);

        // When/Then
        assertFalse(orderAggregate.isReadyForQaReview());
    }

    @Test
    void isReadyForQaReview_withPendingStatusAndPhotos_shouldReturnFalse() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);
        orderAggregate.addPhoto(photoDocument);

        // When/Then
        assertFalse(orderAggregate.isReadyForQaReview());
    }

    @Test
    void isApproved_withApprovedStatus_shouldReturnTrue() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);
        orderAggregate.setStatus(OrderStatus.APPROVED);

        // When/Then
        assertTrue(orderAggregate.isApproved());
        assertFalse(orderAggregate.isRejected());
        assertFalse(orderAggregate.isDelivered());
    }

    @Test
    void isRejected_withRejectedStatus_shouldReturnTrue() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);
        orderAggregate.setStatus(OrderStatus.REJECTED);

        // When/Then
        assertFalse(orderAggregate.isApproved());
        assertTrue(orderAggregate.isRejected());
        assertFalse(orderAggregate.isDelivered());
    }

    @Test
    void isDelivered_withDeliveredStatus_shouldReturnTrue() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, createdBy, createdAt);
        orderAggregate.setStatus(OrderStatus.DELIVERED);

        // When/Then
        assertFalse(orderAggregate.isApproved());
        assertFalse(orderAggregate.isRejected());
        assertTrue(orderAggregate.isDelivered());
    }
}
