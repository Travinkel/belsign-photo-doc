package com.belman.presentation.usecases.worker.summary;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.worker.WorkerService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.completed.CompletedView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.List;

/**
 * ViewModel for the SummaryView.
 * Manages the state and logic for displaying a summary of all photos taken for an order.
 */
public class SummaryViewModel extends BaseViewModel<SummaryViewModel> {

    @Inject
    private OrderService orderService;

    @Inject
    private WorkerService workerService;

    // Properties for UI binding
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Review your photos before submission");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> currentOrder = new SimpleObjectProperty<>();
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final ListProperty<PhotoDocument> takenPhotos = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty photosCount = new SimpleIntegerProperty(0);
    private final IntegerProperty totalRequiredPhotosCount = new SimpleIntegerProperty(0);
    private final ListProperty<PhotoTemplate> missingTemplates = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty allRequiredPhotosTaken = new SimpleBooleanProperty(false);

    @Override
    public void onShow() {
        // Load the current order and its photos from the worker flow context
        loadOrderAndPhotos();
    }

    /**
     * Loads the current order and its photos from the worker flow context.
     */
    private void loadOrderAndPhotos() {
        loading.set(true);

        try {
            // Get the current order from the worker flow context
            OrderBusiness order = WorkerFlowContext.getCurrentOrder();
            if (order == null) {
                errorMessage.set("No order is currently loaded.");
                loading.set(false);
                return;
            }

            currentOrder.set(order);
            // Format the order number in a user-friendly way (e.g., "Order #123" instead of technical format)
            String friendlyOrderNumber = order.getOrderNumber().toString().replace("ORD-", "Order #");
            orderNumber.set(friendlyOrderNumber);

            // Get the taken photos from the worker flow context
            List<PhotoDocument> photos = WorkerFlowContext.getTakenPhotos();
            takenPhotos.setAll(photos);
            photosCount.set(photos.size());

            // Check if all required photos have been taken
            boolean allTaken = workerService.hasAllRequiredPhotos(order.getId());
            allRequiredPhotosTaken.set(allTaken);

            // Get the missing templates
            List<PhotoTemplate> missing = workerService.getMissingRequiredTemplates(order.getId());
            missingTemplates.setAll(missing);

            // Get the total required photos count
            int totalRequired = photos.size() + missing.size();
            totalRequiredPhotosCount.set(totalRequired);

            if (photos.isEmpty()) {
                errorMessage.set("No photos have been taken for this order.");
                statusMessage.set("You need to take photos before submitting.");
            } else if (!allTaken) {
                // Build a message with the missing templates
                StringBuilder message = new StringBuilder("Missing required photos: ");
                for (int i = 0; i < missing.size(); i++) {
                    if (i > 0) {
                        message.append(", ");
                    }
                    message.append(com.belman.presentation.providers.PhotoTemplateLabelProvider.getDisplayLabel(missing.get(i)));
                }
                errorMessage.set(message.toString());
                statusMessage.set("Review your photos before submission. " + photos.size() + " of " + 
                                totalRequired + " required photos taken.");
            } else {
                errorMessage.set("");
                statusMessage.set("All required photos have been taken. Ready to submit.");
            }
        } catch (Exception e) {
            errorMessage.set("Error loading photos: " + e.getMessage());
        } finally {
            loading.set(false);
        }
    }

    /**
     * Submits the photos and marks the order as completed.
     */
    public void submitPhotos() {
        if (takenPhotos.isEmpty()) {
            errorMessage.set("No photos to submit.");
            return;
        }

        OrderBusiness order = currentOrder.get();
        if (order == null) {
            errorMessage.set("No order is currently loaded.");
            return;
        }

        // Check if all required photos have been taken
        if (!allRequiredPhotosTaken.get()) {
            // Build a message with the missing templates
            StringBuilder message = new StringBuilder("Cannot submit: Missing required photos for ");
            for (int i = 0; i < missingTemplates.size(); i++) {
                if (i > 0) {
                    message.append(", ");
                }
                message.append(com.belman.presentation.providers.PhotoTemplateLabelProvider.getDisplayLabel(missingTemplates.get(i)));
            }
            errorMessage.set(message.toString());
            return;
        }

        loading.set(true);
        statusMessage.set("Submitting photos...");

        // Get the current user
        SessionContext.getCurrentUser().ifPresentOrElse(
            user -> {
                try {
                    // Mark the order as completed
                    boolean success = orderService.completeOrder(order.getId(), user);

                    if (success) {
                        // Navigate to the completed view
                        loading.set(false);
                        statusMessage.set("Photos submitted successfully!");

                        // Store information in the worker flow context for the completed view
                        WorkerFlowContext.setAttribute("completedOrderNumber", order.getOrderNumber().toString());
                        WorkerFlowContext.setAttribute("completedPhotoCount", photosCount.get());
                        WorkerFlowContext.setAttribute("completedByUsername", user.getUsername().value());
                        WorkerFlowContext.setAttribute("completedTimestamp", java.time.Instant.now().toString());

                        // Short delay before navigating to the completed view
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                                Router.navigateTo(CompletedView.class);
                            } catch (InterruptedException e) {
                                // Ignore
                            }
                        }).start();
                    } else {
                        errorMessage.set("Failed to mark order as completed.");
                        loading.set(false);
                    }
                } catch (Exception e) {
                    errorMessage.set("Error submitting photos: " + e.getMessage());
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
     * Navigates back to the photo cube view.
     */
    public void goBack() {
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

    public ObjectProperty<OrderBusiness> currentOrderProperty() {
        return currentOrder;
    }

    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    public ListProperty<PhotoDocument> takenPhotosProperty() {
        return takenPhotos;
    }

    public IntegerProperty photosCountProperty() {
        return photosCount;
    }
}
