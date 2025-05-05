package com.belman.acceptance.order;

import com.belman.acceptance.BaseAcceptanceTest;
import com.belman.domain.aggregates.Order;
import com.belman.domain.aggregates.User;
import com.belman.domain.entities.Customer;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.enums.CustomerType;
import com.belman.domain.enums.OrderStatus;
import com.belman.domain.repositories.OrderRepository;
import com.belman.domain.valueobjects.*;
import com.belman.infrastructure.persistence.InMemoryOrderRepository;
import com.belman.infrastructure.persistence.InMemoryUserRepository;
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
public class OrderPhotoManagementTest extends BaseAcceptanceTest {

    private OrderRepository orderRepository;
    private User productionUser;
    private Order testOrder;
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
        
        testOrder = new Order(orderId, orderNumber, productionUser, createdAt);
        
        // Add customer and product details
        Customer customer = new Customer.Builder()
            .withId(CustomerId.newId())
            .withType(CustomerType.COMPANY)
            .withCompany(new Company("Belman Test Customer", "REG-12345", "123 Test Street"))
            .withEmail(new EmailAddress("customer@example.com"))
            .withPhoneNumber(new PhoneNumber("+123456789"))
            .build();
        
        testOrder.setCustomer(customer);
        testOrder.setProductDescription(new ProductDescription(
            "Test expansion joint",
            "High-quality expansion joint for industrial use",
            "Material: Steel, Size: 100x100"
        ));
        
        // Save the order
        orderRepository.save(testOrder);
        logDebug("Test order created: " + testOrder.getOrderNumber().toString());
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
        testOrder.addPhoto(photo);
        
        // Save the updated order
        orderRepository.save(testOrder);
        
        // Retrieve the order from the repository
        Optional<Order> retrievedOrderOpt = orderRepository.findByOrderNumber(new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "Order should exist in repository");
        
        Order retrievedOrder = retrievedOrderOpt.get();
        List<PhotoDocument> photos = retrievedOrder.getPhotos();
        
        // Verify that the photo was added
        assertEquals(1, photos.size(), "Order should have one photo");
        assertEquals(photoId, photos.get(0).getPhotoId(), "Photo ID should match");
        assertEquals(angle.getValue(), photos.get(0).getAngle().getValue(), "Photo angle should match");
        assertEquals(imagePath.getValue(), photos.get(0).getImagePath().getValue(), "Image path should match");
        assertEquals(productionUser.getId(), photos.get(0).getUploadedBy().getId(), "Uploader should match");
        
        logDebug("Photo successfully added to order");
    }

    @Test
    @DisplayName("Order status can be updated to COMPLETED when photos are added")
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
        
        testOrder.addPhoto(photo);
        
        // Update order status to COMPLETED
        testOrder.setStatus(OrderStatus.COMPLETED);
        
        // Save the updated order
        orderRepository.save(testOrder);
        
        // Retrieve the order from the repository
        Optional<Order> retrievedOrderOpt = orderRepository.findByOrderNumber(new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "Order should exist in repository");
        
        Order retrievedOrder = retrievedOrderOpt.get();
        
        // Verify that the order status was updated
        assertEquals(OrderStatus.COMPLETED, retrievedOrder.getStatus(), "Order status should be COMPLETED");
        assertTrue(retrievedOrder.isReadyForQaReview(), "Order should be ready for QA review");
        
        logDebug("Order status successfully updated to COMPLETED");
    }

    @Test
    @DisplayName("Order with photos can be retrieved by order number")
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
        
        testOrder.addPhoto(photo1);
        testOrder.addPhoto(photo2);
        testOrder.addPhoto(photo3);


        // Save the updated order
        orderRepository.save(testOrder);
        
        // Retrieve the order from the repository
        Optional<Order> retrievedOrderOpt = orderRepository.findByOrderNumber(new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "Order should exist in repository");
        
        Order retrievedOrder = retrievedOrderOpt.get();
        List<PhotoDocument> photos = retrievedOrder.getPhotos();
        
        // Verify that all photos were retrieved
        assertEquals(2, photos.size(), "Order should have two photos");
        assertEquals(PhotoAngle.NamedAngle.FRONT, photos.get(0).getAngle().getValue(), "First photo should be front view");
        assertEquals("Side view", photos.get(1).getAngle().getValue(), "Second photo should be side view");
        
        logDebug("Order with photos successfully retrieved by order number");
    }
}