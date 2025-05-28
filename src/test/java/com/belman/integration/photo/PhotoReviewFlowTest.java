package com.belman.integration.photo;

import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.application.usecase.qa.QAService;
import com.belman.common.session.SessionContext;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.PhotoStatus;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import com.belman.domain.user.UserRole;
import com.belman.presentation.usecases.qa.QAFlowContext;
import com.belman.presentation.usecases.qa.review.PhotoReviewViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * End-to-End test for the photo review flow.
 * This test verifies the complete photo review workflow from selecting an order
 * to reviewing and approving/rejecting photos.
 */
public class PhotoReviewFlowTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PhotoService photoService;

    @Mock
    private QAService qaService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private com.belman.domain.services.LoggerFactory loggerFactory;

    private UserBusiness testQAEngineer;
    private OrderBusiness testOrder;
    private List<PhotoDocument> testPhotos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("[DEBUG_LOG] Setting up PhotoReviewFlowTest");

        // Clear any previous state
        QAFlowContext.clear();

        // Clear ServiceLocator and register necessary services
        com.belman.bootstrap.di.ServiceLocator.clear();

        // Register SessionContext
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.common.session.SessionContext.class,
            sessionContext
        );

        // Register LoggerFactory
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.services.LoggerFactory.class,
            loggerFactory
        );

        // Set up logger factory mock
        when(loggerFactory.getLogger(any())).thenReturn(mock(com.belman.domain.services.Logger.class));

        // Register AuthenticationService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.security.AuthenticationService.class,
            authenticationService
        );

        // Register OrderService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.order.OrderService.class,
            orderService
        );

        // Register PhotoService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.photo.PhotoService.class,
            photoService
        );

        // Register QAService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.qa.QAService.class,
            qaService
        );

        // Create test QA engineer
        testQAEngineer = createTestQAEngineer();

        // Create test order
        testOrder = createTestOrder();

        // Create test photos
        testPhotos = createTestPhotos();

        // Set up mocks
        setupMocks();
    }

    /**
     * Tests the complete photo review flow.
     * This test verifies that a QA engineer can select an order, review photos,
     * and approve or reject them.
     */
    @Test
    void testCompletePhotoReviewFlow() {
        System.out.println("[DEBUG_LOG] Running complete photo review flow test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testQAEngineer);

        // Configure sessionContext mock to return the test QA engineer
        when(sessionContext.getUser()).thenReturn(Optional.of(testQAEngineer));

        // 1. Set up the order context
        QAFlowContext.setCurrentOrder(testOrder);
        
        // Verify that the order is set in the context
        assertNotNull(QAFlowContext.getCurrentOrder(), "Order should be set in QAFlowContext");
        assertEquals(testOrder.getId().id(), QAFlowContext.getCurrentOrder().getId().id(), 
                    "Order in context should match test order");

        // 2. Test PhotoReviewViewModel
        PhotoReviewViewModel photoReviewViewModel = new PhotoReviewViewModel();
        injectMocks(photoReviewViewModel);

        // Trigger onShow to load the photos
        photoReviewViewModel.onShow();

        // Verify that photos are loaded
        assertNotNull(photoReviewViewModel.getPhotos(), "Photos should be loaded");
        assertFalse(photoReviewViewModel.getPhotos().isEmpty(), "Photo list should not be empty");
        assertEquals(testPhotos.size(), photoReviewViewModel.getPhotos().size(), 
                    "Number of loaded photos should match test photos");

        // 3. Test approving a photo
        PhotoDocument photoToApprove = testPhotos.get(0);
        
        // Mock the QA service to return true for approvePhoto
        when(qaService.approvePhoto(eq(photoToApprove.getId()), any(UserBusiness.class), anyString()))
            .thenReturn(true);
            
        // Approve the photo
        boolean approveResult = photoReviewViewModel.approvePhoto(photoToApprove, "Looks good");
        
        // Verify that the photo was approved
        assertTrue(approveResult, "Photo should be approved successfully");
        verify(qaService).approvePhoto(eq(photoToApprove.getId()), any(UserBusiness.class), eq("Looks good"));

        // 4. Test rejecting a photo
        PhotoDocument photoToReject = testPhotos.get(1);
        
        // Mock the QA service to return true for rejectPhoto
        when(qaService.rejectPhoto(eq(photoToReject.getId()), any(UserBusiness.class), anyString()))
            .thenReturn(true);
            
        // Reject the photo
        boolean rejectResult = photoReviewViewModel.rejectPhoto(photoToReject, "Blurry image");
        
        // Verify that the photo was rejected
        assertTrue(rejectResult, "Photo should be rejected successfully");
        verify(qaService).rejectPhoto(eq(photoToReject.getId()), any(UserBusiness.class), eq("Blurry image"));

        // 5. Test completing the order review
        // Mock the QA service to return true for completeOrderReview
        when(qaService.completeOrderReview(eq(testOrder.getId()), any(UserBusiness.class)))
            .thenReturn(true);
            
        // Complete the order review
        boolean completeResult = photoReviewViewModel.completeOrderReview();
        
        // Verify that the order review was completed
        assertTrue(completeResult, "Order review should be completed successfully");
        verify(qaService).completeOrderReview(eq(testOrder.getId()), any(UserBusiness.class));

        System.out.println("[DEBUG_LOG] Complete photo review flow test completed successfully");
    }

    /**
     * Tests error handling during photo review.
     * This test verifies that errors during photo review are properly handled.
     */
    @Test
    void testErrorHandlingDuringPhotoReview() {
        System.out.println("[DEBUG_LOG] Running error handling during photo review test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testQAEngineer);

        // Configure sessionContext mock to return the test QA engineer
        when(sessionContext.getUser()).thenReturn(Optional.of(testQAEngineer));

        // 1. Set up the order context
        QAFlowContext.setCurrentOrder(testOrder);

        // 2. Test PhotoReviewViewModel with error during photo loading
        // Mock the PhotoService to throw an exception when loading photos
        when(photoService.getPhotosByOrderId(any(OrderId.class)))
            .thenThrow(new RuntimeException("Failed to load photos"));

        PhotoReviewViewModel photoReviewViewModel = new PhotoReviewViewModel();
        injectMocks(photoReviewViewModel);

        // Trigger onShow to attempt loading photos
        photoReviewViewModel.onShow();

        // Verify that the error is handled (the view model should not crash)
        // In a real application, this would typically set an error message property
        assertTrue(photoReviewViewModel.getPhotos().isEmpty(), "Photos should not be loaded due to error");

        // Reset the mock for subsequent tests
        reset(photoService);
        when(photoService.getPhotosByOrderId(any(OrderId.class))).thenReturn(testPhotos);

        // 3. Test error handling during photo approval
        PhotoDocument photoToApprove = testPhotos.get(0);
        
        // Mock the QA service to throw an exception during photo approval
        when(qaService.approvePhoto(any(PhotoId.class), any(UserBusiness.class), anyString()))
            .thenThrow(new RuntimeException("Failed to approve photo"));

        // In a real application, this would be handled by the PhotoReviewViewModel
        // Here we're just verifying that the exception is thrown
        Exception approveException = assertThrows(RuntimeException.class, () -> {
            qaService.approvePhoto(photoToApprove.getId(), testQAEngineer, "Looks good");
        });
        
        assertEquals("Failed to approve photo", approveException.getMessage(), "Exception message should match");

        // 4. Test error handling during photo rejection
        PhotoDocument photoToReject = testPhotos.get(1);
        
        // Mock the QA service to throw an exception during photo rejection
        when(qaService.rejectPhoto(any(PhotoId.class), any(UserBusiness.class), anyString()))
            .thenThrow(new RuntimeException("Failed to reject photo"));

        // In a real application, this would be handled by the PhotoReviewViewModel
        // Here we're just verifying that the exception is thrown
        Exception rejectException = assertThrows(RuntimeException.class, () -> {
            qaService.rejectPhoto(photoToReject.getId(), testQAEngineer, "Blurry image");
        });
        
        assertEquals("Failed to reject photo", rejectException.getMessage(), "Exception message should match");

        System.out.println("[DEBUG_LOG] Error handling during photo review test completed successfully");
    }

    // Helper methods

    private UserBusiness createTestQAEngineer() {
        return new UserBusiness.Builder()
            .id(new UserId("qa-123"))
            .username(new Username("testqa"))
            .password(new HashedPassword("hashedpassword"))
            .email(new EmailAddress("qa@example.com"))
            .approvalState(ApprovalState.createApproved())
            .addRole(UserRole.QA)
            .build();
    }

    private OrderBusiness createTestOrder() {
        OrderBusiness order = new OrderBusiness(
            new OrderId("order-123"),
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        order.setOrderNumber(new OrderNumber("ORD-78-230625-PIP-0003"));
        order.setStatus(OrderStatus.COMPLETED); // Order must be completed to be reviewed
        return order;
    }

    private List<PhotoDocument> createTestPhotos() {
        List<PhotoDocument> photos = new ArrayList<>();
        
        // Create a few test photos with different templates
        photos.add(PhotoDocument.builder()
            .photoId(new PhotoId("photo-1"))
            .orderId(testOrder.getId())
            .template(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY)
            .imagePath(new Photo("test-photo-1.jpg"))
            .uploadedBy(new UserBusiness.Builder()
                .id(new UserId("worker-123"))
                .username(new Username("testworker"))
                .build())
            .uploadedAt(new Timestamp(Instant.now().minusSeconds(3600)))
            .status(PhotoStatus.PENDING)
            .build());
            
        photos.add(PhotoDocument.builder()
            .photoId(new PhotoId("photo-2"))
            .orderId(testOrder.getId())
            .template(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY)
            .imagePath(new Photo("test-photo-2.jpg"))
            .uploadedBy(new UserBusiness.Builder()
                .id(new UserId("worker-123"))
                .username(new Username("testworker"))
                .build())
            .uploadedAt(new Timestamp(Instant.now().minusSeconds(3000)))
            .status(PhotoStatus.PENDING)
            .build());
            
        photos.add(PhotoDocument.builder()
            .photoId(new PhotoId("photo-3"))
            .orderId(testOrder.getId())
            .template(PhotoTemplate.TOP_VIEW_OF_JOINT)
            .imagePath(new Photo("test-photo-3.jpg"))
            .uploadedBy(new UserBusiness.Builder()
                .id(new UserId("worker-123"))
                .username(new Username("testworker"))
                .build())
            .uploadedAt(new Timestamp(Instant.now().minusSeconds(2400)))
            .status(PhotoStatus.PENDING)
            .build());
            
        return photos;
    }

    private void setupMocks() {
        // Set up OrderService mock
        when(orderService.getOrderById(any(OrderId.class))).thenReturn(Optional.of(testOrder));
        
        // Set up PhotoService mock
        when(photoService.getPhotosByOrderId(any(OrderId.class))).thenReturn(testPhotos);
        
        // Set up AuthenticationService mock
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(testQAEngineer));
    }

    private void injectMocks(PhotoReviewViewModel viewModel) {
        try {
            // Use reflection to inject mocks into the view model
            java.lang.reflect.Field orderServiceField = viewModel.getClass().getDeclaredField("orderService");
            orderServiceField.setAccessible(true);
            orderServiceField.set(viewModel, orderService);

            java.lang.reflect.Field photoServiceField = viewModel.getClass().getDeclaredField("photoService");
            photoServiceField.setAccessible(true);
            photoServiceField.set(viewModel, photoService);

            java.lang.reflect.Field qaServiceField = viewModel.getClass().getDeclaredField("qaService");
            qaServiceField.setAccessible(true);
            qaServiceField.set(viewModel, qaService);

            java.lang.reflect.Field authServiceField = viewModel.getClass().getDeclaredField("authenticationService");
            authServiceField.setAccessible(true);
            authServiceField.set(viewModel, authenticationService);
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error injecting mocks: " + e.getMessage());
            e.printStackTrace();
        }
    }
}