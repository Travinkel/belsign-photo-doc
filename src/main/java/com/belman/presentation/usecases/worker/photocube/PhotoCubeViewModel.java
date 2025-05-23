package com.belman.presentation.usecases.worker.photocube;

import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.CameraImageProvider;
import com.belman.application.usecase.photo.CameraImageProviderFactory;
import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.providers.PhotoTemplateLabelProvider;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.photocube.managers.OrderManager;
import com.belman.presentation.usecases.worker.photocube.managers.PhotoCaptureManager;
import com.belman.presentation.usecases.worker.photocube.managers.TemplateManager;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ViewModel for the PhotoCubeView.
 * Manages the state and logic for displaying the unfolded cube layout
 * and selecting photo templates.
 * 
 * This class has been refactored to use manager classes for better separation of concerns:
 * - OrderManager: Handles order loading and management
 * - PhotoCaptureManager: Handles photo capture and camera interactions
 * - TemplateManager: Handles template management and status tracking
 */
public class PhotoCubeViewModel extends BaseViewModel<PhotoCubeViewModel> {

    @Inject
    private PhotoService photoService;

    // Managers for different responsibilities
    private final OrderManager orderManager;
    private final PhotoCaptureManager photoCaptureManager;
    private final TemplateManager templateManager;

    // Properties for UI binding
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Loading...");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<PhotoCubeState> state = new SimpleObjectProperty<>(PhotoCubeState.LOADING);

    /**
     * Creates a new PhotoCubeViewModel.
     */
    public PhotoCubeViewModel() {
        // Initialize managers
        orderManager = ServiceLocator.getService(OrderManager.class);
        photoCaptureManager = ServiceLocator.getService(PhotoCaptureManager.class);
        templateManager = ServiceLocator.getService(TemplateManager.class);

        // Set up property bindings between managers and this view model
        setupPropertyBindings();
    }

    /**
     * Sets up property bindings between managers and this view model.
     */
    private void setupPropertyBindings() {
        // Bind error messages from managers to this view model
        orderManager.errorMessageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                errorMessage.set(newVal);
            }
        });

        photoCaptureManager.errorMessageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                errorMessage.set(newVal);
            }
        });

        templateManager.errorMessageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                errorMessage.set(newVal);
            }
        });

        // Bind status messages from managers to this view model
        orderManager.statusMessageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                statusMessage.set(newVal);
            }
        });

        photoCaptureManager.statusMessageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                statusMessage.set(newVal);
            }
        });

        templateManager.statusMessageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                statusMessage.set(newVal);
            }
        });
    }

    @Override
    public void onShow() {
        // Load the current order and its photos
        loadCurrentOrder();
    }

    /**
     * Loads the current order and its photos.
     */
    private void loadCurrentOrder() {
        loading.set(true);
        statusMessage.set("Loading order...");

        try {
            // Load the order using the OrderManager
            boolean orderLoaded = orderManager.loadCurrentOrder();

            if (!orderLoaded) {
                loading.set(false);
                return;
            }

            // Get the current order ID
            OrderId orderId = orderManager.getCurrentOrderId();
            if (orderId == null) {
                errorMessage.set("Invalid order ID. Please try again or contact support.");
                loading.set(false);
                return;
            }

            // Load photos for the order
            loadPhotosForOrder(orderId);
        } catch (Exception e) {
            errorMessage.set("Unexpected error loading order: " + e.getMessage() + ". Please try again or contact support.");
            loading.set(false);
        }
    }

    /**
     * Loads the photos and templates for the specified order.
     *
     * @param orderId the ID of the order
     */
    private void loadPhotosForOrder(OrderId orderId) {
        statusMessage.set("Loading photos and templates...");
        loading.set(true);

        // Maximum number of retry attempts
        final int MAX_RETRIES = 3;
        loadPhotosWithRetry(orderId, 0, MAX_RETRIES);
    }

    /**
     * Recursive method to load photos with retry mechanism.
     * 
     * @param orderId the ID of the order
     * @param currentRetry the current retry attempt (0-based)
     * @param maxRetries the maximum number of retry attempts
     */
    private void loadPhotosWithRetry(OrderId orderId, int currentRetry, int maxRetries) {
        try {
            // Get all photos for the order
            List<PhotoDocument> photos = photoService.getPhotosByOrderId(orderId);

            // Load templates using the TemplateManager
            boolean templatesLoaded = templateManager.loadTemplates(orderId);

            if (!templatesLoaded) {
                loading.set(false);
                return;
            }

            // Update template status based on the photos
            templateManager.updateTemplateStatus(photos);

            loading.set(false);
            state.set(PhotoCubeState.SELECTING_TEMPLATE);
            statusMessage.set("Ready to take photos. " + templateManager.photosCompletedProperty().get() + " of " + 
                            templateManager.totalPhotosRequiredProperty().get() + " photos taken.");
        } catch (Exception e) {
            // If we haven't exceeded the maximum number of retries, try again
            if (currentRetry < maxRetries) {
                int nextRetry = currentRetry + 1;
                statusMessage.set("Connection issue detected. Retrying... (Attempt " + nextRetry + "/" + maxRetries + ")");

                // Wait a bit before retrying (exponential backoff)
                try {
                    Thread.sleep(1000 * nextRetry); // Wait longer with each retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                // Recursive call to retry
                loadPhotosWithRetry(orderId, nextRetry, maxRetries);
            } else {
                // We've exhausted all retries, show error message
                errorMessage.set("Failed to load photos and templates after " + maxRetries + 
                               " attempts. Error: " + e.getMessage() + ". Please check your connection and try again.");
                loading.set(false);
                state.set(PhotoCubeState.ERROR);
                statusMessage.set("Error loading data. Please try refreshing.");
            }
        }
    }

    /**
     * Selects a photo template for capture.
     *
     * @param template the template to select
     */
    public void selectTemplate(PhotoTemplate template) {
        // Check if we can transition to the camera preview state
        if (!state.get().canTransitionTo(PhotoCubeState.CAMERA_PREVIEW)) {
            errorMessage.set("Cannot select template in current state: " + state.get().getDescription());
            return;
        }

        loading.set(true);

        try {
            // Store the current order in the WorkerFlowContext
            WorkerFlowContext.setCurrentOrder(orderManager.getCurrentOrder());

            // Select the template using the TemplateManager
            templateManager.selectTemplate(template);

            // Update state to camera preview
            state.set(PhotoCubeState.CAMERA_PREVIEW);
            loading.set(false);
        } catch (Exception e) {
            errorMessage.set("Error selecting template: " + e.getMessage() + ". Please try again.");
            loading.set(false);
        }
    }

    /**
     * Starts the camera preview for the selected template.
     */
    public void startCameraPreview() {
        // Check if we can transition to the capturing photo state
        if (!state.get().canTransitionTo(PhotoCubeState.CAPTURING_PHOTO)) {
            errorMessage.set("Cannot start camera in current state: " + state.get().getDescription());
            return;
        }

        loading.set(true);

        try {
            // Check if a template is selected
            PhotoTemplate selectedTemplate = templateManager.getSelectedTemplate();
            if (selectedTemplate == null) {
                errorMessage.set("Please select a template before starting the camera.");
                loading.set(false);
                return;
            }

            // Start the camera preview using the PhotoCaptureManager
            boolean cameraStarted = photoCaptureManager.startCameraPreview(selectedTemplate);

            loading.set(false);

            if (!cameraStarted) {
                errorMessage.set("Failed to start camera. Please check camera permissions and try again.");
                state.set(PhotoCubeState.ERROR);
            } else {
                // Camera started successfully, update state
                state.set(PhotoCubeState.CAPTURING_PHOTO);
            }
        } catch (Exception e) {
            errorMessage.set("Error starting camera preview: " + e.getMessage() + ". Please try again or use another device.");
            loading.set(false);
            state.set(PhotoCubeState.ERROR);
        }
    }

    /**
     * Captures a photo for the selected template.
     */
    public void capturePhoto() {
        // Check if we can transition to the reviewing photo state
        if (!state.get().canTransitionTo(PhotoCubeState.REVIEWING_PHOTO)) {
            errorMessage.set("Cannot capture photo in current state: " + state.get().getDescription());
            return;
        }

        try {
            // Get the current order and selected template
            OrderBusiness order = orderManager.getCurrentOrder();
            PhotoTemplate template = templateManager.getSelectedTemplate();

            if (template == null) {
                errorMessage.set("No template selected. Please select a template before capturing a photo.");
                state.set(PhotoCubeState.ERROR);
                return;
            }

            // Capture the photo using the PhotoCaptureManager
            PhotoDocument savedPhoto = photoCaptureManager.capturePhoto(order, template);

            if (savedPhoto != null) {
                // Update the template status after photo capture
                templateManager.updateAfterPhotoCapture(savedPhoto);

                // Update state to reviewing photo
                state.set(PhotoCubeState.REVIEWING_PHOTO);
                statusMessage.set("Photo captured successfully. Review the photo or select another template.");
            } else {
                errorMessage.set("Failed to capture photo. Please try again.");
                state.set(PhotoCubeState.ERROR);
            }
        } catch (Exception e) {
            errorMessage.set("Error capturing photo: " + e.getMessage() + ". Please try again.");
            state.set(PhotoCubeState.ERROR);
        }
    }

    /**
     * Checks if all required photos have been taken.
     *
     * @return true if all required photos have been taken, false otherwise
     */
    public boolean areAllPhotosTaken() {
        OrderId orderId = orderManager.getCurrentOrderId();
        if (orderId == null) {
            return false;
        }

        return templateManager.areAllPhotosTaken(orderId);
    }

    /**
     * Gets the missing required templates for the current order.
     * 
     * @return a list of required templates that are missing photos
     */
    public List<PhotoTemplate> getMissingRequiredTemplates() {
        OrderId orderId = orderManager.getCurrentOrderId();
        if (orderId == null) {
            return java.util.Collections.emptyList();
        }

        return templateManager.getMissingRequiredTemplates(orderId);
    }

    /**
     * Navigates to the summary view if all required photos have been taken.
     */
    public void goToSummary() {
        // Check if we can transition to the completed state
        if (!state.get().canTransitionTo(PhotoCubeState.COMPLETED)) {
            errorMessage.set("Cannot proceed to summary in current state: " + state.get().getDescription());
            return;
        }

        loading.set(true);
        statusMessage.set("Checking photo completion status...");

        try {
            if (!areAllPhotosTaken()) {
                // Get the missing required templates
                List<PhotoTemplate> missingTemplates = getMissingRequiredTemplates();

                if (missingTemplates.isEmpty()) {
                    errorMessage.set("Not all required photos have been taken. Please complete all required photos before proceeding.");
                } else {
                    // Build a message with the missing templates
                    StringBuilder message = new StringBuilder("Missing required photos for: ");
                    for (int i = 0; i < missingTemplates.size(); i++) {
                        if (i > 0) {
                            message.append(", ");
                        }
                        message.append(com.belman.presentation.providers.PhotoTemplateLabelProvider.getDisplayLabel(missingTemplates.get(i)));
                    }
                    message.append(". Please complete these photos before proceeding to summary.");
                    errorMessage.set(message.toString());
                }
                loading.set(false);
                return;
            }

            // Update state to completed
            state.set(PhotoCubeState.COMPLETED);
            statusMessage.set("Preparing summary view...");

            // Navigate to the summary view
            Router.navigateTo(com.belman.presentation.usecases.worker.summary.SummaryView.class);

            // Note: We don't set loading to false here because we're navigating away from this view
        } catch (Exception e) {
            errorMessage.set("Error preparing summary view: " + e.getMessage() + ". Please try again.");
            loading.set(false);
            state.set(PhotoCubeState.ERROR);
        }
    }

    /**
     * Navigates back to the assigned order view.
     */
    public void goBack() {
        // Any state can transition back to the previous screen
        loading.set(true);
        statusMessage.set("Returning to previous screen...");

        try {
            // Reset state before navigating back
            // This ensures we don't leave the view in an inconsistent state
            state.set(PhotoCubeState.LOADING);

            // Use ViewStackManager directly instead of Router to ensure proper back navigation
            com.belman.presentation.core.ViewStackManager.getInstance().navigateBack();

            // Note: We don't set loading to false here because we're navigating away from this view
        } catch (Exception e) {
            errorMessage.set("Error navigating back: " + e.getMessage() + ". Please try again.");
            loading.set(false);
            state.set(PhotoCubeState.ERROR);
        }
    }

    /**
     * Sets whether to show only remaining templates.
     * When true, only templates that haven't been captured yet will be shown.
     * When false, all templates will be shown.
     * 
     * @param showRemainingOnly whether to show only remaining templates
     */
    public void setShowRemainingOnly(boolean showRemainingOnly) {
        templateManager.setShowRemainingOnly(showRemainingOnly);
    }

    /**
     * Checks if the given template is the last remaining template in the filtered list.
     * 
     * @param template the template to check
     * @return true if this is the last remaining template, false otherwise
     */
    public boolean isLastRemainingTemplate(PhotoTemplate template) {
        return templateManager.isLastRemainingTemplate(template);
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

    public ObjectProperty<OrderBusiness> currentOrderProperty() {
        return orderManager.currentOrderProperty();
    }

    public StringProperty orderNumberProperty() {
        return orderManager.orderNumberProperty();
    }

    public ObjectProperty<PhotoTemplate> selectedTemplateProperty() {
        return templateManager.selectedTemplateProperty();
    }

    public MapProperty<PhotoTemplate, Boolean> templateCompletionStatusProperty() {
        return templateManager.templateCompletionStatusProperty();
    }

    public ListProperty<PhotoDocument> takenPhotosProperty() {
        return templateManager.takenPhotosProperty();
    }

    public IntegerProperty photosCompletedProperty() {
        return templateManager.photosCompletedProperty();
    }

    public IntegerProperty totalPhotosRequiredProperty() {
        return templateManager.totalPhotosRequiredProperty();
    }

    public ObjectProperty<Image> currentPhotoPreviewProperty() {
        return photoCaptureManager.currentPhotoPreviewProperty();
    }

    public BooleanProperty cameraActiveProperty() {
        return photoCaptureManager.cameraActiveProperty();
    }

    public ListProperty<PhotoTemplateStatusViewModel> templateStatusListProperty() {
        return templateManager.templateStatusListProperty();
    }

    public BooleanProperty captureInProgressProperty() {
        return photoCaptureManager.captureInProgressProperty();
    }

    public BooleanProperty showRemainingOnlyProperty() {
        return templateManager.showRemainingOnlyProperty();
    }

    public List<PhotoTemplate> getRequiredTemplates() {
        return templateManager.getRequiredTemplates();
    }

    /**
     * Gets the current state property of the photo cube workflow.
     * This property can be observed to react to state changes in the UI.
     * 
     * @return the state property
     */
    public ObjectProperty<PhotoCubeState> stateProperty() {
        return state;
    }

    /**
     * Gets the current state of the photo cube workflow.
     * 
     * @return the current state
     */
    public PhotoCubeState getState() {
        return state.get();
    }
}
