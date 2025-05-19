package com.belman.presentation.usecases.worker.photocube;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.PhotoService;
import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.CameraService;
import com.belman.application.usecase.worker.WorkerService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.*;

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

    private final CameraService cameraService = ServiceLocator.getService(CameraService.class);

    // Properties for UI binding
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Loading...");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> currentOrder = new SimpleObjectProperty<>();
    private final StringProperty orderNumber = new SimpleStringProperty("");

    // Properties for the unfolded cube layout
    private final ObjectProperty<PhotoTemplate> selectedTemplate = new SimpleObjectProperty<>();
    private final MapProperty<PhotoTemplate, Boolean> templateCompletionStatus = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final ListProperty<PhotoDocument> takenPhotos = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty photosCompleted = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPhotosRequired = new SimpleIntegerProperty(0);

    // Templates for the unfolded cube - will be loaded from WorkerService
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

        // Get the current order from the WorkerFlowContext
        OrderBusiness order = WorkerFlowContext.getCurrentOrder();

        if (order == null) {
            // If no order is in the context, try to get one from the session
            SessionContext.getCurrentUser().ifPresentOrElse(
                user -> {
                    // Get all orders and find the first one for the current user
                    List<OrderBusiness> orders = orderService.getAllOrders();

                    // Filter orders for the current user
                    List<OrderBusiness> userOrders = orders.stream()
                        .filter(o -> o.getCreatedBy() != null && 
                               o.getCreatedBy().id().equals(user.getId()))
                        .toList();

                    if (userOrders.isEmpty()) {
                        errorMessage.set("No active order found for the current user.");
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
                },
                () -> {
                    errorMessage.set("No user is logged in.");
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
    }

    /**
     * Loads the photos and templates for the specified order.
     *
     * @param orderId the ID of the order
     */
    private void loadPhotosForOrder(OrderId orderId) {
        statusMessage.set("Loading photos and templates...");

        try {
            // Get all photos for the order
            List<PhotoDocument> photos = photoService.getPhotosForOrder(orderId);
            takenPhotos.setAll(photos);

            // Update the photos completed count
            photosCompleted.set(photos.size());

            // Get available templates from the WorkerService
            requiredTemplates = workerService.getAvailableTemplates(orderId);

            // If no templates are available, use default templates
            if (requiredTemplates.isEmpty()) {
                requiredTemplates = Arrays.asList(
                    PhotoTemplate.TOP_VIEW_OF_JOINT,
                    PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                    PhotoTemplate.BACK_VIEW_OF_ASSEMBLY,
                    PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY,
                    PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY,
                    PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY
                );
                statusMessage.set("Using default templates. No custom templates found for this order.");
            }

            // Set the total required photos count
            totalPhotosRequired.set(requiredTemplates.size());

            // Initialize the template completion status map
            Map<PhotoTemplate, Boolean> completionStatus = new HashMap<>();
            for (PhotoTemplate template : requiredTemplates) {
                boolean isCompleted = photos.stream()
                    .anyMatch(photo -> photo.getTemplate().equals(template));
                completionStatus.put(template, isCompleted);
            }
            templateCompletionStatus.putAll(completionStatus);

            // Check if there's a selected template in the WorkerFlowContext
            PhotoTemplate selectedTemplateFromContext = WorkerFlowContext.getSelectedTemplate();
            if (selectedTemplateFromContext != null) {
                selectedTemplate.set(selectedTemplateFromContext);
            } else if (!requiredTemplates.isEmpty()) {
                // Auto-select the first template if none is selected
                selectedTemplate.set(requiredTemplates.get(0));
                WorkerFlowContext.setSelectedTemplate(requiredTemplates.get(0));
            }

            loading.set(false);
            statusMessage.set("Ready to take photos. " + photosCompleted.get() + " of " + 
                            totalPhotosRequired.get() + " photos taken.");
        } catch (Exception e) {
            errorMessage.set("Error loading photos and templates: " + e.getMessage());
            loading.set(false);
        }
    }

    /**
     * Selects a photo template and navigates to the capture view.
     *
     * @param template the template to select
     */
    public void selectTemplate(PhotoTemplate template) {
        if (!cameraService.isCameraAvailable()) {
            errorMessage.set("Camera is not available on this device.");
            return;
        }

        selectedTemplate.set(template);

        // Store the selected template in the worker flow context for the capture view
        WorkerFlowContext.setSelectedTemplate(template);
        WorkerFlowContext.setCurrentOrder(currentOrder.get());

        // Navigate to the capture view
        Router.navigateTo(com.belman.presentation.usecases.worker.capture.CaptureView.class);
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
        if (!areAllPhotosTaken()) {
            // Get the missing required templates
            List<PhotoTemplate> missingTemplates = getMissingRequiredTemplates();

            if (missingTemplates.isEmpty()) {
                errorMessage.set("Not all required photos have been taken.");
            } else {
                // Build a message with the missing templates
                StringBuilder message = new StringBuilder("Missing required photos for: ");
                for (int i = 0; i < missingTemplates.size(); i++) {
                    if (i > 0) {
                        message.append(", ");
                    }
                    message.append(missingTemplates.get(i).name());
                }
                errorMessage.set(message.toString());
            }
            return;
        }

        // Navigate to the summary view
        Router.navigateTo(com.belman.presentation.usecases.worker.summary.SummaryView.class);
    }

    /**
     * Navigates back to the assigned order view.
     */
    public void goBack() {
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

    public List<PhotoTemplate> getRequiredTemplates() {
        return requiredTemplates;
    }
}
