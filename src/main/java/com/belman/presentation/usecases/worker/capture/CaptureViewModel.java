package com.belman.presentation.usecases.worker.capture;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.photo.Photo;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoTemplate;
import com.belman.domain.services.PhotoService;
import com.belman.application.usecase.photo.CameraService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeView;
import javafx.beans.property.*;

import java.io.File;
import java.util.Optional;

/**
 * ViewModel for the CaptureView.
 * Manages the state and logic for capturing photos for a specific template.
 */
public class CaptureViewModel extends BaseViewModel<CaptureViewModel> {

    private final CameraService cameraService = ServiceLocator.getService(CameraService.class);

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

    @Override
    public void onShow() {
        // Load the selected template from the worker flow context
        loadSelectedTemplate();

        // Check if camera is available
        if (!cameraService.isCameraAvailable()) {
            errorMessage.set("Camera is not available on this device.");
            return;
        }
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
        templateName.set(template.name());
        templateDescription.set(template.description());
        statusMessage.set("Ready to take photo for " + template.name());
    }

    /**
     * Takes a photo using the device camera.
     */
    public void takePhoto() {
        if (currentTemplate.get() == null) {
            errorMessage.set("No template selected.");
            return;
        }

        if (!cameraService.isCameraAvailable()) {
            errorMessage.set("Camera is not available.");
            return;
        }

        loading.set(true);
        statusMessage.set("Taking photo...");

        // Take the photo
        Optional<File> photoFile = cameraService.takePhoto();

        photoFile.ifPresentOrElse(
            file -> {
                capturedPhotoFile.set(file);
                photoTaken.set(true);
                loading.set(false);
                statusMessage.set("Photo taken. Review or retake.");
            },
            () -> {
                // User cancelled
                loading.set(false);
                statusMessage.set("Photo capture cancelled.");
            }
        );
    }

    /**
     * Confirms the captured photo and saves it.
     */
    public void confirmPhoto() {
        if (capturedPhotoFile.get() == null) {
            errorMessage.set("No photo has been taken.");
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
        Router.navigateBack();
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
}
