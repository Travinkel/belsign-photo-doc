package com.belman.integration.worker;

import com.belman.application.usecase.order.OrderProgressService;
import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.application.usecase.worker.WorkerService;
import com.belman.common.session.SessionContext;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import com.belman.domain.user.UserRole;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.assignedorder.AssignedOrderViewModel;
import com.belman.presentation.usecases.worker.capture.CaptureViewModel;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewModel;
import com.belman.presentation.usecases.worker.photocube.managers.OrderManager;
import com.belman.presentation.usecases.worker.photocube.managers.PhotoCaptureManager;
import com.belman.presentation.usecases.worker.photocube.managers.TemplateManager;
import com.belman.presentation.usecases.worker.summary.SummaryViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.spy;

/**
 * Integration tests for the worker flow.
 * These tests verify the interaction between different components in the worker flow,
 * including state management across views and error handling.
 */
public class WorkerFlowIntegrationTest {

    @Mock
    private OrderService orderService;

    @Mock
    private WorkerService workerService;

    @Mock
    private PhotoService photoService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private OrderProgressService orderProgressService;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private com.belman.domain.services.LoggerFactory loggerFactory;

    // Using real instances instead of mocks to avoid Mockito issues with inlined mocks
    private OrderManager orderManager;
    private TemplateManager templateManager;
    private PhotoCaptureManager photoCaptureManager;

    private UserBusiness testWorker;
    private OrderBusiness testOrder;
    private List<PhotoTemplate> testTemplates;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("[DEBUG_LOG] Setting up WorkerFlowIntegrationTest");

        // Clear any previous state
        WorkerFlowContext.clear();

        // Clear ServiceLocator and register necessary services
        com.belman.bootstrap.di.ServiceLocator.clear();

        // Register SessionContext
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.common.session.SessionContext.class,
            sessionContext
        );

        // Register a mock CameraService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.photo.CameraService.class,
            mock(com.belman.application.usecase.photo.CameraService.class)
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

        // Register WorkerService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.worker.WorkerService.class,
            workerService
        );

        // Register PhotoService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.photo.PhotoService.class,
            photoService
        );

        // Create test worker
        testWorker = createTestWorker();

        // Create test order
        testOrder = createTestOrder();

        // Create test templates
        testTemplates = createTestTemplates();

        // Initialize managers as real instances
        orderManager = new OrderManager();
        templateManager = new TemplateManager();
        photoCaptureManager = new PhotoCaptureManager();

        // Inject dependencies into OrderManager using reflection
        try {
            java.lang.reflect.Field orderServiceField = OrderManager.class.getDeclaredField("orderService");
            orderServiceField.setAccessible(true);
            orderServiceField.set(orderManager, orderService);

            java.lang.reflect.Field orderProgressServiceField = OrderManager.class.getDeclaredField("orderProgressService");
            orderProgressServiceField.setAccessible(true);
            orderProgressServiceField.set(orderManager, orderProgressService);
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error injecting dependencies into OrderManager: " + e.getMessage());
            e.printStackTrace();
        }

        // Inject dependencies into TemplateManager using reflection
        try {
            java.lang.reflect.Field photoTemplateServiceField = TemplateManager.class.getDeclaredField("photoTemplateService");
            photoTemplateServiceField.setAccessible(true);
            photoTemplateServiceField.set(templateManager, mock(com.belman.application.usecase.photo.PhotoTemplateService.class));
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error injecting dependencies into TemplateManager: " + e.getMessage());
            e.printStackTrace();
        }

        // Inject dependencies into PhotoCaptureManager using reflection
        try {
            java.lang.reflect.Field photoCaptureServiceField = PhotoCaptureManager.class.getDeclaredField("photoCaptureService");
            photoCaptureServiceField.setAccessible(true);
            photoCaptureServiceField.set(photoCaptureManager, mock(com.belman.application.usecase.photo.PhotoCaptureService.class));
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error injecting dependencies into PhotoCaptureManager: " + e.getMessage());
            e.printStackTrace();
        }

        // Register OrderManager
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.presentation.usecases.worker.photocube.managers.OrderManager.class,
            orderManager
        );

        // Register TemplateManager
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.presentation.usecases.worker.photocube.managers.TemplateManager.class,
            templateManager
        );

        // Register PhotoCaptureManager
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.presentation.usecases.worker.photocube.managers.PhotoCaptureManager.class,
            photoCaptureManager
        );

        // Set up mocks
        setupMocks();
    }

    /**
     * Tests the full worker flow from assigned order to photo capture.
     * This test verifies that state is properly maintained across views.
     */
    @Test
    void testFullWorkerFlow() {
        System.out.println("[DEBUG_LOG] Running full worker flow test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testWorker);

        // Configure sessionContext mock to return the test worker
        when(sessionContext.getUser()).thenReturn(Optional.of(testWorker));

        // 1. Test AssignedOrderViewModel
        AssignedOrderViewModel assignedOrderViewModel = new AssignedOrderViewModel();
        injectMocks(assignedOrderViewModel);

        // Trigger onShow to load the assigned order
        assignedOrderViewModel.onShow();

        // Verify that the order was loaded
        assertNotNull(assignedOrderViewModel.getCurrentOrder(), "Order should be loaded");
        assertEquals(testOrder.getId().id(), assignedOrderViewModel.getCurrentOrder().getId().id(), 
                    "Loaded order should match test order");

        // Start the photo process
        assignedOrderViewModel.startPhotoProcess();

        // Verify that the order was stored in the WorkerFlowContext
        assertNotNull(WorkerFlowContext.getCurrentOrder(), "Order should be stored in WorkerFlowContext");
        assertEquals(testOrder.getId().id(), WorkerFlowContext.getCurrentOrder().getId().id(), 
                    "Stored order should match test order");

        // 2. Test PhotoCubeViewModel
        PhotoCubeViewModel photoCubeViewModel = new PhotoCubeViewModel();
        injectManagerMocks(photoCubeViewModel);

        // Set up the OrderManager mock to return the test order
        when(orderManager.getCurrentOrder()).thenReturn(testOrder);
        when(orderManager.getCurrentOrderId()).thenReturn(testOrder.getId());
        when(templateManager.getRequiredTemplates()).thenReturn(testTemplates);

        // Trigger onShow to load the templates
        photoCubeViewModel.onShow();

        // Verify that templates can be accessed
        assertNotNull(photoCubeViewModel.getRequiredTemplates(), "Templates should be accessible");

        // Select a template
        PhotoTemplate selectedTemplate = testTemplates.get(0);
        photoCubeViewModel.selectTemplate(selectedTemplate);

        // Verify that the template was stored in the WorkerFlowContext
        assertNotNull(WorkerFlowContext.getSelectedTemplate(), "Template should be stored in WorkerFlowContext");
        assertEquals(selectedTemplate.name(), WorkerFlowContext.getSelectedTemplate().name(), 
                    "Stored template should match selected template");

        // 3. Test CaptureViewModel
        CaptureViewModel captureViewModel = new CaptureViewModel();
        injectMocks(captureViewModel);

        // Trigger onShow to load the capture view
        captureViewModel.onShow();

        // Verify that the template is available in the capture view
        assertNotNull(captureViewModel.currentTemplateProperty().get(), "Template should be available in capture view");
        assertEquals(selectedTemplate.name(), captureViewModel.currentTemplateProperty().get().name(), 
                    "Template in capture view should match selected template");

        // Simulate taking a photo by selecting a mock image
        File mockImageFile = new File("test-photo.jpg");
        captureViewModel.selectMockImage(mockImageFile);

        // Simulate confirming the photo
        // We need to mock the photoService.uploadPhoto method to return a PhotoDocument
        PhotoDocument photo = createTestPhoto(selectedTemplate);
        when(photoService.uploadPhoto(any(OrderId.class), any(Photo.class), any(UserBusiness.class)))
            .thenReturn(photo);

        captureViewModel.confirmPhoto();

        // Verify that the photo was stored in the WorkerFlowContext
        assertEquals(1, WorkerFlowContext.getTakenPhotos().size(), "Photo should be stored in WorkerFlowContext");

        // 4. Test SummaryViewModel
        SummaryViewModel summaryViewModel = new SummaryViewModel();
        injectMocks(summaryViewModel);

        // Set up the WorkerService mock to return true for hasAllRequiredPhotos
        when(workerService.hasAllRequiredPhotos(any(OrderId.class))).thenReturn(true);
        when(workerService.getMissingRequiredTemplates(any(OrderId.class))).thenReturn(Collections.emptyList());

        // Trigger onShow to load the summary view
        summaryViewModel.onShow();

        // Verify that the photos are available in the summary view
        assertFalse(summaryViewModel.takenPhotosProperty().isEmpty(), "Photos should be available in summary view");

        // Complete the flow by submitting photos
        when(orderService.completeOrder(any(OrderId.class), any(UserBusiness.class))).thenReturn(true);
        summaryViewModel.submitPhotos();

        // Verify that the order was completed
        verify(orderService).completeOrder(any(OrderId.class), any(UserBusiness.class));

        System.out.println("[DEBUG_LOG] Full worker flow test completed successfully");
    }

    /**
     * Tests error handling in the worker flow.
     * This test verifies that errors are properly handled and reported.
     */
    @Test
    void testErrorHandlingInWorkerFlow() {
        System.out.println("[DEBUG_LOG] Running error handling test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testWorker);

        // Configure sessionContext mock to return the test worker
        when(sessionContext.getUser()).thenReturn(Optional.of(testWorker));

        // 1. Test error handling in AssignedOrderViewModel
        // Configure orderService to throw an exception
        when(orderService.getAllOrders()).thenThrow(new RuntimeException("Database connection error"));

        AssignedOrderViewModel assignedOrderViewModel = new AssignedOrderViewModel();
        injectMocks(assignedOrderViewModel);

        // Trigger onShow to load the assigned order
        assignedOrderViewModel.onShow();

        // Verify that the error was handled
        assertTrue(assignedOrderViewModel.errorMessageProperty().get().contains("session"), 
                  "Error message should mention session expiry");
        assertNull(assignedOrderViewModel.getCurrentOrder(), "Order should not be loaded due to error");

        // Reset mock to normal behavior for subsequent tests
        reset(orderService);
        setupOrderServiceMock();

        // 2. Test error handling when no user is logged in
        SessionContext.clear();

        AssignedOrderViewModel noUserViewModel = new AssignedOrderViewModel();
        injectMocks(noUserViewModel);

        // Trigger onShow to load the assigned order
        noUserViewModel.onShow();

        // Verify that the error was handled
        assertTrue(noUserViewModel.errorMessageProperty().get().contains("session"), 
                  "Error message should mention session expiry");
        assertNull(noUserViewModel.getCurrentOrder(), "Order should not be loaded when no user is logged in");

        // Restore session for subsequent tests
        SessionContext.setCurrentUser(testWorker);

        System.out.println("[DEBUG_LOG] Error handling test completed successfully");
    }

    /**
     * Tests state management across views.
     * This test verifies that state is properly maintained when navigating between views.
     */
    @Test
    void testStateManagementAcrossViews() {
        System.out.println("[DEBUG_LOG] Running state management test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testWorker);

        // Store an order in the WorkerFlowContext
        WorkerFlowContext.setCurrentOrder(testOrder);

        // Store a template in the WorkerFlowContext
        PhotoTemplate template = testTemplates.get(0);
        WorkerFlowContext.setSelectedTemplate(template);

        // Store a photo in the WorkerFlowContext
        PhotoDocument photo = createTestPhoto(template);
        WorkerFlowContext.addTakenPhoto(photo);

        // 1. Test that PhotoCubeViewModel can access the order through OrderManager
        PhotoCubeViewModel photoCubeViewModel = new PhotoCubeViewModel();
        injectManagerMocks(photoCubeViewModel);

        // Set up the OrderManager mock to return the test order
        when(orderManager.getCurrentOrder()).thenReturn(testOrder);
        when(orderManager.getCurrentOrderId()).thenReturn(testOrder.getId());

        photoCubeViewModel.onShow();

        // Verify the order is accessible through the OrderManager
        verify(orderManager).loadCurrentOrder();

        // 2. Test that CaptureViewModel can access the template
        CaptureViewModel captureViewModel = new CaptureViewModel();
        injectMocks(captureViewModel);

        captureViewModel.onShow();

        // Verify that the template is available in the capture view
        assertNotNull(captureViewModel.currentTemplateProperty().get(), "CaptureViewModel should access template from WorkerFlowContext");
        assertEquals(template.name(), captureViewModel.currentTemplateProperty().get().name(), 
                    "Template in CaptureViewModel should match stored template");

        // 3. Test that SummaryViewModel can access the photos
        SummaryViewModel summaryViewModel = new SummaryViewModel();
        injectMocks(summaryViewModel);

        summaryViewModel.onShow();

        // Verify that the photos are available in the summary view
        assertFalse(summaryViewModel.takenPhotosProperty().isEmpty(), "SummaryViewModel should access photos from WorkerFlowContext");

        // 4. Test that clearing the context removes all state
        WorkerFlowContext.clear();

        assertNull(WorkerFlowContext.getCurrentOrder(), "Order should be cleared");
        assertNull(WorkerFlowContext.getSelectedTemplate(), "Template should be cleared");
        assertTrue(WorkerFlowContext.getTakenPhotos().isEmpty(), "Photos should be cleared");

        System.out.println("[DEBUG_LOG] State management test completed successfully");
    }

    // Helper methods

    private UserBusiness createTestWorker() {
        return new UserBusiness.Builder()
            .id(new UserId("worker-123"))
            .username(new Username("testworker"))
            .password(new HashedPassword("hashedpassword"))
            .email(new EmailAddress("worker@example.com"))
            .approvalState(ApprovalState.createApproved())
            .addRole(UserRole.PRODUCTION)
            .build();
    }

    private OrderBusiness createTestOrder() {
        OrderBusiness order = new OrderBusiness(
            new OrderId("order-123"),
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        order.setOrderNumber(new OrderNumber("ORD-78-230625-PIP-0003"));
        order.setAssignedTo(new UserReference(testWorker.getId(), testWorker.getUsername()));
        return order;
    }

    private List<PhotoTemplate> createTestTemplates() {
        return Arrays.asList(
            PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
            PhotoTemplate.BACK_VIEW_OF_ASSEMBLY,
            PhotoTemplate.TOP_VIEW_OF_JOINT,
            PhotoTemplate.SIDE_VIEW_OF_WELD
        );
    }

    private PhotoDocument createTestPhoto(PhotoTemplate template) {
        // Use the static builder() method to create a builder
        return PhotoDocument.builder()
               .photoId(new PhotoId("photo-123"))
               .orderId(testOrder.getId())
               .template(template)
               .imagePath(new Photo("test-photo.jpg"))
               .uploadedBy(testWorker)
               .uploadedAt(new Timestamp(Instant.now()))
               .build();
    }

    private void setupMocks() {
        setupOrderServiceMock();
        setupWorkerServiceMock();
        setupPhotoServiceMock();
        setupAuthenticationServiceMock();
    }

    private void setupOrderServiceMock() {
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(testOrder));
        when(orderService.getOrderById(any(OrderId.class))).thenReturn(Optional.of(testOrder));
    }

    private void setupWorkerServiceMock() {
        when(workerService.getAvailableTemplates(any(OrderId.class))).thenReturn(testTemplates);
    }

    private void setupPhotoServiceMock() {
        // This method is not needed as we're not using savePhotos anymore
    }

    private void setupAuthenticationServiceMock() {
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(testWorker));
    }

    private void injectMocks(Object viewModel) {
        try {
            // Use reflection to inject mocks into the view model
            if (viewModel.getClass().getName().contains("AssignedOrder") || 
                viewModel.getClass().getName().contains("Summary")) {
                java.lang.reflect.Field orderServiceField = viewModel.getClass().getDeclaredField("orderService");
                orderServiceField.setAccessible(true);
                orderServiceField.set(viewModel, orderService);
            }

            if (viewModel.getClass().getName().contains("AssignedOrder") || 
                viewModel.getClass().getName().contains("Summary")) {
                java.lang.reflect.Field workerServiceField = viewModel.getClass().getDeclaredField("workerService");
                workerServiceField.setAccessible(true);
                workerServiceField.set(viewModel, workerService);
            }

            if (viewModel.getClass().getName().contains("Capture")) {
                java.lang.reflect.Field photoServiceField = viewModel.getClass().getDeclaredField("photoService");
                photoServiceField.setAccessible(true);
                photoServiceField.set(viewModel, photoService);
            }

            if (viewModel.getClass().getName().contains("AssignedOrder")) {
                java.lang.reflect.Field authServiceField = viewModel.getClass().getDeclaredField("authenticationService");
                authServiceField.setAccessible(true);
                authServiceField.set(viewModel, authenticationService);
            }
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error injecting mocks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void injectManagerMocks(PhotoCubeViewModel viewModel) {
        try {
            // Inject the manager mocks into the PhotoCubeViewModel
            java.lang.reflect.Field orderManagerField = PhotoCubeViewModel.class.getDeclaredField("orderManager");
            orderManagerField.setAccessible(true);
            orderManagerField.set(viewModel, orderManager);

            java.lang.reflect.Field templateManagerField = PhotoCubeViewModel.class.getDeclaredField("templateManager");
            templateManagerField.setAccessible(true);
            templateManagerField.set(viewModel, templateManager);

            java.lang.reflect.Field photoCaptureManagerField = PhotoCubeViewModel.class.getDeclaredField("photoCaptureManager");
            photoCaptureManagerField.setAccessible(true);
            photoCaptureManagerField.set(viewModel, photoCaptureManager);

            // Also inject the photoService
            java.lang.reflect.Field photoServiceField = PhotoCubeViewModel.class.getDeclaredField("photoService");
            photoServiceField.setAccessible(true);
            photoServiceField.set(viewModel, photoService);
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error injecting manager mocks: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
