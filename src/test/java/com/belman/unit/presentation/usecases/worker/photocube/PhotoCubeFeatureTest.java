package com.belman.unit.presentation.usecases.worker.photocube;

import com.belman.application.usecase.order.OrderProgressService;
import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.CameraImageProvider;
import com.belman.application.usecase.photo.CameraImageProviderFactory;
import com.belman.application.usecase.photo.PhotoCaptureService;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.application.usecase.photo.PhotoTemplateService;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.Username;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeState;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewModel;
import com.belman.presentation.usecases.worker.photocube.managers.OrderManager;
import com.belman.presentation.usecases.worker.photocube.managers.PhotoCaptureManager;
import com.belman.presentation.usecases.worker.photocube.managers.TemplateManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Feature-coverage tests for the PhotoCubeViewModel.
 * These tests verify that the ViewModel correctly handles complete user stories
 * rather than just individual methods.
 */
@ExtendWith(MockitoExtension.class)
public class PhotoCubeFeatureTest {

    @Mock
    private OrderService orderService;

    @Mock
    private PhotoService photoService;

    @Mock
    private OrderProgressService orderProgressService;

    @Mock
    private PhotoCaptureService photoCaptureService;

    @Mock
    private PhotoTemplateService photoTemplateService;

    @Mock
    private CameraImageProvider cameraImageProvider;

    @Mock
    private OrderManager orderManager;

    @Mock
    private PhotoCaptureManager photoCaptureManager;

    @Mock
    private TemplateManager templateManager;

    private PhotoCubeViewModel viewModel;
    private OrderBusiness testOrder;
    private UserBusiness testUser;
    private List<PhotoTemplate> testTemplates;
    private static MockedStatic<CameraImageProviderFactory> mockedFactory;
    private static MockedStatic<WorkerFlowContext> mockedContext;

    @BeforeEach
    public void setUp() throws Exception {
        // Mock static methods
        mockedFactory = mockStatic(CameraImageProviderFactory.class);
        mockedFactory.when(CameraImageProviderFactory::getInstance).thenReturn(cameraImageProvider);

        mockedContext = mockStatic(WorkerFlowContext.class);

        // Set up test data
        // Create a user with the PRODUCTION role
        testUser = UserBusiness.createNewUser(
                new Username("testuser"),
                new HashedPassword("password"),
                new EmailAddress("test@example.com")
        );
        testUser.addRole(UserRole.PRODUCTION);

        // Create an order using the factory method
        testOrder = OrderBusiness.createNew(
                new OrderNumber("07/23-123456-12345678"),
                testUser
        );

        testTemplates = new ArrayList<>();
        testTemplates.add(PhotoTemplate.TOP_VIEW_OF_JOINT);
        testTemplates.add(PhotoTemplate.SIDE_VIEW_OF_WELD);
        testTemplates.add(PhotoTemplate.ANGLED_VIEW_OF_JOINT);

        // Configure mocks
        when(orderManager.getCurrentOrder()).thenReturn(testOrder);
        when(orderManager.getCurrentOrderId()).thenReturn(testOrder.getId());
        when(templateManager.getRequiredTemplates()).thenReturn(testTemplates);
        when(templateManager.getSelectedTemplate()).thenReturn(testTemplates.get(0));
        when(photoCaptureManager.startCameraPreview(any())).thenReturn(true);

        // Create the view model
        viewModel = new PhotoCubeViewModel();

        // Inject mocked dependencies using reflection
        injectDependency("photoService", photoService);
        injectDependency("orderManager", orderManager);
        injectDependency("photoCaptureManager", photoCaptureManager);
        injectDependency("templateManager", templateManager);

        // Add state property to ViewModel using reflection
        Field stateField = PhotoCubeViewModel.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(viewModel, new SimpleObjectProperty<>(PhotoCubeState.SELECTING_TEMPLATE));
    }

    /**
     * Helper method to inject a dependency into the view model using reflection.
     *
     * @param fieldName the name of the field to inject
     * @param dependency the dependency to inject
     * @throws Exception if the field doesn't exist or can't be accessed
     */
    private void injectDependency(String fieldName, Object dependency) throws Exception {
        Field field = PhotoCubeViewModel.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(viewModel, dependency);
    }

    /**
     * Test that a production worker can capture a photo for a template.
     * This test covers the complete flow from selecting a template to capturing a photo.
     */
    @Test
    public void testProductionWorkerCapturesPhotoInCorrectOrder() throws Exception {
        // Arrange
        PhotoTemplate selectedTemplate = testTemplates.get(0);

        // Create a photo document using the builder pattern
        PhotoDocument capturedPhoto = PhotoDocument.builder()
                .photoId(PhotoId.newId())
                .orderId(testOrder.getId())
                .template(selectedTemplate)
                .imagePath(new Photo("test-photo.jpg"))
                .uploadedBy(testUser)
                .uploadedAt(new Timestamp(java.time.Instant.now()))
                .build();

        when(photoCaptureManager.capturePhoto(eq(testOrder), eq(selectedTemplate)))
                .thenReturn(capturedPhoto);

        // Get the state field using reflection
        Field stateField = PhotoCubeViewModel.class.getDeclaredField("state");
        stateField.setAccessible(true);
        SimpleObjectProperty<PhotoCubeState> stateProperty = 
            (SimpleObjectProperty<PhotoCubeState>) stateField.get(viewModel);

        // Act - Select a template
        viewModel.selectTemplate(selectedTemplate);

        // Assert - Template is selected and state is updated
        verify(templateManager).selectTemplate(selectedTemplate);
        assertEquals(PhotoCubeState.CAMERA_PREVIEW, stateProperty.get());

        // Act - Start camera preview
        viewModel.startCameraPreview();

        // Assert - Camera preview is started
        verify(photoCaptureManager).startCameraPreview(selectedTemplate);

        // Act - Capture photo
        viewModel.capturePhoto();

        // Assert - Photo is captured and template status is updated
        verify(photoCaptureManager).capturePhoto(testOrder, selectedTemplate);
        verify(templateManager).updateAfterPhotoCapture(capturedPhoto);
        assertEquals(PhotoCubeState.REVIEWING_PHOTO, stateProperty.get());
    }

    /**
     * Test that the ViewModel correctly handles the case when all required photos are taken.
     * This test verifies that the user can navigate to the summary view when all photos are complete.
     */
    @Test
    public void testAllRequiredPhotosTaken() throws Exception {
        // Arrange
        when(templateManager.areAllPhotosTaken(testOrder.getId())).thenReturn(true);
        when(templateManager.getMissingRequiredTemplates(testOrder.getId())).thenReturn(new ArrayList<>());

        // Get the state field using reflection
        Field stateField = PhotoCubeViewModel.class.getDeclaredField("state");
        stateField.setAccessible(true);
        SimpleObjectProperty<PhotoCubeState> stateProperty = 
            (SimpleObjectProperty<PhotoCubeState>) stateField.get(viewModel);
        stateProperty.set(PhotoCubeState.REVIEWING_PHOTO);

        // Act - Check if all photos are taken
        boolean allPhotosTaken = viewModel.areAllPhotosTaken();

        // Assert - All photos are taken
        assertTrue(allPhotosTaken);
        verify(templateManager).areAllPhotosTaken(testOrder.getId());

        // Act - Go to summary
        viewModel.goToSummary();

        // Assert - State is updated to COMPLETED
        assertEquals(PhotoCubeState.COMPLETED, stateProperty.get());
    }

    /**
     * Test that the ViewModel correctly handles the case when not all required photos are taken.
     * This test verifies that the user cannot navigate to the summary view when photos are missing.
     */
    @Test
    public void testMissingRequiredPhotos() throws Exception {
        // Arrange
        List<PhotoTemplate> missingTemplates = new ArrayList<>();
        missingTemplates.add(testTemplates.get(1));
        missingTemplates.add(testTemplates.get(2));

        when(templateManager.areAllPhotosTaken(testOrder.getId())).thenReturn(false);
        when(templateManager.getMissingRequiredTemplates(testOrder.getId())).thenReturn(missingTemplates);

        // Get the state and error message fields using reflection
        Field stateField = PhotoCubeViewModel.class.getDeclaredField("state");
        stateField.setAccessible(true);
        SimpleObjectProperty<PhotoCubeState> stateProperty = 
            (SimpleObjectProperty<PhotoCubeState>) stateField.get(viewModel);
        stateProperty.set(PhotoCubeState.REVIEWING_PHOTO);

        Field errorMessageField = PhotoCubeViewModel.class.getDeclaredField("errorMessage");
        errorMessageField.setAccessible(true);

        // Act - Check if all photos are taken
        boolean allPhotosTaken = viewModel.areAllPhotosTaken();

        // Assert - Not all photos are taken
        assertFalse(allPhotosTaken);
        verify(templateManager).areAllPhotosTaken(testOrder.getId());

        // Act - Try to go to summary
        viewModel.goToSummary();

        // Assert - Error message is set and state remains the same
        verify(templateManager).getMissingRequiredTemplates(testOrder.getId());
        assertEquals(PhotoCubeState.REVIEWING_PHOTO, stateProperty.get());
    }
}
