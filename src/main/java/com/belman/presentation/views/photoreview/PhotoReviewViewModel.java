package com.belman.presentation.views.photoreview;

import com.belman.presentation.core.BaseViewModel;
import com.belman.application.core.Inject;
import com.belman.presentation.navigation.Router;
import com.belman.domain.aggregates.User;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.services.PhotoService;
import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.valueobjects.OrderNumber;
import com.belman.domain.valueobjects.Timestamp;
import com.belman.infrastructure.service.SessionManager;
import com.belman.presentation.views.login.LoginView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * View model for the photo review view.
 */
public class PhotoReviewViewModel extends BaseViewModel<PhotoReviewViewModel> {

    @Inject
    private PhotoService photoService;

    @Inject
    private OrderRepository orderRepository;

    private final SessionManager sessionManager = SessionManager.getInstance();

    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty orderInfo = new SimpleStringProperty("No order selected");
    private final StringProperty commentText = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    private final BooleanProperty orderSelected = new SimpleBooleanProperty(false);
    private final BooleanProperty photoSelected = new SimpleBooleanProperty(false);

    private final ObjectProperty<OrderAggregate> selectedOrder = new SimpleObjectProperty<>();
    private final ObjectProperty<PhotoDocument> selectedPhoto = new SimpleObjectProperty<>();

    private final ListProperty<PhotoDocument> photos = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Override
    public void onShow() {
        // Clear any previous data
        clearForm();
    }

    /**
     * Searches for an order by its number.
     * 
     * @param orderNumberStr the order number to search for
     * @return true if the order was found, false otherwise
     */
    public boolean searchOrder(String orderNumberStr) {
        if (orderNumberStr == null || orderNumberStr.isBlank()) {
            errorMessage.set("Please enter an order number");
            return false;
        }

        try {
            OrderNumber orderNum = new OrderNumber(orderNumberStr);

            // In a real application, you would search by OrderNumber
            // For simplicity, we'll just get all orderAggregates and find the one with the matching number
            List<OrderAggregate> orderAggregates = orderRepository.findAll();
            for (OrderAggregate orderAggregate : orderAggregates) {
                if (orderAggregate.getOrderNumber() != null && orderAggregate.getOrderNumber().equals(orderNum)) {
                    selectedOrder.set(orderAggregate);
                    orderSelected.set(true);
                    orderInfo.set("OrderAggregate: " + orderNumberStr + " - Customer: " +
                                 (orderAggregate.getCustomer() != null ? orderAggregate.getCustomer().getName() : "N/A"));

                    // Load photos for this orderAggregate
                    loadPhotosForOrder(orderAggregate.getId());

                    return true;
                }
            }

            errorMessage.set("OrderAggregate not found: " + orderNumberStr);
            return false;
        } catch (IllegalArgumentException e) {
            errorMessage.set("Invalid order number format");
            return false;
        }
    }

    /**
     * Loads photos for the specified order.
     * 
     * @param orderId the ID of the order
     */
    private void loadPhotosForOrder(OrderId orderId) {
        List<PhotoDocument> orderPhotos = photoService.getPhotosForOrder(orderId);
        photos.setAll(orderPhotos);
    }

    /**
     * Sets the selected photo.
     * 
     * @param photo the selected photo
     */
    public void setSelectedPhoto(PhotoDocument photo) {
        if (photo != null) {
            selectedPhoto.set(photo);
            photoSelected.set(true);

            // Pre-fill comment if it exists
            if (photo.getReviewComment() != null) {
                commentText.set(photo.getReviewComment());
            } else {
                commentText.set("");
            }
        }
    }

    /**
     * Approves the selected photo.
     * 
     * @return true if the photo was approved successfully, false otherwise
     */
    public boolean approvePhoto() {
        if (!photoSelected.get()) {
            errorMessage.set("Please select a photo first");
            return false;
        }

        try {
            PhotoDocument photo = selectedPhoto.get();

            // Get the current user and timestamp
            User currentUser = sessionManager.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not logged in"));
            Timestamp now = Timestamp.now();

            // Approve the photo
            photo.approve(currentUser, now);

            // Save the updated photo (this would typically be done through a repository)
            // For now, we'll just refresh the photos list
            loadPhotosForOrder(selectedOrder.get().getId());

            // Clear the selection
            clearPhotoSelection();

            return true;
        } catch (Exception e) {
            errorMessage.set("Error approving photo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Rejects the selected photo.
     * 
     * @return true if the photo was rejected successfully, false otherwise
     */
    public boolean rejectPhoto() {
        if (!photoSelected.get()) {
            errorMessage.set("Please select a photo first");
            return false;
        }

        if (commentText.get() == null || commentText.get().isBlank()) {
            errorMessage.set("Please provide a reason for rejection");
            return false;
        }

        try {
            PhotoDocument photo = selectedPhoto.get();

            // Get the current user and timestamp
            User currentUser = sessionManager.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not logged in"));
            Timestamp now = Timestamp.now();

            // Reject the photo with comment
            photo.reject(currentUser, now, commentText.get());

            // Refresh the photos list
            loadPhotosForOrder(selectedOrder.get().getId());

            // Clear the selection
            clearPhotoSelection();

            return true;
        } catch (Exception e) {
            errorMessage.set("Error rejecting photo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clears the photo selection.
     */
    private void clearPhotoSelection() {
        selectedPhoto.set(null);
        photoSelected.set(false);
        commentText.set("");
    }

    /**
     * Clears the form.
     */
    public void clearForm() {
        orderNumber.set("");
        orderInfo.set("No order selected");
        commentText.set("");
        errorMessage.set("");
        orderSelected.set(false);
        photoSelected.set(false);
        selectedOrder.set(null);
        selectedPhoto.set(null);
        photos.clear();
    }

    // Getters for properties

    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    public StringProperty orderInfoProperty() {
        return orderInfo;
    }

    public StringProperty commentTextProperty() {
        return commentText;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public BooleanProperty orderSelectedProperty() {
        return orderSelected;
    }

    public BooleanProperty photoSelectedProperty() {
        return photoSelected;
    }

    public ObjectProperty<PhotoDocument> selectedPhotoProperty() {
        return selectedPhoto;
    }

    public ListProperty<PhotoDocument> photosProperty() {
        return photos;
    }

    public ObservableList<PhotoDocument> getPhotos() {
        return photos.get();
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    public void logout() {
        try {
            // Log out the user
            if (sessionManager != null) {
                sessionManager.logout();
            }

            // Navigate to the login view
            Router.navigateTo(LoginView.class);
        } catch (Exception e) {
            errorMessage.set("Error logging out: " + e.getMessage());
        }
    }
}
