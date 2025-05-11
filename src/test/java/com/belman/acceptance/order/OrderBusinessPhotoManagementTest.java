package com.belman.acceptance.order;

import com.belman.acceptance.BaseAcceptanceTest;
import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PhoneNumber;
import com.belman.domain.common.Timestamp;
import com.belman.domain.customer.Company;
import com.belman.domain.customer.CustomerAggregate;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.customer.CustomerType;
import com.belman.domain.order.*;
import com.belman.domain.order.photo.Photo;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.order.photo.PhotoTemplate;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import com.belman.repository.persistence.memory.InMemoryOrderRepository;
import com.belman.repository.persistence.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Acceptance tests for order photo management.
 * Verifies that users can create orders and add photos to them.
 */
public class OrderBusinessPhotoManagementTest extends BaseAcceptanceTest {

    private static final String TEST_ORDER_NUMBER = "ORD-12345";
    private OrderRepository orderRepository;
    private UserBusiness productionUser;
    private OrderBusiness testOrderBusiness;

    @BeforeEach
    void setup() {
        logDebug("Setting up order photo management test");

        // Create repositories
        orderRepository = new InMemoryOrderRepository();
        InMemoryUserRepository userRepository = new InMemoryUserRepository();

        // Get a production user from the repository
        Optional<UserBusiness> userOpt = userRepository.findByUsername(new Username("production"));
        assertTrue(userOpt.isPresent(), "Production user should exist in repository");
        productionUser = userOpt.get();
        UserReference userRef = new UserReference(productionUser.getId(), productionUser.getUsername());

        // Create a test order
        OrderId orderId = OrderId.newId();
        OrderNumber orderNumber = new OrderNumber(TEST_ORDER_NUMBER);
        Timestamp createdAt = new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        testOrderBusiness = new OrderBusiness(orderId, orderNumber, userRef, createdAt);

        // Add customer and product details
        CustomerAggregate customer = new CustomerAggregate.Builder()
                .withId(CustomerId.newId())
                .withType(CustomerType.COMPANY)
                .withCompany(new Company("Belman Test Customer", "123 Test Street", "REG-12345"))
                .withEmail(new EmailAddress("customer@example.com"))
                .withPhoneNumber(new PhoneNumber("+123456789"))
                .build();

        testOrderBusiness.setCustomerId(customer.getId());
        testOrderBusiness.setProductDescription(new ProductDescription(
                "Test expansion joint",
                "High-quality expansion joint for industrial use",
                "Material: Steel, Size: 100x100"
        ));

        // Save the order
        orderRepository.save(testOrderBusiness);
        logDebug("Test order created: " + testOrderBusiness.getOrderNumber().toString());
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
        testOrderBusiness.addPhoto(photoDoc);

        // Save the updated order
        orderRepository.save(testOrderBusiness);

        // Retrieve the order from the repository
        Optional<OrderBusiness> retrievedOrderOpt = orderRepository.findByOrderNumber(
                new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "OrderBusiness should exist in repository");

        OrderBusiness retrievedOrderBusiness = retrievedOrderOpt.get();
        List<PhotoDocument> photos = retrievedOrderBusiness.getPhotos();

        // Verify that the photo was added
        assertEquals(1, photos.size(), "OrderBusiness should have one photo");
        assertEquals(photoId, photos.get(0).getPhotoId(), "Photo ID should match");
        assertEquals(template, photos.get(0).getTemplate(), "Photo template should match");
        assertEquals(photo, photos.get(0).getImagePath(), "Image path should match");
        assertEquals(productionUser, photos.get(0).getUploadedBy(), "Uploader should match");

        logDebug("Photo successfully added to order");
    }

    @Test
    @DisplayName("OrderBusiness status can be updated to COMPLETED when photos are added")
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

        testOrderBusiness.addPhoto(photoDoc);

        // Update order status to COMPLETED
        testOrderBusiness.setStatus(OrderStatus.COMPLETED);

        // Save the updated order
        orderRepository.save(testOrderBusiness);

        // Retrieve the order from the repository
        Optional<OrderBusiness> retrievedOrderOpt = orderRepository.findByOrderNumber(
                new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "OrderBusiness should exist in repository");

        OrderBusiness retrievedOrderBusiness = retrievedOrderOpt.get();

        // Verify that the order status was updated
        assertEquals(OrderStatus.COMPLETED, retrievedOrderBusiness.getStatus(),
                "OrderBusiness status should be COMPLETED");
        assertTrue(retrievedOrderBusiness.isReadyForQaReview(), "OrderBusiness should be ready for QA review");

        logDebug("OrderBusiness status successfully updated to COMPLETED");
    }

    @Test
    @DisplayName("OrderBusiness with photos can be retrieved by order number")
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

        testOrderBusiness.addPhoto(photoDoc1);
        testOrderBusiness.addPhoto(photoDoc2);
        testOrderBusiness.addPhoto(photoDoc3);

        // Save the updated order
        orderRepository.save(testOrderBusiness);

        // Retrieve the order from the repository
        Optional<OrderBusiness> retrievedOrderOpt = orderRepository.findByOrderNumber(
                new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "OrderBusiness should exist in repository");

        OrderBusiness retrievedOrderBusiness = retrievedOrderOpt.get();
        List<PhotoDocument> photos = retrievedOrderBusiness.getPhotos();

        // Verify that all photos were retrieved
        assertEquals(3, photos.size(), "OrderBusiness should have three photos");
        assertEquals(template1, photos.get(0).getTemplate(), "First photo should be front view");
        assertEquals(template2, photos.get(1).getTemplate(), "Second photo should be right view");
        assertEquals(template3, photos.get(2).getTemplate(), "Third photo should be left view");

        logDebug("OrderBusiness with photos successfully retrieved by order number");
    }
}
