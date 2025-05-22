package com.belman.presentation.usecases.worker.photocube;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.application.usecase.photo.PhotoService;
import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.CameraService;
import com.belman.application.usecase.photo.CameraImageProvider;
import com.belman.application.usecase.photo.CameraImageProviderFactory;
import com.belman.application.usecase.worker.WorkerService;
import com.belman.presentation.providers.PhotoTemplateLabelProvider;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ViewModel for the PhotoCubeView.
 * Manages the state and logic for displaying the unfolded cube layout
 * and selecting photo templates.
 */
public class PhotoCubeViewModel extends BaseViewModel<PhotoCubeViewModel> {

    @Inject
    private OrderService orderService;

    @Inject
    private PhotoService photoService;

    @Inject
    private WorkerService workerService;

    private final CameraImageProvider cameraImageProvider = CameraImageProviderFactory.getInstance();

    // Properties for UI binding
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Loading...");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> currentOrder = new SimpleObjectProperty<>();
    private final StringProperty orderNumber = new SimpleStringProperty("");

    // Properties for the Progressive Capture Dashboard
    private final ObjectProperty<PhotoTemplate> selectedTemplate = new SimpleObjectProperty<>();
    private final MapProperty<PhotoTemplate, Boolean> templateCompletionStatus = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final ListProperty<PhotoDocument> takenPhotos = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty photosCompleted = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPhotosRequired = new SimpleIntegerProperty(0);

    // New properties for the Progressive Capture Dashboard
    private final ObjectProperty<Image> currentPhotoPreview = new SimpleObjectProperty<>();
    private final BooleanProperty cameraActive = new SimpleBooleanProperty(false);
    private final ListProperty<PhotoTemplateStatusViewModel> templateStatusList = 
        new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty captureInProgress = new SimpleBooleanProperty(false);

    // Property for the "Show remaining only" toggle
    private final BooleanProperty showRemainingOnly = new SimpleBooleanProperty(false);

    // Filtered list of templates (when showRemainingOnly is true)
    private final ListProperty<PhotoTemplateStatusViewModel> filteredTemplateStatusList = 
        new SimpleListProperty<>(FXCollections.observableArrayList());

    // Templates for the dashboard - will be loaded from WorkerService
    private List<PhotoTemplate> requiredTemplates = new ArrayList<>();

    @Override
    public void onShow() {
        // Load the current order and its photos
        loadCurrentOrder();
    }

    /**
     * Loads the current order from the WorkerFlowContext.
     */
    private void loadCurrentOrder() {
        loading.set(true);
        statusMessage.set("Loading order...");

        try {
            // Get the current order from the WorkerFlowContext
            OrderBusiness order = WorkerFlowContext.getCurrentOrder();

            if (order == null) {
                // If no order is in the context, try to get one from the session
                SessionContext.getCurrentUser().ifPresentOrElse(
                    user -> {
                        try {
                            // Get all orders and find the first one for the current user
                            List<OrderBusiness> orders = orderService.getAllOrders();

                            if (orders.isEmpty()) {
                                errorMessage.set("No orders found in the system. Please contact your supervisor to create an order.");
                                loading.set(false);
                                return;
                            }

                            // Filter orders for the current user
                            List<OrderBusiness> userOrders = orders.stream()
                                .filter(o -> o.getCreatedBy() != null && 
                                       o.getCreatedBy().id().equals(user.getId()))
                                .toList();

                            if (userOrders.isEmpty()) {
                                errorMessage.set("No active orders assigned to you. Please contact your supervisor to assign an order for documentation.");
                                loading.set(false);
                                return;
                            }

                            // Set the current order to the first order for the user
                            OrderBusiness userOrder = userOrders.get(0);
                            currentOrder.set(userOrder);
                            orderNumber.set(userOrder.getOrderNumber().toString());

                            // Store the order in the WorkerFlowContext for future use
                            WorkerFlowContext.setCurrentOrder(userOrder);

                            // Load the photos for this order
                            loadPhotosForOrder(userOrder.getId());
                        } catch (Exception e) {
                            errorMessage.set("Error loading orders: " + e.getMessage() + ". Please try again or contact support.");
                            loading.set(false);
                        }
                    },
                    () -> {
                        errorMessage.set("No user is logged in. Please log in to continue.");
                        loading.set(false);
                    }
                );
            } else {
                // Use the order from the WorkerFlowContext
                currentOrder.set(order);
                orderNumber.set(order.getOrderNumber().toString());

                // Load the photos for this order
                loadPhotosForOrder(order.getId());
            }
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
            takenPhotos.setAll(photos);

            // Update the photos completed count
            photosCompleted.set(photos.size());

            // Get available templates from the WorkerService
            requiredTemplates = workerService.getAvailableTemplates(orderId);

            // If no templates are available, show a clear non-technical message
            if (requiredTemplates.isEmpty()) {
                errorMessage.set("No photo checklist defined for this order. Please contact QA team.");
                statusMessage.set("No photo templates found. Please contact QA team for assistance.");
                loading.set(false);
                return;
            }

            // Set the total required photos count
            totalPhotosRequired.set(requiredTemplates.size());

            // Initialize the template completion status map
            Map<PhotoTemplate, Boolean> completionStatus = new HashMap<>();

            // Create template status view models for each template
            ObservableList<PhotoTemplateStatusViewModel> statusList = FXCollections.observableArrayList();

            for (PhotoTemplate template : requiredTemplates) {
                boolean isCompleted = photos.stream()
                    .anyMatch(photo -> photo.getTemplate().equals(template));
                completionStatus.put(template, isCompleted);

                // Create a status view model for this template
                PhotoTemplateStatusViewModel statusViewModel = new PhotoTemplateStatusViewModel(
                    template, isCompleted, isCompleted, true);

                // If there's a photo for this template, find it and set the preview image
                if (isCompleted) {
                    photos.stream()
                        .filter(photo -> photo.getTemplate().equals(template))
                        .findFirst()
                        .ifPresent(photo -> {
                            // If this is the most recently taken photo, set it as the current preview
                            if (currentPhotoPreview.get() == null) {
                                try {
                                    // Try to load the photo as an image
                                    Image image = loadPhotoAsImage(photo);
                                    if (image != null) {
                                        currentPhotoPreview.set(image);
                                    }
                                } catch (Exception e) {
                                    // Log error but continue
                                    System.err.println("Error loading photo preview: " + e.getMessage());
                                }
                            }
                        });
                }

                statusList.add(statusViewModel);
            }

            // Update the observable lists
            templateCompletionStatus.putAll(completionStatus);
            templateStatusList.setAll(statusList);

            // Update the filtered template list to ensure UI is refreshed immediately
            updateFilteredTemplateList();

            // Check if there's a selected template in the WorkerFlowContext
            PhotoTemplate selectedTemplateFromContext = WorkerFlowContext.getSelectedTemplate();
            if (selectedTemplateFromContext != null) {
                selectedTemplate.set(selectedTemplateFromContext);

                // Update the selected state in the status view models only if the list is not empty
                if (!templateStatusList.isEmpty()) {
                    for (PhotoTemplateStatusViewModel statusViewModel : templateStatusList) {
                        statusViewModel.setSelected(
                            statusViewModel.getTemplate().equals(selectedTemplateFromContext));
                    }
                }
            } else if (!requiredTemplates.isEmpty()) {
                // Auto-select the first template if none is selected
                PhotoTemplate firstTemplate = requiredTemplates.get(0);
                selectedTemplate.set(firstTemplate);
                WorkerFlowContext.setSelectedTemplate(firstTemplate);

                // Update the selected state in the status view models only if the list is not empty
                if (!templateStatusList.isEmpty()) {
                    for (PhotoTemplateStatusViewModel statusViewModel : templateStatusList) {
                        statusViewModel.setSelected(
                            statusViewModel.getTemplate().equals(firstTemplate));
                    }
                }
            }

            loading.set(false);
            statusMessage.set("Ready to take photos. " + photosCompleted.get() + " of " + 
                            totalPhotosRequired.get() + " photos taken.");
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
        loading.set(true);
        statusMessage.set("Selecting template...");

        try {
            selectedTemplate.set(template);

            // Store the selected template in the worker flow context
            WorkerFlowContext.setSelectedTemplate(template);
            WorkerFlowContext.setCurrentOrder(currentOrder.get());

            // Update the selected state in the status view models only if the list is not empty
            if (!templateStatusList.isEmpty()) {
                for (PhotoTemplateStatusViewModel statusViewModel : templateStatusList) {
                    statusViewModel.setSelected(
                        statusViewModel.getTemplate().equals(template));
                }
            }

            // Update the filtered template list to ensure UI is refreshed immediately
            updateFilteredTemplateList();

            loading.set(false);
            statusMessage.set("Template selected: " + PhotoTemplateLabelProvider.getDisplayLabel(template) + ". Ready to capture.");
        } catch (Exception e) {
            errorMessage.set("Error selecting template: " + e.getMessage() + ". Please try again.");
            loading.set(false);
        }
    }

    /**
     * Starts the camera preview for the selected template.
     */
    public void startCameraPreview() {
        loading.set(true);
        statusMessage.set("Starting camera preview...");

        try {
            if (!cameraImageProvider.isCameraAvailable()) {
                errorMessage.set("Camera is not available on this device. Please check camera permissions or try another device.");
                loading.set(false);
                return;
            }

            // Check if a template is selected
            if (selectedTemplate.get() == null) {
                errorMessage.set("Please select a template before starting the camera.");
                loading.set(false);
                return;
            }

            // Start the camera preview
            cameraActive.set(true);

            // The actual camera preview will be handled by the controller
            loading.set(false);
            statusMessage.set("Camera preview started. Position the camera and tap 'Capture' when ready.");
        } catch (Exception e) {
            errorMessage.set("Error starting camera preview: " + e.getMessage() + ". Please try again or use another device.");
            loading.set(false);
            cameraActive.set(false);
        }
    }

    /**
     * Captures a photo for the selected template.
     */
    public void capturePhoto() {
        if (!cameraActive.get()) {
            errorMessage.set("Camera is not active. Please start the camera preview first.");
            return;
        }

        if (selectedTemplate.get() == null) {
            errorMessage.set("No template selected. Please select a template before capturing a photo.");
            return;
        }

        if (captureInProgress.get()) {
            errorMessage.set("Capture already in progress. Please wait.");
            return;
        }

        captureInProgress.set(true);
        statusMessage.set("Capturing photo...");

        try {
            // Capture the photo using the camera image provider
            OrderBusiness order = currentOrder.get();
            PhotoTemplate template = selectedTemplate.get();
            String orderId = order != null ? order.getId().toString() : "";
            Optional<File> capturedPhotoFile = cameraImageProvider.takePhoto(template, orderId);

            if (capturedPhotoFile.isEmpty()) {
                errorMessage.set("Failed to capture photo or operation was cancelled. Please try again.");
                captureInProgress.set(false);
                return;
            }

            // Convert the file to a Photo object
            File photoFile = capturedPhotoFile.get();
            Photo capturedPhoto = new Photo(photoFile.getName());

            // Save the photo
            savePhoto(capturedPhoto);
        } catch (Exception e) {
            errorMessage.set("Error capturing photo: " + e.getMessage() + ". Please try again.");
            captureInProgress.set(false);
        }
    }

    /**
     * Saves a captured photo for the selected template.
     * 
     * @param photo the photo to save
     */
    private void savePhoto(Photo photo) {
        statusMessage.set("Saving photo...");

        try {
            // Get the current order and selected template
            OrderBusiness order = currentOrder.get();
            PhotoTemplate template = selectedTemplate.get();

            if (order == null || template == null) {
                errorMessage.set("Missing order or template information. Please try again.");
                captureInProgress.set(false);
                return;
            }

            // Get the current user
            SessionContext.getCurrentUser().ifPresentOrElse(
                user -> {
                    try {
                        // Save the photo using the worker service
                        PhotoDocument savedPhoto = workerService.capturePhoto(
                            order.getId(), template, photo, user);

                        if (savedPhoto == null) {
                            errorMessage.set("Failed to save photo. Please try again.");
                            captureInProgress.set(false);
                            return;
                        }

                        // Update the UI
                        updateAfterPhotoCapture(savedPhoto);
                    } catch (Exception e) {
                        errorMessage.set("Error saving photo: " + e.getMessage() + ". Please try again.");
                        captureInProgress.set(false);
                    }
                },
                () -> {
                    errorMessage.set("No user is logged in. Please log in to continue.");
                    captureInProgress.set(false);
                }
            );
        } catch (Exception e) {
            errorMessage.set("Error saving photo: " + e.getMessage() + ". Please try again.");
            captureInProgress.set(false);
        }
    }

    /**
     * Updates the UI after a photo is captured and saved.
     * 
     * @param savedPhoto the saved photo document
     */
    private void updateAfterPhotoCapture(PhotoDocument savedPhoto) {
        try {
            // Add the photo to the taken photos list
            takenPhotos.add(savedPhoto);

            // Update the photos completed count
            photosCompleted.set(photosCompleted.get() + 1);

            // Update the template completion status
            PhotoTemplate template = savedPhoto.getTemplate();
            templateCompletionStatus.put(template, true);

            // Update the template status view model only if the list is not empty
            if (!templateStatusList.isEmpty()) {
                for (PhotoTemplateStatusViewModel statusViewModel : templateStatusList) {
                    if (statusViewModel.getTemplate().equals(template)) {
                        statusViewModel.setCaptured(true);
                        statusViewModel.setValidated(true);
                        break;
                    }
                }
            }

            // Update the filtered template list to ensure UI is refreshed immediately
            updateFilteredTemplateList();

            // Load the photo as an image for preview
            Image image = loadPhotoAsImage(savedPhoto);
            if (image != null) {
                currentPhotoPreview.set(image);
            }

            // Stop the camera preview
            cameraActive.set(false);

            // Reset the capture in progress flag
            captureInProgress.set(false);

            // Update the status message
            statusMessage.set("Photo captured successfully. " + photosCompleted.get() + " of " + 
                            totalPhotosRequired.get() + " photos taken.");

            // Auto-select the next template if available
            selectNextTemplate();
        } catch (Exception e) {
            errorMessage.set("Error updating UI after photo capture: " + e.getMessage() + ". Please try again.");
            captureInProgress.set(false);
        }
    }

    /**
     * Selects the next template that doesn't have a photo yet.
     */
    private void selectNextTemplate() {
        // Find the next template that doesn't have a photo
        for (PhotoTemplate template : requiredTemplates) {
            Boolean isCompleted = templateCompletionStatus.get(template);
            if (isCompleted == null || !isCompleted) {
                // Select this template
                selectTemplate(template);
                return;
            }
        }

        // All templates have photos, check if we can go to summary
        if (areAllPhotosTaken()) {
            statusMessage.set("All required photos have been taken. You can now proceed to the summary.");
        }
    }

    /**
     * Checks if all required photos have been taken.
     *
     * @return true if all required photos have been taken, false otherwise
     */
    public boolean areAllPhotosTaken() {
        OrderBusiness order = currentOrder.get();
        if (order == null) {
            return false;
        }

        try {
            // Use the WorkerService to check if all required photos have been taken
            return workerService.hasAllRequiredPhotos(order.getId());
        } catch (Exception e) {
            // If there's an error, fall back to checking the template completion status map
            errorMessage.set("Error checking if all photos are taken: " + e.getMessage());
            return templateCompletionStatus.values().stream().allMatch(Boolean::booleanValue);
        }
    }

    /**
     * Gets the missing required templates for the current order.
     * 
     * @return a list of required templates that are missing photos
     */
    public List<PhotoTemplate> getMissingRequiredTemplates() {
        OrderBusiness order = currentOrder.get();
        if (order == null) {
            return Collections.emptyList();
        }

        try {
            // Use the WorkerService to get the missing required templates
            return workerService.getMissingRequiredTemplates(order.getId());
        } catch (Exception e) {
            // If there's an error, log it and return an empty list
            errorMessage.set("Error getting missing required templates: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Navigates to the summary view if all required photos have been taken.
     */
    public void goToSummary() {
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

            statusMessage.set("Preparing summary view...");

            // Navigate to the summary view
            Router.navigateTo(com.belman.presentation.usecases.worker.summary.SummaryView.class);

            // Note: We don't set loading to false here because we're navigating away from this view
        } catch (Exception e) {
            errorMessage.set("Error preparing summary view: " + e.getMessage() + ". Please try again.");
            loading.set(false);
        }
    }

    /**
     * Navigates back to the assigned order view.
     */
    public void goBack() {
        loading.set(true);
        statusMessage.set("Returning to previous screen...");

        try {
            // Use ViewStackManager directly instead of Router to ensure proper back navigation
            com.belman.presentation.core.ViewStackManager.getInstance().navigateBack();

            // Note: We don't set loading to false here because we're navigating away from this view
        } catch (Exception e) {
            errorMessage.set("Error navigating back: " + e.getMessage() + ". Please try again.");
            loading.set(false);
        }
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
        return currentOrder;
    }

    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    public ObjectProperty<PhotoTemplate> selectedTemplateProperty() {
        return selectedTemplate;
    }

    public MapProperty<PhotoTemplate, Boolean> templateCompletionStatusProperty() {
        return templateCompletionStatus;
    }

    public ListProperty<PhotoDocument> takenPhotosProperty() {
        return takenPhotos;
    }

    public IntegerProperty photosCompletedProperty() {
        return photosCompleted;
    }

    public IntegerProperty totalPhotosRequiredProperty() {
        return totalPhotosRequired;
    }

    public ObjectProperty<Image> currentPhotoPreviewProperty() {
        return currentPhotoPreview;
    }

    public BooleanProperty cameraActiveProperty() {
        return cameraActive;
    }

    public ListProperty<PhotoTemplateStatusViewModel> templateStatusListProperty() {
        // Create a default empty list to return if needed
        ObservableList<PhotoTemplateStatusViewModel> emptyList = FXCollections.observableArrayList();
        SimpleListProperty<PhotoTemplateStatusViewModel> emptyProperty = new SimpleListProperty<>(emptyList);

        try {
            // First check if both lists are null or empty
            if ((templateStatusList == null || templateStatusList.isEmpty()) && 
                (filteredTemplateStatusList == null || filteredTemplateStatusList.isEmpty())) {
                // If both lists are empty, return an empty list
                System.out.println("Both template lists are empty, returning empty list");
                return emptyProperty;
            }

            // Check if the appropriate list is empty
            if (showRemainingOnly.get()) {
                // If showing remaining only and filtered list is empty but main list is not,
                // automatically switch to showing all templates
                if ((filteredTemplateStatusList == null || filteredTemplateStatusList.isEmpty()) && 
                    templateStatusList != null && !templateStatusList.isEmpty()) {
                    // Only switch if we have captured all templates
                    boolean allCaptured = true;
                    try {
                        allCaptured = templateStatusList.stream()
                            .allMatch(PhotoTemplateStatusViewModel::isCaptured);
                    } catch (Exception e) {
                        System.err.println("Error checking if all templates are captured: " + e.getMessage());
                    }

                    if (allCaptured) {
                        // Log this action
                        System.out.println("All templates captured, automatically switching to show all templates");
                        // Set a user-friendly message
                        errorMessage.set("All templates have been captured. Showing all templates.");
                        // Switch to showing all templates
                        showRemainingOnly.set(false);
                        // Return the main list if it's not null, otherwise return an empty list
                        return templateStatusList != null ? templateStatusList : emptyProperty;
                    }
                }

                // Return filtered list if it's not null and not empty, otherwise return an empty list
                if (filteredTemplateStatusList != null && !filteredTemplateStatusList.isEmpty()) {
                    return filteredTemplateStatusList;
                } else {
                    System.out.println("Filtered template list is null or empty, returning empty list");
                    return emptyProperty;
                }
            } else {
                // Return main list if it's not null and not empty, otherwise return an empty list
                if (templateStatusList != null && !templateStatusList.isEmpty()) {
                    return templateStatusList;
                } else {
                    System.out.println("Main template list is null or empty, returning empty list");
                    return emptyProperty;
                }
            }
        } catch (Exception e) {
            // Log the error and return an empty list
            System.err.println("Error in templateStatusListProperty: " + e.getMessage());
            return emptyProperty;
        }
    }

    public BooleanProperty captureInProgressProperty() {
        return captureInProgress;
    }

    public BooleanProperty showRemainingOnlyProperty() {
        return showRemainingOnly;
    }

    /**
     * Sets whether to show only remaining templates.
     * When true, only templates that haven't been captured yet will be shown.
     * When false, all templates will be shown.
     * 
     * @param showRemainingOnly whether to show only remaining templates
     */
    public void setShowRemainingOnly(boolean showRemainingOnly) {
        this.showRemainingOnly.set(showRemainingOnly);
        updateFilteredTemplateList();
    }

    /**
     * Updates the filtered template list based on the showRemainingOnly property.
     * When showRemainingOnly is true, only templates that haven't been captured yet will be included.
     * When showRemainingOnly is false, all templates will be included.
     */
    private void updateFilteredTemplateList() {
        try {
            // First check if the filtered template list is null to avoid NPE
            if (filteredTemplateStatusList == null) {
                System.err.println("Filtered template list is null, cannot update it");
                return;
            }

            // Check if the template status list is null or empty to avoid NPE
            if (templateStatusList == null || templateStatusList.isEmpty()) {
                // If the template status list is empty, ensure the filtered list is also empty
                System.out.println("Template status list is null or empty, clearing filtered list");
                try {
                    // Clear the list safely
                    safelyClearList(filteredTemplateStatusList);
                } catch (Exception e) {
                    System.err.println("Error clearing filtered template list: " + e.getMessage());
                }
                return;
            }

            if (showRemainingOnly.get()) {
                try {
                    // Create a safe copy of the template status list to avoid concurrent modification
                    List<PhotoTemplateStatusViewModel> safeList = new ArrayList<>();
                    try {
                        // Add all items from the template status list, with null check for each item
                        for (PhotoTemplateStatusViewModel item : templateStatusList) {
                            if (item != null) {
                                safeList.add(item);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error creating safe copy of template status list: " + e.getMessage());
                        // If we can't create a safe copy, use the original list
                        safeList = new ArrayList<>(templateStatusList);
                    }

                    // Filter out completed templates with additional error handling
                    List<PhotoTemplateStatusViewModel> remainingTemplates = new ArrayList<>();
                    for (PhotoTemplateStatusViewModel template : safeList) {
                        try {
                            if (template != null && !template.isCaptured()) {
                                remainingTemplates.add(template);
                            }
                        } catch (Exception e) {
                            System.err.println("Error checking if template is captured: " + e.getMessage());
                            // Include it in the remaining templates to be safe
                            if (template != null) {
                                remainingTemplates.add(template);
                            }
                        }
                    }

                    // Check if the filtered list would be empty
                    if (remainingTemplates.isEmpty() && !safeList.isEmpty()) {
                        // If all templates are captured and we're trying to show only remaining,
                        // show a message and revert to showing all templates
                        System.out.println("All templates captured, showing all templates instead of empty filtered list");
                        errorMessage.set("All templates have been captured. Showing all templates.");
                        showRemainingOnly.set(false);

                        try {
                            // Set all templates safely
                            safelySetAllItems(filteredTemplateStatusList, safeList);
                        } catch (Exception e) {
                            System.err.println("Error setting all templates in filtered list: " + e.getMessage());
                        }
                    } else {
                        // Set the filtered list to the remaining templates
                        System.out.println("Setting filtered list to " + remainingTemplates.size() + " remaining templates");

                        try {
                            // Set remaining templates safely
                            safelySetAllItems(filteredTemplateStatusList, remainingTemplates);
                        } catch (Exception e) {
                            System.err.println("Error setting remaining templates in filtered list: " + e.getMessage());
                            // Fallback to showing all templates
                            try {
                                safelySetAllItems(filteredTemplateStatusList, safeList);
                            } catch (Exception ex) {
                                System.err.println("Error in fallback for filtered list: " + ex.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error filtering templates: " + e.getMessage());
                    // Fallback to showing all templates
                    try {
                        safelySetAllItems(filteredTemplateStatusList, templateStatusList);
                    } catch (Exception ex) {
                        System.err.println("Error in fallback for filtered list: " + ex.getMessage());
                    }
                }
            } else {
                // Show all templates
                System.out.println("Showing all " + templateStatusList.size() + " templates");

                try {
                    safelySetAllItems(filteredTemplateStatusList, templateStatusList);
                } catch (Exception e) {
                    System.err.println("Error setting all templates in filtered list: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating filtered template list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Safely clears a list property.
     * This method adds additional error handling to prevent exceptions when clearing a list.
     * 
     * @param listProperty the list property to clear
     */
    private <T> void safelyClearList(ListProperty<T> listProperty) {
        if (listProperty == null) {
            return;
        }

        try {
            // Create a new empty observable list
            ObservableList<T> emptyList = FXCollections.observableArrayList();

            // Set the list property to the empty list
            listProperty.setAll(emptyList);
        } catch (Exception e) {
            System.err.println("Error clearing list: " + e.getMessage());

            try {
                // Fallback to using clear() method
                listProperty.clear();
            } catch (Exception ex) {
                System.err.println("Error using clear() method: " + ex.getMessage());
            }
        }
    }

    /**
     * Safely sets all items in a list property.
     * This method adds additional error handling to prevent exceptions when setting items in a list.
     * 
     * @param listProperty the list property to update
     * @param items the items to set
     */
    private <T> void safelySetAllItems(ListProperty<T> listProperty, List<T> items) {
        if (listProperty == null || items == null) {
            return;
        }

        try {
            // Create a new observable list with the items
            ObservableList<T> observableList = FXCollections.observableArrayList();

            // Add each item with null check
            for (T item : items) {
                if (item != null) {
                    observableList.add(item);
                }
            }

            // Set the list property to the new list
            listProperty.setAll(observableList);
        } catch (Exception e) {
            System.err.println("Error setting items in list: " + e.getMessage());

            try {
                // Fallback to clearing and adding each item
                listProperty.clear();
                for (T item : items) {
                    if (item != null) {
                        listProperty.add(item);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error using clear() and add() methods: " + ex.getMessage());
            }
        }
    }

    public List<PhotoTemplate> getRequiredTemplates() {
        return requiredTemplates;
    }

    /**
     * Checks if the given template is the last remaining template in the filtered list.
     * This is used to prevent IndexOutOfBoundsException when clearing selection after
     * selecting the last remaining template when showRemainingOnly is true.
     * 
     * @param template the template to check
     * @return true if this is the last remaining template, false otherwise
     */
    public boolean isLastRemainingTemplate(PhotoTemplate template) {
        // If template is null, it can't be the last remaining template
        if (template == null) {
            return false;
        }

        // If not showing remaining only, this check is not relevant
        if (!showRemainingOnly.get()) {
            return false;
        }

        // If template status list is null or empty, return false
        if (templateStatusList == null || templateStatusList.isEmpty()) {
            return false;
        }

        try {
            // Count how many templates are not captured yet
            long remainingCount = templateStatusList.stream()
                .filter(status -> !status.isCaptured())
                .count();

            // If there's only one remaining and it's this template, it's the last one
            if (remainingCount == 1) {
                return templateStatusList.stream()
                    .filter(status -> !status.isCaptured())
                    .anyMatch(status -> status.getTemplate() != null && status.getTemplate().equals(template));
            }

            return false;
        } catch (Exception e) {
            // Log the error but don't crash
            System.err.println("Error in isLastRemainingTemplate: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads a photo as a JavaFX Image.
     * 
     * @param photoDocument the photo document to load
     * @return the photo as a JavaFX Image, or null if the photo could not be loaded
     */
    private Image loadPhotoAsImage(PhotoDocument photoDocument) {
        try {
            if (photoDocument == null) {
                System.err.println("Photo document is null");
                return loadFallbackImage();
            }

            // Get the photo path from the photo document
            String photoPath = photoDocument.getImagePath().value();
            if (photoPath == null || photoPath.isBlank()) {
                System.err.println("Photo path is null or empty");
                return loadFallbackImage();
            }

            // Check if the photo path is a URL
            if (photoPath.startsWith("http://") || photoPath.startsWith("https://")) {
                // Load the image from the URL
                return new Image(photoPath, true); // Use background loading
            } else {
                // Load the image from the file system
                File photoFile = new File(photoPath);
                if (photoFile.exists()) {
                    // Load the image from the file
                    return new Image(photoFile.toURI().toString(), true); // Use background loading
                } 

                // Try to load from mock camera directory
                File mockCameraFile = new File("src/main/resources/mock/camera/" + photoFile.getName());
                if (mockCameraFile.exists()) {
                    return new Image(mockCameraFile.toURI().toString(), true); // Use background loading
                }

                // Try to load from mock camera dev-simulated directory
                File mockDevFile = new File("src/main/resources/mock/camera/dev-simulated/" + photoFile.getName());
                if (mockDevFile.exists()) {
                    return new Image(mockDevFile.toURI().toString(), true); // Use background loading
                }

                // Try to load from the class path
                String classPathResource = "/com/belman/images/" + photoPath;
                var inputStream = getClass().getResourceAsStream(classPathResource);
                if (inputStream != null) {
                    return new Image(inputStream);
                }

                // If all else fails, load a fallback image
                System.err.println("Could not find image at path: " + photoPath);
                return loadFallbackImage();
            }
        } catch (Exception e) {
            // Log the error and return fallback image
            System.err.println("Error loading photo as image: " + e.getMessage());
            return loadFallbackImage();
        }
    }

    /**
     * Loads a fallback image when the requested image cannot be found.
     * 
     * @return a fallback image
     */
    private Image loadFallbackImage() {
        try {
            // First try to load a placeholder image from the classpath
            var inputStream = getClass().getResourceAsStream("/com/belman/images/placeholder.png");
            if (inputStream != null) {
                return new Image(inputStream);
            }

            // Try to load any image from mock camera directory
            File mockCameraDir = new File("src/main/resources/mock/camera");
            if (mockCameraDir.exists() && mockCameraDir.isDirectory()) {
                File[] imageFiles = mockCameraDir.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".jpg") || 
                    name.toLowerCase().endsWith(".png") || 
                    name.toLowerCase().endsWith(".gif"));

                if (imageFiles != null && imageFiles.length > 0) {
                    return new Image(imageFiles[0].toURI().toString(), true);
                }
            }

            // If all else fails, create a simple placeholder image
            System.err.println("Creating empty placeholder image");
            return new Image("https://via.placeholder.com/150x150.png?text=No+Image");
        } catch (Exception e) {
            System.err.println("Error loading fallback image: " + e.getMessage());
            // Return null as last resort
            return null;
        }
    }
}
