package com.belman.unit.domain.aggregates;

import com.belman.domain.aggregates.Order;
import com.belman.domain.aggregates.User;
import com.belman.domain.entities.Customer;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.enums.OrderStatus;
import com.belman.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Order aggregate.
 */
class OrderTest {

    private OrderId orderId;
    private OrderNumber orderNumber;
    private User createdBy;
    private Timestamp createdAt;
    private Customer customer;
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
        customer = Customer.company(customerId, company, customerEmail);

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
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // Then
        assertEquals(orderId, order.getId());
        assertEquals(orderNumber, order.getOrderNumber());
        assertEquals(createdBy, order.getCreatedBy());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertTrue(order.getPhotos().isEmpty());
    }

    @Test
    void constructor_withNullId_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new Order(null, orderNumber, createdBy, createdAt));
    }

    @Test
    void constructor_withNullOrderNumber_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new Order(orderId, null, createdBy, createdAt));
    }

    @Test
    void constructor_withNullCreatedBy_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new Order(orderId, orderNumber, null, createdAt));
    }

    @Test
    void constructor_withNullCreatedAt_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new Order(orderId, orderNumber, createdBy, null));
    }

    @Test
    void setOrderNumber_withValidOrderNumber_shouldUpdateOrderNumber() {
        // Given
        Order order = new Order(orderId, createdBy, createdAt);

        // When
        order.setOrderNumber(orderNumber);

        // Then
        assertEquals(orderNumber, order.getOrderNumber());
    }

    @Test
    void setOrderNumber_withNullOrderNumber_shouldThrowException() {
        // Given
        Order order = new Order(orderId, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> order.setOrderNumber(null));
    }

    @Test
    void setCustomer_withValidCustomer_shouldUpdateCustomer() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When
        order.setCustomer(customer);

        // Then
        assertEquals(customer, order.getCustomer());
    }

    @Test
    void setCustomer_withNullCustomer_shouldThrowException() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> order.setCustomer(null));
    }

    @Test
    void setProductDescription_withValidProductDescription_shouldUpdateProductDescription() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When
        order.setProductDescription(productDescription);

        // Then
        assertEquals(productDescription, order.getProductDescription());
    }

    @Test
    void setProductDescription_withNullProductDescription_shouldThrowException() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> order.setProductDescription(null));
    }

    @Test
    void setDeliveryInformation_withValidDeliveryInformation_shouldUpdateDeliveryInformation() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When
        order.setDeliveryInformation(deliveryInformation);

        // Then
        assertEquals(deliveryInformation, order.getDeliveryInformation());
    }

    @Test
    void setDeliveryInformation_withNullDeliveryInformation_shouldThrowException() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> order.setDeliveryInformation(null));
    }

    @Test
    void setStatus_withValidStatus_shouldUpdateStatus() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When
        order.setStatus(OrderStatus.COMPLETED);

        // Then
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void setStatus_withNullStatus_shouldThrowException() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> order.setStatus(null));
    }

    @Test
    void addPhoto_withValidPhoto_shouldAddPhotoToOrder() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When
        order.addPhoto(photoDocument);

        // Then
        List<PhotoDocument> photos = order.getPhotos();
        assertEquals(1, photos.size());
        assertTrue(photos.contains(photoDocument));
        assertEquals(orderId, photoDocument.getOrderId());
    }

    @Test
    void addPhoto_withNullPhoto_shouldThrowException() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> order.addPhoto(null));
    }

    @Test
    void getApprovedPhotos_withMixedPhotos_shouldReturnOnlyApprovedPhotos() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // Create an approved photo
        PhotoDocument approvedPhoto = new PhotoDocument(PhotoId.newId(), new PhotoAngle(PhotoAngle.NamedAngle.RIGHT), new ImagePath("/path/to/approved.jpg"), createdBy, createdAt);
        approvedPhoto.approve(createdBy, createdAt);

        // Add both photos
        order.addPhoto(photoDocument); // Pending
        order.addPhoto(approvedPhoto); // Approved

        // When
        List<PhotoDocument> approvedPhotos = order.getApprovedPhotos();

        // Then
        assertEquals(1, approvedPhotos.size());
        assertTrue(approvedPhotos.contains(approvedPhoto));
        assertFalse(approvedPhotos.contains(photoDocument));
    }

    @Test
    void getPendingPhotos_withMixedPhotos_shouldReturnOnlyPendingPhotos() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);

        // Create an approved photo
        PhotoDocument approvedPhoto = new PhotoDocument(PhotoId.newId(), new PhotoAngle(PhotoAngle.NamedAngle.LEFT), new ImagePath("/path/to/approved.jpg"), createdBy, createdAt);
        approvedPhoto.approve(createdBy, createdAt);

        // Add both photos
        order.addPhoto(photoDocument); // Pending
        order.addPhoto(approvedPhoto); // Approved

        // When
        List<PhotoDocument> pendingPhotos = order.getPendingPhotos();

        // Then
        assertEquals(1, pendingPhotos.size());
        assertTrue(pendingPhotos.contains(photoDocument));
        assertFalse(pendingPhotos.contains(approvedPhoto));
    }

    @Test
    void isReadyForQaReview_withCompletedStatusAndPhotos_shouldReturnTrue() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);
        order.setStatus(OrderStatus.COMPLETED);
        order.addPhoto(photoDocument);

        // When/Then
        assertTrue(order.isReadyForQaReview());
    }

    @Test
    void isReadyForQaReview_withCompletedStatusButNoPhotos_shouldReturnFalse() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);
        order.setStatus(OrderStatus.COMPLETED);

        // When/Then
        assertFalse(order.isReadyForQaReview());
    }

    @Test
    void isReadyForQaReview_withPendingStatusAndPhotos_shouldReturnFalse() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);
        order.addPhoto(photoDocument);

        // When/Then
        assertFalse(order.isReadyForQaReview());
    }

    @Test
    void isApproved_withApprovedStatus_shouldReturnTrue() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);
        order.setStatus(OrderStatus.APPROVED);

        // When/Then
        assertTrue(order.isApproved());
        assertFalse(order.isRejected());
        assertFalse(order.isDelivered());
    }

    @Test
    void isRejected_withRejectedStatus_shouldReturnTrue() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);
        order.setStatus(OrderStatus.REJECTED);

        // When/Then
        assertFalse(order.isApproved());
        assertTrue(order.isRejected());
        assertFalse(order.isDelivered());
    }

    @Test
    void isDelivered_withDeliveredStatus_shouldReturnTrue() {
        // Given
        Order order = new Order(orderId, orderNumber, createdBy, createdAt);
        order.setStatus(OrderStatus.DELIVERED);

        // When/Then
        assertFalse(order.isApproved());
        assertFalse(order.isRejected());
        assertTrue(order.isDelivered());
    }
}
