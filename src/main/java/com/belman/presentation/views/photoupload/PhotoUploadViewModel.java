package com.belman.presentation.views.photoupload;

import com.belman.presentation.core.BaseViewModel;
import com.belman.business.core.Inject;
import com.belman.presentation.navigation.Router;
import com.belman.business.domain.order.photo.PhotoDocument;
import com.belman.business.domain.order.OrderRepository;
import com.belman.business.domain.order.OrderAggregate;
import com.belman.business.domain.order.OrderId;
import com.belman.business.domain.order.OrderNumber;
import com.belman.business.domain.order.photo.PhotoTemplate;
import com.belman.business.domain.services.PhotoService;
import com.belman.data.service.SessionManager;
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

import java.io.File;
import java.util.List;

/**
 * View model for the photo upload view.
 */
public class PhotoUploadViewModel extends BaseViewModel<PhotoUploadViewModel> {

    @Inject
    private PhotoService photoService;

    @Inject
    private OrderRepository orderRepository;

    private final SessionManager sessionManager = SessionManager.getInstance();

    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty orderInfo = new SimpleStringProperty("No order selected");
    private final StringProperty photoAngle = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    private final BooleanProperty orderSelected = new SimpleBooleanProperty(false);
    private final BooleanProperty photoSelected = new SimpleBooleanProperty(false);

    private final ObjectProperty<OrderAggregate> selectedOrder = new SimpleObjectProperty<>();
    private final ObjectProperty<File> selectedPhotoFile = new SimpleObjectProperty<>();

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
                    orderInfo.set("OrderAggregate: " + orderNumberStr + " - Customer ID: " +
                                 (orderAggregate.getCustomerId() != null ? orderAggregate.getCustomerId().id() : "N/A"));

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
     * Sets the selected photo file.
     * 
     * @param file the selected photo file
     */
    public void setSelectedPhotoFile(File file) {
        if (file != null) {
            selectedPhotoFile.set(file);
            photoSelected.set(true);
        }
    }

    /**
     * Uploads the selected photo.
     * 
     * @return true if the photo was uploaded successfully, false otherwise
     */
    public boolean uploadPhoto() {
        if (!orderSelected.get()) {
            errorMessage.set("Please select an order first");
            return false;
        }

        if (!photoSelected.get()) {
            errorMessage.set("Please select a photo first");
            return false;
        }

        if (photoAngle.get() == null || photoAngle.get().isBlank()) {
            errorMessage.set("Please enter a photo angle");
            return false;
        }

        try {
            // Use a predefined template based on the angle input
            // For simplicity, we'll use ANGLED_VIEW_OF_JOINT for any angle
            PhotoTemplate photoTemplate = PhotoTemplate.ANGLED_VIEW_OF_JOINT;

            PhotoDocument photo = photoService.uploadPhoto(
                selectedPhotoFile.get(),
                selectedOrder.get().getId(),
                photoTemplate,
                sessionManager.getCurrentUser().orElseThrow(() -> new IllegalStateException("User not logged in"))
            );

            // Refresh the photos list
            loadPhotosForOrder(selectedOrder.get().getId());

            // Clear the photo selection
            selectedPhotoFile.set(null);
            photoSelected.set(false);
            photoAngle.set("");

            return true;
        } catch (NumberFormatException e) {
            errorMessage.set("Invalid angle format. Please enter a number.");
            return false;
        } catch (IllegalArgumentException e) {
            errorMessage.set("Invalid angle value: " + e.getMessage());
            return false;
        } catch (Exception e) {
            errorMessage.set("Error uploading photo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes the specified photo.
     * 
     * @param photo the photo to delete
     * @return true if the photo was deleted successfully, false otherwise
     */
    public boolean deletePhoto(PhotoDocument photo) {
        if (photo == null) {
            errorMessage.set("No photo selected");
            return false;
        }

        try {
            boolean deleted = photoService.deletePhoto(photo.getPhotoId());
            if (deleted) {
                // Refresh the photos list
                loadPhotosForOrder(selectedOrder.get().getId());
                return true;
            } else {
                errorMessage.set("Failed to delete photo");
                return false;
            }
        } catch (Exception e) {
            errorMessage.set("Error deleting photo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clears the form.
     */
    public void clearForm() {
        orderNumber.set("");
        orderInfo.set("No order selected");
        photoAngle.set("");
        errorMessage.set("");
        orderSelected.set(false);
        photoSelected.set(false);
        selectedOrder.set(null);
        selectedPhotoFile.set(null);
        photos.clear();
    }

    // Getters for properties

    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    public StringProperty orderInfoProperty() {
        return orderInfo;
    }

    public StringProperty photoAngleProperty() {
        return photoAngle;
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
