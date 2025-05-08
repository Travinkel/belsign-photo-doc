package com.belman.presentation.views.photoreview;

import com.belman.business.richbe.common.Timestamp;
import com.belman.business.richbe.order.OrderAggregate;
import com.belman.business.richbe.order.OrderId;
import com.belman.business.richbe.order.OrderNumber;
import com.belman.business.richbe.user.UserReference;
import com.belman.presentation.core.BaseViewModel;
import com.belman.business.core.Inject;
import com.belman.presentation.navigation.Router;
import com.belman.business.richbe.order.photo.PhotoDocument;
import com.belman.business.richbe.order.OrderRepository;
import com.belman.business.richbe.services.PhotoService;

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
        clearForm();
    }

    /**
     * Searches for an order by its number.
     * 
     * @param orderNumberStr the order number to search for
     * @return true if the order was found, false otherwise
     */
    public boolean searchOrder(String orderNumberStr) {
        if (isNullOrEmpty(orderNumberStr)) {
            setErrorMessage("Please enter an order number");
            return false;
        }

        try {
            OrderNumber orderNum = new OrderNumber(orderNumberStr);
            List<OrderAggregate> orderAggregates = orderRepository.findAll();

            return orderAggregates.stream()
                .filter(order -> order.getOrderNumber() != null && order.getOrderNumber().equals(orderNum))
                .findFirst()
                .map(this::handleOrderFound)
                .orElseGet(() -> {
                    setErrorMessage("Order not found: " + orderNumberStr);
                    return false;
                });
        } catch (IllegalArgumentException e) {
            setErrorMessage("Invalid order number format");
            return false;
        }
    }

    private boolean handleOrderFound(OrderAggregate orderAggregate) {
        selectedOrder.set(orderAggregate);
        orderSelected.set(true);
        orderInfo.set("Order: " + orderAggregate.getOrderNumber().value() + " - Customer: " +
                      (orderAggregate.getCustomerId() != null ? orderAggregate.getCustomerId() : "N/A"));
        loadPhotosForOrder(orderAggregate.getId());
        return true;
    }

    private void loadPhotosForOrder(OrderId orderId) {
        try {
            List<PhotoDocument> orderPhotos = photoService.getPhotosForOrder(orderId);
            photos.setAll(orderPhotos);
        } catch (Exception e) {
            setErrorMessage("Error loading photos: " + e.getMessage());
        }
    }

    public void setSelectedPhoto(PhotoDocument photo) {
        if (photo != null) {
            selectedPhoto.set(photo);
            photoSelected.set(true);
            commentText.set(photo.getReviewComment() != null ? photo.getReviewComment() : "");
        }
    }

    public boolean approvePhoto() {
        if (!validatePhotoSelection()) return false;

        try {
            PhotoDocument photo = selectedPhoto.get();
            UserReference currentUser = getCurrentUser();
            photo.approve(currentUser, Timestamp.now());
            refreshPhotos();
            clearPhotoSelection();
            return true;
        } catch (Exception e) {
            setErrorMessage("Error approving photo: " + e.getMessage());
            return false;
        }
    }

    public boolean rejectPhoto() {
        if (!validatePhotoSelection()) return false;

        if (isNullOrEmpty(commentText.get())) {
            setErrorMessage("Please provide a reason for rejection");
            return false;
        }

        try {
            PhotoDocument photo = selectedPhoto.get();
            UserReference currentUser = getCurrentUser();
            photo.reject(currentUser, Timestamp.now(), commentText.get());
            refreshPhotos();
            clearPhotoSelection();
            return true;
        } catch (Exception e) {
            setErrorMessage("Error rejecting photo: " + e.getMessage());
            return false;
        }
    }

    private boolean validatePhotoSelection() {
        if (!photoSelected.get()) {
            setErrorMessage("Please select a photo first");
            return false;
        }
        return true;
    }

    private UserReference getCurrentUser() {
        return sessionManager.getCurrentUser()
                .map(user -> new UserReference(user.getId(), user.getUsername()))
                .orElseThrow(() -> new IllegalStateException("User not logged in"));
    }

    private void refreshPhotos() {
        loadPhotosForOrder(selectedOrder.get().getId());
    }

    private void clearPhotoSelection() {
        selectedPhoto.set(null);
        photoSelected.set(false);
        commentText.set("");
    }

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

    protected void setErrorMessage(String message) {
        errorMessage.set(message);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isBlank();
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

    public void logout() {
        try {
            sessionManager.logout();
            Router.navigateTo(LoginView.class);
        } catch (Exception e) {
            setErrorMessage("Error logging out: " + e.getMessage());
        }
    }
}
