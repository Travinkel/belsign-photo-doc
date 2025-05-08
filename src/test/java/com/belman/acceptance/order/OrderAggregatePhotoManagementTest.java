package com.belman.acceptance.order;

import com.belman.acceptance.BaseAcceptanceTest;
import com.belman.business.domain.user.UserAggregate;
import com.belman.business.domain.user.UserReference;
import com.belman.business.domain.customer.CustomerAggregate;
import com.belman.business.domain.order.photo.PhotoDocument;
import com.belman.business.domain.order.photo.PhotoId;
import com.belman.business.domain.order.photo.Photo;
import com.belman.business.domain.order.photo.PhotoTemplate;
import com.belman.business.domain.customer.CustomerType;
import com.belman.business.domain.order.OrderStatus;
import com.belman.business.domain.order.OrderRepository;
import com.belman.business.domain.order.OrderId;
import com.belman.business.domain.order.OrderNumber;
import com.belman.business.domain.order.OrderAggregate;
import com.belman.business.domain.order.ProductDescription;
import com.belman.business.domain.order.DeliveryInformation;
import com.belman.business.domain.common.Timestamp;
import com.belman.business.domain.common.EmailAddress;
import com.belman.business.domain.common.PhoneNumber;
import com.belman.business.domain.customer.Company;
import com.belman.business.domain.customer.CustomerId;
import com.belman.business.domain.user.Username;
import com.belman.data.persistence.InMemoryOrderRepository;
import com.belman.data.persistence.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Acceptance tests for order photo management.
 * Verifies that users can create orders and add photos to them.
 */
public class OrderAggregatePhotoManagementTest extends BaseAcceptanceTest {

    private OrderRepository orderRepository;
    private UserAggregate productionUser;
    private OrderAggregate testOrderAggregate;
    private static final String TEST_ORDER_NUMBER = "ORD-12345";

    @BeforeEach
    void setup() {
        logDebug("Setting up order photo management test");

        // Create repositories
        orderRepository = new InMemoryOrderRepository();
        InMemoryUserRepository userRepository = new InMemoryUserRepository();

        // Get a production user from the repository
        Optional<UserAggregate> userOpt = userRepository.findByUsername(new Username("production"));
        assertTrue(userOpt.isPresent(), "Production user should exist in repository");
        productionUser = userOpt.get();
        UserReference userRef = new UserReference(productionUser.getId(), productionUser.getUsername());

        // Create a test order
        OrderId orderId = OrderId.newId();
        OrderNumber orderNumber = new OrderNumber(TEST_ORDER_NUMBER);
        Timestamp createdAt = new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        testOrderAggregate = new OrderAggregate(orderId, orderNumber, userRef, createdAt);

        // Add customer and product details
        CustomerAggregate customer = new CustomerAggregate.Builder()
            .withId(CustomerId.newId())
            .withType(CustomerType.COMPANY)
            .withCompany(new Company("Belman Test Customer", "123 Test Street", "REG-12345"))
            .withEmail(new EmailAddress("customer@example.com"))
            .withPhoneNumber(new PhoneNumber("+123456789"))
            .build();

        testOrderAggregate.setCustomerId(customer.getId());
        testOrderAggregate.setProductDescription(new ProductDescription(
            "Test expansion joint",
            "High-quality expansion joint for industrial use",
            "Material: Steel, Size: 100x100"
        ));

        // Save the order
        orderRepository.save(testOrderAggregate);
        logDebug("Test order created: " + testOrderAggregate.getOrderNumber().toString());
    }

    @Test
    @DisplayName("Production user can add photos to an order")
    void testAddPhotoToOrder() {
        logDebug("Testing adding photo to order");

        // Create a photo document
        PhotoId photoId = PhotoId.newId();
        PhotoTemplate template = PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
        Photo photo = new Photo("/path/to/test/image.jpg");
        Timestamp uploadedAt = new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        UserReference userRef = new UserReference(productionUser.getId(), productionUser.getUsername());

        PhotoDocument photoDoc = PhotoDocument.builder()
            .photoId(photoId)
            .template(template)
            .imagePath(photo)
            .uploadedBy(productionUser)
            .uploadedAt(uploadedAt)
            .build();

        // Add the photo to the order
        testOrderAggregate.addPhoto(photoDoc);

        // Save the updated order
        orderRepository.save(testOrderAggregate);

        // Retrieve the order from the repository
        Optional<OrderAggregate> retrievedOrderOpt = orderRepository.findByOrderNumber(new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "OrderAggregate should exist in repository");

        OrderAggregate retrievedOrderAggregate = retrievedOrderOpt.get();
        List<PhotoDocument> photos = retrievedOrderAggregate.getPhotos();

        // Verify that the photo was added
        assertEquals(1, photos.size(), "OrderAggregate should have one photo");
        assertEquals(photoId, photos.get(0).getPhotoId(), "Photo ID should match");
        assertEquals(template, photos.get(0).getTemplate(), "Photo template should match");
        assertEquals(photo, photos.get(0).getImagePath(), "Image path should match");
        assertEquals(productionUser, photos.get(0).getUploadedBy(), "Uploader should match");

        logDebug("Photo successfully added to order");
    }

    @Test
    @DisplayName("OrderAggregate status can be updated to COMPLETED when photos are added")
    void testUpdateOrderStatusToCompleted() {
        logDebug("Testing updating order status to COMPLETED");

        // Create and add a photo document
        PhotoId photoId = PhotoId.newId();
        PhotoTemplate template = PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
        Photo photo = new Photo("/path/to/test/image.jpg");
        Timestamp uploadedAt = new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        PhotoDocument photoDoc = PhotoDocument.builder()
            .photoId(photoId)
            .template(template)
            .imagePath(photo)
            .uploadedBy(productionUser)
            .uploadedAt(uploadedAt)
            .build();

        testOrderAggregate.addPhoto(photoDoc);

        // Update order status to COMPLETED
        testOrderAggregate.setStatus(OrderStatus.COMPLETED);

        // Save the updated order
        orderRepository.save(testOrderAggregate);

        // Retrieve the order from the repository
        Optional<OrderAggregate> retrievedOrderOpt = orderRepository.findByOrderNumber(new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "OrderAggregate should exist in repository");

        OrderAggregate retrievedOrderAggregate = retrievedOrderOpt.get();

        // Verify that the order status was updated
        assertEquals(OrderStatus.COMPLETED, retrievedOrderAggregate.getStatus(), "OrderAggregate status should be COMPLETED");
        assertTrue(retrievedOrderAggregate.isReadyForQaReview(), "OrderAggregate should be ready for QA review");

        logDebug("OrderAggregate status successfully updated to COMPLETED");
    }

    @Test
    @DisplayName("OrderAggregate with photos can be retrieved by order number")
    void testRetrieveOrderWithPhotosByOrderNumber() {
        logDebug("Testing retrieving order with photos by order number");

        // Create and add multiple photo documents
        PhotoId photoId1 = PhotoId.newId();
        PhotoTemplate template1 = PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
        Photo photo1 = new Photo("/path/to/test/image1.jpg");
        Timestamp uploadedAt1 = new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        PhotoDocument photoDoc1 = PhotoDocument.builder()
            .photoId(photoId1)
            .template(template1)
            .imagePath(photo1)
            .uploadedBy(productionUser)
            .uploadedAt(uploadedAt1)
            .build();

        // Side view from the right
        PhotoId photoId2 = PhotoId.newId();
        PhotoTemplate template2 = PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY;
        Photo photo2 = new Photo("/path/to/test/image2.jpg");
        Timestamp uploadedAt2 = new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        PhotoDocument photoDoc2 = PhotoDocument.builder()
            .photoId(photoId2)
            .template(template2)
            .imagePath(photo2)
            .uploadedBy(productionUser)
            .uploadedAt(uploadedAt2)
            .build();

        // Side view from the left
        PhotoId photoId3 = PhotoId.newId();
        PhotoTemplate template3 = PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY;
        Photo photo3 = new Photo("/path/to/test/image3.jpg");
        Timestamp uploadedAt3 = new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        PhotoDocument photoDoc3 = PhotoDocument.builder()
            .photoId(photoId3)
            .template(template3)
            .imagePath(photo3)
            .uploadedBy(productionUser)
            .uploadedAt(uploadedAt3)
            .build();

        testOrderAggregate.addPhoto(photoDoc1);
        testOrderAggregate.addPhoto(photoDoc2);
        testOrderAggregate.addPhoto(photoDoc3);

        // Save the updated order
        orderRepository.save(testOrderAggregate);

        // Retrieve the order from the repository
        Optional<OrderAggregate> retrievedOrderOpt = orderRepository.findByOrderNumber(new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "OrderAggregate should exist in repository");

        OrderAggregate retrievedOrderAggregate = retrievedOrderOpt.get();
        List<PhotoDocument> photos = retrievedOrderAggregate.getPhotos();

        // Verify that all photos were retrieved
        assertEquals(3, photos.size(), "OrderAggregate should have three photos");
        assertEquals(template1, photos.get(0).getTemplate(), "First photo should be front view");
        assertEquals(template2, photos.get(1).getTemplate(), "Second photo should be right view");
        assertEquals(template3, photos.get(2).getTemplate(), "Third photo should be left view");

        logDebug("OrderAggregate with photos successfully retrieved by order number");
    }
}
