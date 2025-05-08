package com.belman.acceptance.order;

import com.belman.acceptance.BaseAcceptanceTest;
import com.belman.domain.aggregates.User;
import com.belman.business.domain.customer.CustomerAggregate;
import com.belman.business.domain.order.photo.PhotoDocument;
import com.belman.domain.enums.CustomerType;
import com.belman.domain.enums.OrderStatus;
import com.belman.business.domain.order.OrderRepository;
import com.belman.domain.valueobjects.*;
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
    private User productionUser;
    private OrderAggregate testOrderAggregate;
    private static final String TEST_ORDER_NUMBER = "ORD-12345";

    @BeforeEach
    void setup() {
        logDebug("Setting up order photo management test");
        
        // Create repositories
        orderRepository = new InMemoryOrderRepository();
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        
        // Get a production user from the repository
        Optional<User> userOpt = userRepository.findByUsername(new Username("production"));
        assertTrue(userOpt.isPresent(), "Production user should exist in repository");
        productionUser = userOpt.get();
        
        // Create a test order
        OrderId orderId = OrderId.newId();
        OrderNumber orderNumber = new OrderNumber(TEST_ORDER_NUMBER);
        Timestamp createdAt = new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        
        testOrderAggregate = new OrderAggregate(orderId, orderNumber, productionUser, createdAt);
        
        // Add customer and product details
        CustomerAggregate customer = new CustomerAggregate.Builder()
            .withId(CustomerId.newId())
            .withType(CustomerType.COMPANY)
            .withCompany(new Company("Belman Test Customer", "REG-12345", "123 Test Street"))
            .withEmail(new EmailAddress("customer@example.com"))
            .withPhoneNumber(new PhoneNumber("+123456789"))
            .build();
        
        testOrderAggregate.setCustomer(customer);
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
        PhotoAngle angle = new PhotoAngle(PhotoAngle.NamedAngle.FRONT);
        ImagePath imagePath = new ImagePath("/path/to/test/image.jpg");
        Timestamp uploadedAt = new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        
        PhotoDocument photo = new PhotoDocument(photoId, angle, imagePath, productionUser, uploadedAt);
        
        // Add the photo to the order
        testOrderAggregate.addPhoto(photo);
        
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
        assertEquals(angle.getValue(), photos.get(0).getAngle().getValue(), "Photo angle should match");
        assertEquals(imagePath.getValue(), photos.get(0).getImagePath().getValue(), "Image path should match");
        assertEquals(productionUser.getId(), photos.get(0).getUploadedBy().getId(), "Uploader should match");
        
        logDebug("Photo successfully added to order");
    }

    @Test
    @DisplayName("OrderAggregate status can be updated to COMPLETED when photos are added")
    void testUpdateOrderStatusToCompleted() {
        logDebug("Testing updating order status to COMPLETED");
        
        // Create and add a photo document
        PhotoDocument photo = new PhotoDocument(
            PhotoId.newId(),
            new PhotoAngle(PhotoAngle.NamedAngle.FRONT),
            new ImagePath("/path/to/test/image.jpg"),
            productionUser,
            new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC))
        );
        
        testOrderAggregate.addPhoto(photo);
        
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
        PhotoDocument photo1 = new PhotoDocument(
            PhotoId.newId(),
            new PhotoAngle(PhotoAngle.NamedAngle.FRONT),
            new ImagePath("/path/to/test/image1.jpg"),
            productionUser,
            new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC))
        );

        // Side view from the right
        PhotoDocument photo2 = new PhotoDocument(
            PhotoId.newId(),
            new PhotoAngle(PhotoAngle.NamedAngle.RIGHT),
            new ImagePath("/path/to/test/image2.jpg"),
            productionUser,
            new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC))
        );

        // Side view from the left
        PhotoDocument photo3 = new PhotoDocument(
                PhotoId.newId(),
                new PhotoAngle(PhotoAngle.NamedAngle.LEFT),
                new ImagePath("/path/to/test/image2.jpg"),
                productionUser,
                new Timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC))
        );
        
        testOrderAggregate.addPhoto(photo1);
        testOrderAggregate.addPhoto(photo2);
        testOrderAggregate.addPhoto(photo3);


        // Save the updated order
        orderRepository.save(testOrderAggregate);
        
        // Retrieve the order from the repository
        Optional<OrderAggregate> retrievedOrderOpt = orderRepository.findByOrderNumber(new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "OrderAggregate should exist in repository");
        
        OrderAggregate retrievedOrderAggregate = retrievedOrderOpt.get();
        List<PhotoDocument> photos = retrievedOrderAggregate.getPhotos();
        
        // Verify that all photos were retrieved
        assertEquals(2, photos.size(), "OrderAggregate should have two photos");
        assertEquals(PhotoAngle.NamedAngle.FRONT, photos.get(0).getAngle().getValue(), "First photo should be front view");
        assertEquals("Side view", photos.get(1).getAngle().getValue(), "Second photo should be side view");
        
        logDebug("OrderAggregate with photos successfully retrieved by order number");
    }
}