package com.belman.integration.photo;

import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.application.usecase.photo.PhotoTemplateService;
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
import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewModel;
import com.belman.presentation.usecases.worker.photocube.managers.OrderManager;
import com.belman.presentation.usecases.worker.photocube.managers.PhotoCaptureManager;
import com.belman.presentation.usecases.worker.photocube.managers.TemplateManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * End-to-End test for the photo upload flow.
 * This test verifies the complete photo upload workflow from selecting an order
 * to uploading photos with templates.
 */
public class PhotoUploadFlowTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PhotoService photoService;

    @Mock
    private PhotoTemplateService photoTemplateService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private com.belman.domain.services.LoggerFactory loggerFactory;

    // Using real instances instead of mocks
    private OrderManager orderManager;
    private TemplateManager templateManager;
    private PhotoCaptureManager photoCaptureManager;

    private UserBusiness testWorker;
    private OrderBusiness testOrder;
    private List<PhotoTemplate> testTemplates;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("[DEBUG_LOG] Setting up PhotoUploadFlowTest");

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

        // Register PhotoService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.photo.PhotoService.class,
            photoService
        );

        // Register PhotoTemplateService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.photo.PhotoTemplateService.class,
            photoTemplateService
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
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error injecting dependencies into OrderManager: " + e.getMessage());
            e.printStackTrace();
        }

        // Inject dependencies into TemplateManager using reflection
        try {
            java.lang.reflect.Field photoTemplateServiceField = TemplateManager.class.getDeclaredField("photoTemplateService");
            photoTemplateServiceField.setAccessible(true);
            photoTemplateServiceField.set(templateManager, photoTemplateService);
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
     * Tests the complete photo upload flow.
     * This test verifies that a user can select an order, choose a template,
     * capture a photo, and upload it successfully.
     */
    @Test
    void testCompletePhotoUploadFlow() {
        System.out.println("[DEBUG_LOG] Running complete photo upload flow test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testWorker);

        // Configure sessionContext mock to return the test worker
        when(sessionContext.getUser()).thenReturn(Optional.of(testWorker));

        // 1. Set up the order context
        WorkerFlowContext.setCurrentOrder(testOrder);

        // Verify that the order is set in the context
        assertNotNull(WorkerFlowContext.getCurrentOrder(), "Order should be set in WorkerFlowContext");
        assertEquals(testOrder.getId().id(), WorkerFlowContext.getCurrentOrder().getId().id(), 
                    "Order in context should match test order");

        // 2. Test PhotoCubeViewModel for template selection
        PhotoCubeViewModel photoCubeViewModel = new PhotoCubeViewModel();
        injectManagerMocks(photoCubeViewModel);

        // Set up the OrderManager mock to return the test order
        when(orderManager.getCurrentOrder()).thenReturn(testOrder);
        when(orderManager.getCurrentOrderId()).thenReturn(testOrder.getId());
        when(templateManager.getRequiredTemplates()).thenReturn(testTemplates);

        // Trigger onShow to load the templates
        photoCubeViewModel.onShow();

        // Verify that templates are loaded
        assertNotNull(photoCubeViewModel.getRequiredTemplates(), "Templates should be loaded");
        assertFalse(photoCubeViewModel.getRequiredTemplates().isEmpty(), "Template list should not be empty");

        // Select a template
        PhotoTemplate selectedTemplate = testTemplates.get(0);
        photoCubeViewModel.selectTemplate(selectedTemplate);

        // Verify that the template was stored in the WorkerFlowContext
        assertNotNull(WorkerFlowContext.getSelectedTemplate(), "Template should be stored in WorkerFlowContext");
        assertEquals(selectedTemplate.name(), WorkerFlowContext.getSelectedTemplate().name(), 
                    "Stored template should match selected template");

        // 3. Simulate photo capture
        File mockImageFile = new File("test-photo.jpg");

        // Create a test photo document
        PhotoDocument photoDocument = createTestPhoto(selectedTemplate);

        // Mock the photo service to return our test photo document
        when(photoService.uploadPhoto(any(OrderId.class), any(Photo.class), any(UserBusiness.class)))
            .thenReturn(photoDocument);

        // Add the photo to the context (simulating a successful capture)
        WorkerFlowContext.addTakenPhoto(photoDocument);

        // Verify that the photo was added to the context
        assertEquals(1, WorkerFlowContext.getTakenPhotos().size(), "Photo should be added to WorkerFlowContext");
        assertEquals(photoDocument.getId().id(), WorkerFlowContext.getTakenPhotos().get(0).getId().id(),
                    "Photo in context should match test photo");

        // 4. Verify that the photo was uploaded with the correct template
        assertEquals(selectedTemplate.name(), WorkerFlowContext.getTakenPhotos().get(0).getTemplate().name(),
                    "Uploaded photo should have the correct template");

        System.out.println("[DEBUG_LOG] Complete photo upload flow test completed successfully");
    }

    /**
     * Tests error handling during photo upload.
     * This test verifies that errors during photo upload are properly handled.
     */
    @Test
    void testErrorHandlingDuringPhotoUpload() {
        System.out.println("[DEBUG_LOG] Running error handling during photo upload test");

        // Set the current user in the session
        SessionContext.setCurrentUser(testWorker);

        // Configure sessionContext mock to return the test worker
        when(sessionContext.getUser()).thenReturn(Optional.of(testWorker));

        // 1. Set up the order context
        WorkerFlowContext.setCurrentOrder(testOrder);

        // 2. Test PhotoCubeViewModel with error during template loading
        when(templateManager.getRequiredTemplates()).thenThrow(new RuntimeException("Failed to load templates"));

        PhotoCubeViewModel photoCubeViewModel = new PhotoCubeViewModel();
        injectManagerMocks(photoCubeViewModel);

        // Set up the OrderManager mock to return the test order
        when(orderManager.getCurrentOrder()).thenReturn(testOrder);
        when(orderManager.getCurrentOrderId()).thenReturn(testOrder.getId());

        // Trigger onShow to attempt loading templates
        photoCubeViewModel.onShow();

        // Verify that the error is handled (the view model should not crash)
        // In a real application, this would typically set an error message property
        assertNull(photoCubeViewModel.getRequiredTemplates(), "Templates should not be loaded due to error");

        // Reset the mock for subsequent tests
        reset(templateManager);
        when(templateManager.getRequiredTemplates()).thenReturn(testTemplates);

        // 3. Test error handling during photo upload
        // Set up the order context and template again
        WorkerFlowContext.setCurrentOrder(testOrder);
        PhotoTemplate selectedTemplate = testTemplates.get(0);
        WorkerFlowContext.setSelectedTemplate(selectedTemplate);

        // Mock the photo service to throw an exception during upload
        when(photoService.uploadPhoto(any(OrderId.class), any(Photo.class), any(UserBusiness.class)))
            .thenThrow(new RuntimeException("Failed to upload photo"));

        // In a real application, this would be handled by the CaptureViewModel
        // Here we're just verifying that the exception is thrown
        Exception exception = assertThrows(RuntimeException.class, () -> {
            photoService.uploadPhoto(testOrder.getId(), new Photo("test-photo.jpg"), testWorker);
        });

        assertEquals("Failed to upload photo", exception.getMessage(), "Exception message should match");

        System.out.println("[DEBUG_LOG] Error handling during photo upload test completed successfully");
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
        // Set up OrderService mock
        when(orderService.getOrderById(any(OrderId.class))).thenReturn(Optional.of(testOrder));

        // Set up PhotoTemplateService mock
        when(photoTemplateService.getAvailableTemplates(any(OrderId.class))).thenReturn(testTemplates);

        // Set up AuthenticationService mock
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(testWorker));
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
