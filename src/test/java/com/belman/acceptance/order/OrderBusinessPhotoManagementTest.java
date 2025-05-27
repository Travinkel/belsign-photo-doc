package com.belman.acceptance.order;

import com.belman.acceptance.BaseAcceptanceTest;
import com.belman.dataaccess.repository.memory.InMemoryPhotoRepository;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PhoneNumber;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.customer.Company;
import com.belman.domain.customer.CustomerBusiness;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.customer.CustomerType;
import com.belman.domain.order.*;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import com.belman.dataaccess.persistence.memory.InMemoryOrderRepository;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Acceptance tests for order photo management.
 * Verifies that users can create orders and add photos to them.
 */
public class OrderBusinessPhotoManagementTest extends BaseAcceptanceTest {

    private static final String TEST_ORDER_NUMBER = "ORD-78-230625-ABC-0001";
    private OrderRepository orderRepository;
    private PhotoRepository photoRepository;
    private UserBusiness productionUser;
    private OrderBusiness testOrderBusiness;

    @BeforeEach
    void setup() {
        logDebug("Setting up order photo management test");

        // Create repositories
        orderRepository = new InMemoryOrderRepository();
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        // Create a mock LoggerFactory for the PhotoRepository
        LoggerFactory loggerFactory = new LoggerFactory() {
            @Override
            public com.belman.domain.services.Logger getLogger(Class<?> clazz) {
                return new com.belman.domain.services.Logger() {
                    @Override
                    public void debug(String message) {}
                    @Override
                    public void info(String message) {}
                    @Override
                    public void warn(String message) {}
                    @Override
                    public void error(String message) {}
                    @Override
                    public void error(String message, Throwable throwable) {}
                    @Override
                    public void debug(String message, Object... args) {}
                    @Override
                    public void info(String message, Object... args) {}
                    @Override
                    public void warn(String message, Object... args) {}
                    @Override
                    public void error(String message, Object... args) {}
                    @Override
                    public void trace(String message) {}
                    @Override
                    public void trace(String message, Object... args) {}
                };
            }
        };
        photoRepository = new InMemoryPhotoRepository(loggerFactory);

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
        CustomerBusiness customer = new CustomerBusiness.Builder()
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

        PhotoDocument photoDoc = PhotoDocument.builder()
                .photoId(photoId)
                .template(template)
                .imagePath(photo)
                .uploadedBy(productionUser)
                .uploadedAt(uploadedAt)
                .orderId(testOrderBusiness.getId())
                .build();

        // Save the photo document to the repository
        photoRepository.save(photoDoc);

        // Add the photo ID to the order
        testOrderBusiness.addPhotoId(photoId);

        // Save the updated order
        orderRepository.save(testOrderBusiness);

        // Retrieve the order from the repository
        Optional<OrderBusiness> retrievedOrderOpt = orderRepository.findByOrderNumber(
                new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "OrderBusiness should exist in repository");

        OrderBusiness retrievedOrderBusiness = retrievedOrderOpt.get();
        List<PhotoId> photoIds = retrievedOrderBusiness.getPhotoIds();

        // Verify that the photo ID was added to the order
        assertEquals(1, photoIds.size(), "OrderBusiness should have one photo ID");
        assertEquals(photoId, photoIds.get(0), "Photo ID should match");

        // Retrieve the photo from the repository
        Optional<PhotoDocument> retrievedPhotoOpt = photoRepository.findById(photoId);
        assertTrue(retrievedPhotoOpt.isPresent(), "Photo should exist in repository");

        PhotoDocument retrievedPhoto = retrievedPhotoOpt.get();
        assertEquals(template, retrievedPhoto.getTemplate(), "Photo template should match");
        assertEquals(photo, retrievedPhoto.getImagePath(), "Image path should match");
        assertEquals(productionUser, retrievedPhoto.getUploadedBy(), "Uploader should match");

        logDebug("Photo successfully added to order");
    }

    @Test
    @DisplayName("OrderBusiness status can be updated to COMPLETED when photos are added")
    void testUpdateOrderStatusToCompleted() {
        logDebug("Testing updating order status to COMPLETED");

        // Create a photo document
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
                .orderId(testOrderBusiness.getId())
                .build();

        // Save the photo document to the repository
        photoRepository.save(photoDoc);

        // Add the photo ID to the order
        testOrderBusiness.addPhotoId(photoId);

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
                .orderId(testOrderBusiness.getId())
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
                .orderId(testOrderBusiness.getId())
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
                .orderId(testOrderBusiness.getId())
                .build();

        // Save the photo documents to the repository
        photoRepository.save(photoDoc1);
        photoRepository.save(photoDoc2);
        photoRepository.save(photoDoc3);

        // Add the photo IDs to the order
        testOrderBusiness.addPhotoId(photoId1);
        testOrderBusiness.addPhotoId(photoId2);
        testOrderBusiness.addPhotoId(photoId3);

        // Save the updated order
        orderRepository.save(testOrderBusiness);

        // Retrieve the order from the repository
        Optional<OrderBusiness> retrievedOrderOpt = orderRepository.findByOrderNumber(
                new OrderNumber(TEST_ORDER_NUMBER));
        assertTrue(retrievedOrderOpt.isPresent(), "OrderBusiness should exist in repository");

        OrderBusiness retrievedOrderBusiness = retrievedOrderOpt.get();
        List<PhotoId> photoIds = retrievedOrderBusiness.getPhotoIds();

        // Verify that all photo IDs were retrieved
        assertEquals(3, photoIds.size(), "OrderBusiness should have three photo IDs");

        // Retrieve the photos from the repository
        List<PhotoDocument> photos = new ArrayList<>();
        for (PhotoId photoId : photoIds) {
            Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
            assertTrue(photoOpt.isPresent(), "Photo should exist in repository");
            photos.add(photoOpt.get());
        }

        // Verify that all photos were retrieved with the correct templates
        assertEquals(3, photos.size(), "Should have retrieved three photos");

        // Find the photo with template1
        Optional<PhotoDocument> frontViewPhoto = photos.stream()
                .filter(p -> p.getTemplate() == template1)
                .findFirst();
        assertTrue(frontViewPhoto.isPresent(), "Front view photo should exist");

        // Find the photo with template2
        Optional<PhotoDocument> rightViewPhoto = photos.stream()
                .filter(p -> p.getTemplate() == template2)
                .findFirst();
        assertTrue(rightViewPhoto.isPresent(), "Right view photo should exist");

        // Find the photo with template3
        Optional<PhotoDocument> leftViewPhoto = photos.stream()
                .filter(p -> p.getTemplate() == template3)
                .findFirst();
        assertTrue(leftViewPhoto.isPresent(), "Left view photo should exist");

        logDebug("OrderBusiness with photos successfully retrieved by order number");
    }
}
