package com.belman.presentation.views.photoupload;

import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.di.Inject;
import com.belman.domain.aggregates.Order;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.repositories.OrderRepository;
import com.belman.domain.services.PhotoService;
import com.belman.domain.valueobjects.ImagePath;
import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.valueobjects.OrderNumber;
import com.belman.domain.valueobjects.PhotoAngle;
import com.belman.domain.valueobjects.PhotoId;
import com.belman.infrastructure.service.SessionManager;
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
import java.util.UUID;

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
    
    private final ObjectProperty<Order> selectedOrder = new SimpleObjectProperty<>();
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
            // For simplicity, we'll just get all orders and find the one with the matching number
            List<Order> orders = orderRepository.findAll();
            for (Order order : orders) {
                if (order.getOrderNumber() != null && order.getOrderNumber().equals(orderNum)) {
                    selectedOrder.set(order);
                    orderSelected.set(true);
                    orderInfo.set("Order: " + orderNumberStr + " - Customer: " + 
                                 (order.getCustomer() != null ? order.getCustomer().getName() : "N/A"));
                    
                    // Load photos for this order
                    loadPhotosForOrder(order.getId());
                    
                    return true;
                }
            }
            
            errorMessage.set("Order not found: " + orderNumberStr);
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
            double angle = Double.parseDouble(photoAngle.get());
            PhotoAngle photoAngleObj = new PhotoAngle(angle);
            
            PhotoDocument photo = photoService.uploadPhoto(
                selectedPhotoFile.get(),
                selectedOrder.get().getId(),
                photoAngleObj,
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
}