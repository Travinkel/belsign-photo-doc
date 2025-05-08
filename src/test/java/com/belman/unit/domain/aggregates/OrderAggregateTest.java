package com.belman.unit.domain.aggregates;

import com.belman.business.richbe.user.UserAggregate;
import com.belman.business.richbe.user.UserReference;
import com.belman.business.richbe.customer.CustomerAggregate;
import com.belman.business.richbe.customer.CustomerType;
import com.belman.business.richbe.order.OrderAggregate;
import com.belman.business.richbe.order.OrderId;
import com.belman.business.richbe.order.OrderNumber;
import com.belman.business.richbe.order.OrderStatus;
import com.belman.business.richbe.order.ProductDescription;
import com.belman.business.richbe.order.DeliveryInformation;
import com.belman.business.richbe.order.photo.PhotoDocument;
import com.belman.business.richbe.order.photo.PhotoId;
import com.belman.business.richbe.order.photo.Photo;
import com.belman.business.richbe.order.photo.PhotoTemplate;
import com.belman.business.richbe.common.Timestamp;
import com.belman.business.richbe.common.EmailAddress;
import com.belman.business.richbe.security.HashedPassword;
import com.belman.business.richbe.customer.CustomerId;
import com.belman.business.richbe.customer.Company;
import com.belman.business.richbe.user.UserId;
import com.belman.business.richbe.user.Username;
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
    private UserAggregate createdBy;
    private UserReference userRef;
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
        HashedPassword password = HashedPassword.fromPlainText("password123", null);
        EmailAddress userEmail = new EmailAddress("user@example.com");

        createdBy = new UserAggregate.Builder()
            .id(userId)
            .username(username)
            .password(password)
            .email(userEmail)
            .build();

        userRef = new UserReference(userId, username);

        // Create a timestamp
        createdAt = new Timestamp(Instant.now());

        // Create a customer
        CustomerId customerId = CustomerId.newId();
        EmailAddress customerEmail = new EmailAddress("customer@example.com");
        Company company = new Company("Test Company", "123 Test St", "REG-12345");

        customer = new CustomerAggregate.Builder()
            .withId(customerId)
            .withType(CustomerType.COMPANY)
            .withCompany(company)
            .withEmail(customerEmail)
            .build();

        // Create a product description
        productDescription = new ProductDescription("Test Product", "A test product", "Test specifications");

        // Create delivery information
        deliveryInformation = new DeliveryInformation(
            "123 Test St", 
            java.time.LocalDate.now().plusDays(1), 
            "Handle with care",
            new EmailAddress("delivery@example.com"),
            "+1234567890"
        );

        // Create a photo document
        PhotoId photoId = PhotoId.newId();
        PhotoTemplate template = PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
        Photo photo = new Photo("/path/to/image.jpg");

        photoDocument = PhotoDocument.builder()
            .photoId(photoId)
            .template(template)
            .imagePath(photo)
            .uploadedBy(createdBy)
            .uploadedAt(createdAt)
            .build();
    }

    @Test
    void constructor_withValidParameters_shouldCreateOrder() {
        // When
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // Then
        assertEquals(orderId, orderAggregate.getId());
        assertEquals(orderNumber, orderAggregate.getOrderNumber());
        assertEquals(userRef, orderAggregate.getCreatedBy());
        assertEquals(createdAt, orderAggregate.getCreatedAt());
        assertEquals(OrderStatus.PENDING, orderAggregate.getStatus());
        assertTrue(orderAggregate.getPhotos().isEmpty());
    }

    @Test
    void constructor_withNullId_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderAggregate(null, orderNumber, userRef, createdAt));
    }

    @Test
    void constructor_withNullOrderNumber_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderAggregate(orderId, null, userRef, createdAt));
    }

    @Test
    void constructor_withNullCreatedBy_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderAggregate(orderId, orderNumber, null, createdAt));
    }

    @Test
    void constructor_withNullCreatedAt_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderAggregate(orderId, orderNumber, userRef, null));
    }

    @Test
    void setOrderNumber_withValidOrderNumber_shouldUpdateOrderNumber() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, userRef, createdAt);

        // When
        orderAggregate.setOrderNumber(orderNumber);

        // Then
        assertEquals(orderNumber, orderAggregate.getOrderNumber());
    }

    @Test
    void setOrderNumber_withNullOrderNumber_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setOrderNumber(null));
    }

    @Test
    void setCustomerId_withValidCustomerId_shouldUpdateCustomerId() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // When
        orderAggregate.setCustomerId(customer.getId());

        // Then
        assertEquals(customer.getId(), orderAggregate.getCustomerId());
    }

    @Test
    void setCustomerId_withNullCustomerId_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setCustomerId(null));
    }

    @Test
    void setProductDescription_withValidProductDescription_shouldUpdateProductDescription() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // When
        orderAggregate.setProductDescription(productDescription);

        // Then
        assertEquals(productDescription, orderAggregate.getProductDescription());
    }

    @Test
    void setProductDescription_withNullProductDescription_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setProductDescription(null));
    }

    @Test
    void setDeliveryInformation_withValidDeliveryInformation_shouldUpdateDeliveryInformation() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // When
        orderAggregate.setDeliveryInformation(deliveryInformation);

        // Then
        assertEquals(deliveryInformation, orderAggregate.getDeliveryInformation());
    }

    @Test
    void setDeliveryInformation_withNullDeliveryInformation_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setDeliveryInformation(null));
    }

    @Test
    void setStatus_withValidStatus_shouldUpdateStatus() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // When
        orderAggregate.setStatus(OrderStatus.COMPLETED);

        // Then
        assertEquals(OrderStatus.COMPLETED, orderAggregate.getStatus());
    }

    @Test
    void setStatus_withNullStatus_shouldThrowException() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.setStatus(null));
    }

    @Test
    void addPhoto_withValidPhoto_shouldAddPhotoToOrder() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

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
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderAggregate.addPhoto(null));
    }

    @Test
    void getApprovedPhotos_withMixedPhotos_shouldReturnOnlyApprovedPhotos() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // Create an approved photo
        PhotoId photoId = PhotoId.newId();
        PhotoTemplate template = PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY;
        Photo photo = new Photo("/path/to/approved.jpg");

        PhotoDocument approvedPhoto = PhotoDocument.builder()
            .photoId(photoId)
            .template(template)
            .imagePath(photo)
            .uploadedBy(createdBy)
            .uploadedAt(createdAt)
            .build();

        approvedPhoto.approve(userRef, createdAt);

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
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // Create an approved photo
        PhotoId photoId = PhotoId.newId();
        PhotoTemplate template = PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY;
        Photo photo = new Photo("/path/to/approved.jpg");

        PhotoDocument approvedPhoto = PhotoDocument.builder()
            .photoId(photoId)
            .template(template)
            .imagePath(photo)
            .uploadedBy(createdBy)
            .uploadedAt(createdAt)
            .build();

        approvedPhoto.approve(userRef, createdAt);

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
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);
        orderAggregate.setStatus(OrderStatus.COMPLETED);
        orderAggregate.addPhoto(photoDocument);

        // When/Then
        assertTrue(orderAggregate.isReadyForQaReview());
    }

    @Test
    void isReadyForQaReview_withCompletedStatusButNoPhotos_shouldReturnFalse() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);
        orderAggregate.setStatus(OrderStatus.COMPLETED);

        // When/Then
        assertFalse(orderAggregate.isReadyForQaReview());
    }

    @Test
    void isReadyForQaReview_withPendingStatusAndPhotos_shouldReturnFalse() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);
        orderAggregate.addPhoto(photoDocument);

        // When/Then
        assertFalse(orderAggregate.isReadyForQaReview());
    }

    @Test
    void getStatus_withApprovedStatus_shouldReturnApproved() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);
        orderAggregate.setStatus(OrderStatus.APPROVED);

        // When/Then
        assertEquals(OrderStatus.APPROVED, orderAggregate.getStatus());
        assertNotEquals(OrderStatus.REJECTED, orderAggregate.getStatus());
        assertNotEquals(OrderStatus.DELIVERED, orderAggregate.getStatus());
    }

    @Test
    void getStatus_withRejectedStatus_shouldReturnRejected() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);
        orderAggregate.setStatus(OrderStatus.REJECTED);

        // When/Then
        assertNotEquals(OrderStatus.APPROVED, orderAggregate.getStatus());
        assertEquals(OrderStatus.REJECTED, orderAggregate.getStatus());
        assertNotEquals(OrderStatus.DELIVERED, orderAggregate.getStatus());
    }

    @Test
    void getStatus_withDeliveredStatus_shouldReturnDelivered() {
        // Given
        OrderAggregate orderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);
        orderAggregate.setStatus(OrderStatus.DELIVERED);

        // When/Then
        assertNotEquals(OrderStatus.APPROVED, orderAggregate.getStatus());
        assertNotEquals(OrderStatus.REJECTED, orderAggregate.getStatus());
        assertEquals(OrderStatus.DELIVERED, orderAggregate.getStatus());
    }
}
