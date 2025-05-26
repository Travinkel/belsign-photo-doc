package com.belman.unit.presentation.usecases.qa.review;

import com.belman.application.usecase.qa.QAService;
import com.belman.common.session.SessionContext;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.photo.PhotoAnnotation;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.qa.review.PhotoReviewViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PhotoReviewViewModel class.
 * These tests verify that the ViewModel correctly handles loading orders,
 * selecting photos, approving/rejecting photos, and other photo review functionality.
 */
public class PhotoReviewViewModelTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private QAService qaService;

    private PhotoReviewViewModel viewModel;
    private OrderBusiness mockOrder;
    private PhotoDocument mockPhoto;
    private UserBusiness mockUser;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Create the view model
        viewModel = new PhotoReviewViewModel();

        // Inject the mocked dependencies using reflection
        injectDependency("orderRepository", orderRepository);
        injectDependency("photoRepository", photoRepository);
        injectDependency("sessionContext", sessionContext);
        injectDependency("qaService", qaService);

        // Create a real QA user with username "qa_user"
        Username username = new Username("qa_user");
        HashedPassword password = new HashedPassword("$2a$10$ReM2gCw1o9rZz/ctET48N.XCmTxSKFcQvwNaqtjCSZxGr78adkX5u"); // Standard test password
        EmailAddress email = new EmailAddress("qa_user@belman.com");

        // Create a real UserBusiness instance
        mockUser = UserBusiness.createNewUser(username, password, email);

        // Mock the session context to return the real user
        when(sessionContext.getUser()).thenReturn(Optional.of(mockUser));

        // Set up mock order
        mockOrder = createMockOrder("ORDER-001", OrderStatus.COMPLETED);
        when(orderRepository.findByOrderNumber(any(OrderNumber.class))).thenReturn(Optional.of(mockOrder));

        // Set up mock photo
        mockPhoto = createMockPhoto(mockOrder.getId());
        List<PhotoDocument> photos = new ArrayList<>();
        photos.add(mockPhoto);
        when(photoRepository.findByOrderId(mockOrder.getId())).thenReturn(photos);

        // Set up mock annotations
        List<PhotoAnnotation> annotations = new ArrayList<>();
        annotations.add(createMockAnnotation(mockPhoto.getId()));
        when(qaService.getAnnotations(mockPhoto.getId())).thenReturn(annotations);
    }

    /**
     * Helper method to inject a dependency into the view model using reflection.
     * 
     * @param fieldName the name of the field to inject
     * @param dependency the dependency to inject
     * @throws Exception if the field doesn't exist or can't be accessed
     */
    private void injectDependency(String fieldName, Object dependency) throws Exception {
        Field field = PhotoReviewViewModel.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(viewModel, dependency);
    }

    /**
     * Test that loadOrder correctly loads an order and its photos.
     */
    @Test
    void testLoadOrder_LoadsOrderAndPhotos() {
        // Arrange
        String orderNumber = "ORDER-001";

        // Act
        viewModel.loadOrder(orderNumber);

        // Assert
        verify(orderRepository).findByOrderNumber(new OrderNumber(orderNumber));
        verify(photoRepository).findByOrderId(mockOrder.getId());

        assertEquals(orderNumber, viewModel.orderNumberProperty().get(),
                "Order number property should be set to the loaded order number");
        assertTrue(viewModel.orderInfoProperty().get().contains(orderNumber),
                "Order info property should contain the order number");
        assertEquals(1, viewModel.getPhotos().size(),
                "Photos list should contain the photos for the order");
    }

    /**
     * Test that loadOrder sets an error message when the order is not found.
     */
    @Test
    void testLoadOrder_SetsErrorMessage_WhenOrderNotFound() {
        // Arrange
        String orderNumber = "NON-EXISTENT";
        when(orderRepository.findByOrderNumber(any(OrderNumber.class))).thenReturn(Optional.empty());

        // Act
        viewModel.loadOrder(orderNumber);

        // Assert
        assertEquals("Order not found: NON-EXISTENT", viewModel.errorMessageProperty().get(),
                "Error message should indicate that the order was not found");
    }

    /**
     * Test that selectPhoto correctly selects a photo and loads its annotations.
     */
    @Test
    void testSelectPhoto_SelectsPhotoAndLoadsAnnotations() throws Exception {
        // Act
        viewModel.selectPhoto(mockPhoto);

        // Assert
        // We need to use reflection to access the private field
        Field selectedPhotoField = PhotoReviewViewModel.class.getDeclaredField("selectedPhoto");
        selectedPhotoField.setAccessible(true);
        PhotoDocument selectedPhoto = (PhotoDocument) selectedPhotoField.get(viewModel);

        assertEquals(mockPhoto, selectedPhoto, "Selected photo should be the mock photo");
        verify(qaService).getAnnotations(mockPhoto.getId());
        assertEquals(1, viewModel.getAnnotations().size(),
                "Annotations list should contain the annotations for the photo");
    }

    /**
     * Test that approveSelectedPhoto correctly approves a photo.
     */
    @Test
    void testApproveSelectedPhoto_ApprovesPhoto() throws Exception {
        // Arrange
        viewModel.selectPhoto(mockPhoto);

        // Act
        boolean result = viewModel.approveSelectedPhoto();

        // Assert
        assertTrue(result, "approveSelectedPhoto should return true on success");

        // Verify that the photo was approved
        ArgumentCaptor<PhotoDocument> photoCaptor = ArgumentCaptor.forClass(PhotoDocument.class);
        verify(photoRepository).save(photoCaptor.capture());

        PhotoDocument savedPhoto = photoCaptor.getValue();
        assertEquals(PhotoDocument.ApprovalStatus.APPROVED, savedPhoto.getStatus(),
                "Photo status should be APPROVED");
    }

    /**
     * Test that approveSelectedPhoto returns false when no photo is selected.
     */
    @Test
    void testApproveSelectedPhoto_ReturnsFalse_WhenNoPhotoSelected() {
        // Act
        boolean result = viewModel.approveSelectedPhoto();

        // Assert
        assertFalse(result, "approveSelectedPhoto should return false when no photo is selected");
        assertEquals("No photo selected", viewModel.errorMessageProperty().get(),
                "Error message should indicate that no photo is selected");
    }

    /**
     * Test that rejectSelectedPhoto correctly rejects a photo.
     */
    @Test
    void testRejectSelectedPhoto_RejectsPhoto() throws Exception {
        // Arrange
        viewModel.selectPhoto(mockPhoto);
        viewModel.commentProperty().set("Rejection reason");

        // Act
        boolean result = viewModel.rejectSelectedPhoto();

        // Assert
        assertTrue(result, "rejectSelectedPhoto should return true on success");

        // Verify that the photo was rejected
        ArgumentCaptor<PhotoDocument> photoCaptor = ArgumentCaptor.forClass(PhotoDocument.class);
        verify(photoRepository).save(photoCaptor.capture());

        PhotoDocument savedPhoto = photoCaptor.getValue();
        assertEquals(PhotoDocument.ApprovalStatus.REJECTED, savedPhoto.getStatus(),
                "Photo status should be REJECTED");
    }

    /**
     * Test that rejectSelectedPhoto returns false when no comment is provided.
     */
    @Test
    void testRejectSelectedPhoto_ReturnsFalse_WhenNoCommentProvided() {
        // Arrange
        viewModel.selectPhoto(mockPhoto);
        viewModel.commentProperty().set("");

        // Act
        boolean result = viewModel.rejectSelectedPhoto();

        // Assert
        assertFalse(result, "rejectSelectedPhoto should return false when no comment is provided");
        assertEquals("A comment is required when rejecting a photo", viewModel.errorMessageProperty().get(),
                "Error message should indicate that a comment is required");
    }

    /**
     * Test that createAnnotation correctly creates an annotation.
     */
    @Test
    void testCreateAnnotation_CreatesAnnotation() {
        // Arrange
        viewModel.selectPhoto(mockPhoto);
        double x = 0.5;
        double y = 0.5;
        String text = "Test annotation";
        PhotoAnnotation mockAnnotation = createMockAnnotation(mockPhoto.getId());
        when(qaService.createAnnotation(eq(mockPhoto.getId()), eq(x), eq(y), eq(text), any())).thenReturn(mockAnnotation);

        // Act
        PhotoAnnotation result = viewModel.createAnnotation(x, y, text);

        // Assert
        assertNotNull(result, "createAnnotation should return the created annotation");
        verify(qaService).createAnnotation(eq(mockPhoto.getId()), eq(x), eq(y), eq(text), any());
        verify(qaService).getAnnotations(mockPhoto.getId());
    }

    /**
     * Test that approveOrder correctly approves an order and its photos.
     */
    @Test
    void testApproveOrder_ApprovesOrderAndPhotos() {
        // Arrange
        try (MockedStatic<Router> mockedRouter = mockStatic(Router.class)) {
            // Set up the current order
            Field currentOrderField = PhotoReviewViewModel.class.getDeclaredField("currentOrder");
            currentOrderField.setAccessible(true);
            currentOrderField.set(viewModel, mockOrder);

            // Act
            viewModel.approveOrder();

            // Assert
            // Verify that the order status was updated
            ArgumentCaptor<OrderBusiness> orderCaptor = ArgumentCaptor.forClass(OrderBusiness.class);
            verify(orderRepository).save(orderCaptor.capture());

            OrderBusiness savedOrder = orderCaptor.getValue();
            assertEquals(OrderStatus.APPROVED, savedOrder.getStatus(),
                    "Order status should be APPROVED");

            // Verify that the photos were approved
            verify(photoRepository).findByOrderId(mockOrder.getId());
            verify(photoRepository).save(any(PhotoDocument.class));

            // Verify that navigation occurred
            mockedRouter.verify(() -> Router.navigateTo(any(Class.class), anyMap()));
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    /**
     * Helper method to create a real order.
     * 
     * @param orderNumber the order number
     * @param status the order status
     * @return a real OrderBusiness instance
     */
    private OrderBusiness createMockOrder(String orderNumber, OrderStatus status) {
        // Create a real OrderId
        OrderId orderId = new OrderId(UUID.randomUUID().toString());

        // Create a real OrderNumber with the correct format (ORD-XX-YYMMDD-ZZZ-NNNN)
        OrderNumber orderNum = new OrderNumber("ORD-XX-230101-ABC-0001");

        // Create a user reference for the creator
        UserId userId = new UserId(UUID.randomUUID().toString());
        Username username = new Username("test_user");
        UserReference createdBy = new UserReference(userId, username);

        // Create a timestamp for creation time
        Timestamp createdAt = new Timestamp(Instant.now());

        // Use the constructor to create a real OrderBusiness instance
        OrderBusiness order = new OrderBusiness(orderId, orderNum, createdBy, createdAt);

        // Set the status
        order.setStatus(status);

        return order;
    }

    /**
     * Helper method to create a mock photo.
     * 
     * @param orderId the order ID
     * @return a mock photo
     */
    private PhotoDocument createMockPhoto(OrderId orderId) {
        PhotoDocument photo = mock(PhotoDocument.class);
        PhotoId photoId = new PhotoId(UUID.randomUUID().toString());
        when(photo.getId()).thenReturn(photoId);
        when(photo.getOrderId()).thenReturn(orderId);
        when(photo.getStatus()).thenReturn(PhotoDocument.ApprovalStatus.PENDING);
        return photo;
    }

    /**
     * Helper method to create a mock annotation.
     * 
     * @param photoId the photo ID
     * @return a mock annotation
     */
    private PhotoAnnotation createMockAnnotation(PhotoId photoId) {
        return new PhotoAnnotation(
                UUID.randomUUID().toString(),
                0.5,
                0.5,
                "Test annotation",
                PhotoAnnotation.AnnotationType.NOTE
        );
    }
}
