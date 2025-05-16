package com.belman.ui.usecases.photo.review;

import com.belman.common.di.Inject;
import com.belman.domain.common.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.services.PhotoService;
import com.belman.domain.user.UserReference;
import com.belman.ui.session.SessionManager;
import com.belman.ui.base.BaseViewModel;
import com.belman.ui.navigation.Router;
import com.belman.ui.usecases.authentication.login.LoginView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * View model for the photo review view.
 */
public class PhotoReviewViewModel extends BaseViewModel<PhotoReviewViewModel> {

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty orderInfo = new SimpleStringProperty("No order selected");
    private final StringProperty commentText = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty orderSelected = new SimpleBooleanProperty(false);
    private final BooleanProperty photoSelected = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> selectedOrder = new SimpleObjectProperty<>();
    private final ObjectProperty<PhotoDocument> selectedPhoto = new SimpleObjectProperty<>();
    private final ListProperty<PhotoDocument> photos = new SimpleListProperty<>(FXCollections.observableArrayList());
    @Inject
    private PhotoService photoService;
    @Inject
    private OrderRepository orderRepository;

    @Override
    public void onShow() {
        clearForm();
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

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    protected void setErrorMessage(String message) {
        errorMessage.set(message);
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
            List<OrderBusiness> orderBusinesses = orderRepository.findAll();

            return orderBusinesses.stream()
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

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isBlank();
    }

    private boolean handleOrderFound(OrderBusiness orderBusiness) {
        selectedOrder.set(orderBusiness);
        orderSelected.set(true);
        orderInfo.set("Order: " + orderBusiness.getOrderNumber().value() + " - Customer: " +
                      (orderBusiness.getCustomerId() != null ? orderBusiness.getCustomerId() : "N/A"));
        loadPhotosForOrder(orderBusiness.getId());
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

    // Getters for properties

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

    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    public StringProperty orderInfoProperty() {
        return orderInfo;
    }

    public StringProperty commentTextProperty() {
        return commentText;
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