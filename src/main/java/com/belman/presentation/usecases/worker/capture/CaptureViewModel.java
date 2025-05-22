package com.belman.presentation.usecases.worker.capture;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.PhotoService;
import com.belman.application.usecase.photo.CameraService;
import com.belman.application.usecase.photo.SimulatedCameraService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * ViewModel for the CaptureView.
 * Manages the state and logic for capturing photos for a specific template.
 */
public class CaptureViewModel extends BaseViewModel<CaptureViewModel> {

    private final SimulatedCameraService simulatedCameraService;
    private final CameraService cameraService;

    @Inject
    private PhotoService photoService;

    // Properties for UI binding
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Ready to capture photo");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty photoTaken = new SimpleBooleanProperty(false);
    private final ObjectProperty<PhotoTemplate> currentTemplate = new SimpleObjectProperty<>();
    private final StringProperty templateName = new SimpleStringProperty("");
    private final StringProperty templateDescription = new SimpleStringProperty("");
    private final ObjectProperty<File> capturedPhotoFile = new SimpleObjectProperty<>();

    // Properties for mock image selection
    private final ObservableList<File> availableMockImages = FXCollections.observableArrayList();
    private final BooleanProperty mockImagesLoaded = new SimpleBooleanProperty(false);

    public CaptureViewModel() {
        // Try to get the SimulatedCameraService, fall back to regular CameraService if not available
        SimulatedCameraService simService = null;
        try {
            simService = ServiceLocator.getService(SimulatedCameraService.class);
        } catch (Exception e) {
            // Service not registered, create a new instance
            simService = new SimulatedCameraService();
            // Register it for future use
            ServiceLocator.registerService(SimulatedCameraService.class, simService);
        }

        this.simulatedCameraService = simService;
        this.cameraService = ServiceLocator.getService(CameraService.class);
    }

    @Override
    public void onShow() {
        // Load the selected template from the worker flow context
        loadSelectedTemplate();

        // Load mock images
        loadMockImages();
    }

    /**
     * Loads the mock images from the simulated camera service.
     */
    private void loadMockImages() {
        loading.set(true);
        statusMessage.set("Loading mock images...");

        // Clear the current list
        availableMockImages.clear();

        // Get the mock images from the simulated camera service
        List<File> mockImages = simulatedCameraService.getAvailableMockImages();

        if (mockImages.isEmpty()) {
            errorMessage.set("No mock images available.");
            loading.set(false);
            return;
        }

        // Add the mock images to the observable list
        availableMockImages.addAll(mockImages);
        mockImagesLoaded.set(true);

        loading.set(false);
        statusMessage.set("Select a mock image to simulate taking a photo.");
    }

    /**
     * Loads the selected template from the worker flow context.
     */
    private void loadSelectedTemplate() {
        PhotoTemplate template = WorkerFlowContext.getSelectedTemplate();
        if (template == null) {
            errorMessage.set("No template selected.");
            return;
        }

        currentTemplate.set(template);
        templateName.set(com.belman.presentation.providers.PhotoTemplateLabelProvider.getDisplayLabel(template));
        templateDescription.set(template.description());
        statusMessage.set("Ready to take photo for " + com.belman.presentation.providers.PhotoTemplateLabelProvider.getDisplayLabel(template));
    }

    /**
     * Takes a photo using the device camera.
     * This method is kept for backward compatibility but is not used in the simulated camera UI.
     */
    public void takePhoto() {
        if (currentTemplate.get() == null) {
            errorMessage.set("No template selected.");
            return;
        }

        // In the simulated camera UI, we don't actually take a photo
        // Instead, the user selects a mock image from the grid
        statusMessage.set("Please select a mock image from the grid.");
    }

    /**
     * Selects a mock image to simulate taking a photo.
     * 
     * @param selectedImage the selected mock image
     */
    public void selectMockImage(File selectedImage) {
        if (currentTemplate.get() == null) {
            errorMessage.set("No template selected. Please go back and select a template before taking a photo.");
            statusMessage.set("Error: No template selected");
            return;
        }

        if (selectedImage == null) {
            errorMessage.set("No image selected.");
            return;
        }

        loading.set(true);
        statusMessage.set("Processing selected image...");

        // Set the selected image in the simulated camera service
        simulatedCameraService.setSelectedImage(selectedImage);

        // Set the captured photo file
        capturedPhotoFile.set(selectedImage);
        photoTaken.set(true);
        loading.set(false);
        statusMessage.set("Image selected. Review or select another.");
    }

    /**
     * Confirms the captured photo and saves it.
     */
    public void confirmPhoto() {
        if (capturedPhotoFile.get() == null) {
            errorMessage.set("No photo has been taken.");
            return;
        }

        if (currentTemplate.get() == null) {
            errorMessage.set("No template selected. Please go back and select a template before confirming a photo.");
            statusMessage.set("Error: No template selected");
            return;
        }

        OrderBusiness order = WorkerFlowContext.getCurrentOrder();
        if (order == null) {
            errorMessage.set("No order is currently loaded.");
            return;
        }

        loading.set(true);
        statusMessage.set("Saving photo...");

        // Get the current user
        SessionContext.getCurrentUser().ifPresentOrElse(
            user -> {
                // Upload the photo
                try {
                    PhotoDocument photo = photoService.uploadPhoto(
                        capturedPhotoFile.get(),
                        order.getId(),
                        currentTemplate.get(),
                        user
                    );

                    // Add the photo to the worker flow context
                    WorkerFlowContext.addTakenPhoto(photo);

                    // Navigate back to the photo cube view
                    loading.set(false);
                    statusMessage.set("Photo saved successfully!");

                    // Short delay before navigating back
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            Router.navigateTo(PhotoCubeView.class);
                        } catch (InterruptedException e) {
                            // Ignore
                        }
                    }).start();

                } catch (Exception e) {
                    errorMessage.set("Error saving photo: " + e.getMessage());
                    loading.set(false);
                }
            },
            () -> {
                errorMessage.set("No user is logged in.");
                loading.set(false);
            }
        );
    }

    /**
     * Retakes the photo.
     */
    public void retakePhoto() {
        capturedPhotoFile.set(null);
        photoTaken.set(false);
        statusMessage.set("Ready to take photo for " + currentTemplate.get().name());
    }

    /**
     * Cancels the photo capture and navigates back to the photo cube view.
     */
    public void cancel() {
        // Use ViewStackManager directly instead of Router to ensure proper back navigation
        com.belman.presentation.core.ViewStackManager.getInstance().navigateBack();
    }

    // Getters for properties

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public BooleanProperty photoTakenProperty() {
        return photoTaken;
    }

    public ObjectProperty<PhotoTemplate> currentTemplateProperty() {
        return currentTemplate;
    }

    public StringProperty templateNameProperty() {
        return templateName;
    }

    public StringProperty templateDescriptionProperty() {
        return templateDescription;
    }

    public ObjectProperty<File> capturedPhotoFileProperty() {
        return capturedPhotoFile;
    }

    /**
     * Gets the observable list of available mock images.
     * 
     * @return the observable list of available mock images
     */
    public ObservableList<File> getAvailableMockImages() {
        return availableMockImages;
    }

    /**
     * Gets the property indicating whether mock images have been loaded.
     * 
     * @return the property indicating whether mock images have been loaded
     */
    public BooleanProperty mockImagesLoadedProperty() {
        return mockImagesLoaded;
    }
}
