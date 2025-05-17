package com.belman.presentation.usecases.worker.summary;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.application.usecase.order.OrderService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.completed.CompletedView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * ViewModel for the SummaryView.
 * Manages the state and logic for displaying a summary of all photos taken for an order.
 */
public class SummaryViewModel extends BaseViewModel<SummaryViewModel> {

    @Inject
    private OrderService orderService;

    // Properties for UI binding
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Review your photos before submission");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> currentOrder = new SimpleObjectProperty<>();
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final ListProperty<PhotoDocument> takenPhotos = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty photosCount = new SimpleIntegerProperty(0);

    @Override
    public void onShow() {
        // Load the current order and its photos from the worker flow context
        loadOrderAndPhotos();
    }

    /**
     * Loads the current order and its photos from the worker flow context.
     */
    private void loadOrderAndPhotos() {
        // Get the current order from the worker flow context
        OrderBusiness order = WorkerFlowContext.getCurrentOrder();
        if (order == null) {
            errorMessage.set("No order is currently loaded.");
            return;
        }

        currentOrder.set(order);
        orderNumber.set(order.getOrderNumber().toString());

        // Get the taken photos from the worker flow context
        List<PhotoDocument> photos = WorkerFlowContext.getTakenPhotos();
        takenPhotos.setAll(photos);
        photosCount.set(photos.size());

        if (photos.isEmpty()) {
            errorMessage.set("No photos have been taken for this order.");
        } else {
            statusMessage.set("Review your photos before submission. " + photos.size() + " photos taken.");
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

                        // Store the order number in the worker flow context for the completed view
                        WorkerFlowContext.setAttribute("completedOrderNumber", order.getOrderNumber().toString());

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

    public ListProperty<PhotoDocument> takenPhotosProperty() {
        return takenPhotos;
    }

    public IntegerProperty photosCountProperty() {
        return photosCount;
    }
}
