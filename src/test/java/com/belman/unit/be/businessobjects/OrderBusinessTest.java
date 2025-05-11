package com.belman.unit.be.businessobjects;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.Timestamp;
import com.belman.domain.customer.Company;
import com.belman.domain.customer.CustomerBusiness;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.customer.CustomerType;
import com.belman.domain.order.*;
import com.belman.domain.order.photo.Photo;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.order.photo.PhotoTemplate;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OrderBusiness business object.
 */
class OrderBusinessTest {

    private OrderId orderId;
    private OrderNumber orderNumber;
    private UserBusiness createdBy;
    private UserReference userRef;
    private Timestamp createdAt;
    private CustomerBusiness customer;
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

        createdBy = new UserBusiness.Builder()
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

        customer = new CustomerBusiness.Builder()
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
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // Then
        assertEquals(orderId, orderBusiness.getId());
        assertEquals(orderNumber, orderBusiness.getOrderNumber());
        assertEquals(userRef, orderBusiness.getCreatedBy());
        assertEquals(createdAt, orderBusiness.getCreatedAt());
        assertEquals(OrderStatus.PENDING, orderBusiness.getStatus());
        assertTrue(orderBusiness.getPhotos().isEmpty());
    }

    @Test
    void constructor_withNullId_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderBusiness(null, orderNumber, userRef, createdAt));
    }

    @Test
    void constructor_withNullOrderNumber_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderBusiness(orderId, null, userRef, createdAt));
    }

    @Test
    void constructor_withNullCreatedBy_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderBusiness(orderId, orderNumber, null, createdAt));
    }

    @Test
    void constructor_withNullCreatedAt_shouldThrowException() {
        // When/Then
        assertThrows(NullPointerException.class, () -> new OrderBusiness(orderId, orderNumber, userRef, null));
    }

    @Test
    void setOrderNumber_withValidOrderNumber_shouldUpdateOrderNumber() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, userRef, createdAt);

        // When
        orderBusiness.setOrderNumber(orderNumber);

        // Then
        assertEquals(orderNumber, orderBusiness.getOrderNumber());
    }

    @Test
    void setOrderNumber_withNullOrderNumber_shouldThrowException() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderBusiness.setOrderNumber(null));
    }

    @Test
    void setCustomerId_withValidCustomerId_shouldUpdateCustomerId() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When
        orderBusiness.setCustomerId(customer.getId());

        // Then
        assertEquals(customer.getId(), orderBusiness.getCustomerId());
    }

    @Test
    void setCustomerId_withNullCustomerId_shouldThrowException() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderBusiness.setCustomerId(null));
    }

    @Test
    void setProductDescription_withValidProductDescription_shouldUpdateProductDescription() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When
        orderBusiness.setProductDescription(productDescription);

        // Then
        assertEquals(productDescription, orderBusiness.getProductDescription());
    }

    @Test
    void setProductDescription_withNullProductDescription_shouldThrowException() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderBusiness.setProductDescription(null));
    }

    @Test
    void setDeliveryInformation_withValidDeliveryInformation_shouldUpdateDeliveryInformation() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When
        orderBusiness.setDeliveryInformation(deliveryInformation);

        // Then
        assertEquals(deliveryInformation, orderBusiness.getDeliveryInformation());
    }

    @Test
    void setDeliveryInformation_withNullDeliveryInformation_shouldThrowException() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderBusiness.setDeliveryInformation(null));
    }

    @Test
    void setStatus_withValidStatus_shouldUpdateStatus() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When
        orderBusiness.setStatus(OrderStatus.COMPLETED);

        // Then
        assertEquals(OrderStatus.COMPLETED, orderBusiness.getStatus());
    }

    @Test
    void setStatus_withNullStatus_shouldThrowException() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderBusiness.setStatus(null));
    }

    @Test
    void addPhoto_withValidPhoto_shouldAddPhotoToOrder() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When
        orderBusiness.addPhoto(photoDocument);

        // Then
        List<PhotoDocument> photos = orderBusiness.getPhotos();
        assertEquals(1, photos.size());
        assertTrue(photos.contains(photoDocument));
        assertEquals(orderId, photoDocument.getOrderId());
    }

    @Test
    void addPhoto_withNullPhoto_shouldThrowException() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // When/Then
        assertThrows(NullPointerException.class, () -> orderBusiness.addPhoto(null));
    }

    @Test
    void getApprovedPhotos_withMixedPhotos_shouldReturnOnlyApprovedPhotos() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

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
        orderBusiness.addPhoto(photoDocument); // Pending
        orderBusiness.addPhoto(approvedPhoto); // Approved

        // When
        List<PhotoDocument> approvedPhotos = orderBusiness.getApprovedPhotos();

        // Then
        assertEquals(1, approvedPhotos.size());
        assertTrue(approvedPhotos.contains(approvedPhoto));
        assertFalse(approvedPhotos.contains(photoDocument));
    }

    @Test
    void getPendingPhotos_withMixedPhotos_shouldReturnOnlyPendingPhotos() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

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
        orderBusiness.addPhoto(photoDocument); // Pending
        orderBusiness.addPhoto(approvedPhoto); // Approved

        // When
        List<PhotoDocument> pendingPhotos = orderBusiness.getPendingPhotos();

        // Then
        assertEquals(1, pendingPhotos.size());
        assertTrue(pendingPhotos.contains(photoDocument));
        assertFalse(pendingPhotos.contains(approvedPhoto));
    }

    @Test
    void isReadyForQaReview_withCompletedStatusAndPhotos_shouldReturnTrue() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);
        orderBusiness.setStatus(OrderStatus.COMPLETED);
        orderBusiness.addPhoto(photoDocument);

        // When/Then
        assertTrue(orderBusiness.isReadyForQaReview());
    }

    @Test
    void isReadyForQaReview_withCompletedStatusButNoPhotos_shouldReturnFalse() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);
        orderBusiness.setStatus(OrderStatus.COMPLETED);

        // When/Then
        assertFalse(orderBusiness.isReadyForQaReview());
    }

    @Test
    void isReadyForQaReview_withPendingStatusAndPhotos_shouldReturnFalse() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);
        orderBusiness.addPhoto(photoDocument);

        // When/Then
        assertFalse(orderBusiness.isReadyForQaReview());
    }

    @Test
    void getStatus_withApprovedStatus_shouldReturnApproved() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);
        orderBusiness.setStatus(OrderStatus.APPROVED);

        // When/Then
        assertEquals(OrderStatus.APPROVED, orderBusiness.getStatus());
        assertNotEquals(OrderStatus.REJECTED, orderBusiness.getStatus());
        assertNotEquals(OrderStatus.DELIVERED, orderBusiness.getStatus());
    }

    @Test
    void getStatus_withRejectedStatus_shouldReturnRejected() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);
        orderBusiness.setStatus(OrderStatus.REJECTED);

        // When/Then
        assertNotEquals(OrderStatus.APPROVED, orderBusiness.getStatus());
        assertEquals(OrderStatus.REJECTED, orderBusiness.getStatus());
        assertNotEquals(OrderStatus.DELIVERED, orderBusiness.getStatus());
    }

    @Test
    void getStatus_withDeliveredStatus_shouldReturnDelivered() {
        // Given
        OrderBusiness orderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);
        orderBusiness.setStatus(OrderStatus.DELIVERED);

        // When/Then
        assertNotEquals(OrderStatus.APPROVED, orderBusiness.getStatus());
        assertNotEquals(OrderStatus.REJECTED, orderBusiness.getStatus());
        assertEquals(OrderStatus.DELIVERED, orderBusiness.getStatus());
    }
}