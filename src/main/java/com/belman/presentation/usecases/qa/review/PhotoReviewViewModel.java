package com.belman.presentation.usecases.qa.review;

import com.belman.application.usecase.qa.QAService;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.photo.PhotoAnnotation;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.user.UserReference;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * ViewModel for the photo review view.
 * Provides data and operations for reviewing photos of an order.
 */
public class PhotoReviewViewModel extends BaseViewModel<PhotoReviewViewModel> {
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty orderInfo = new SimpleStringProperty("No order selected");
    private final StringProperty comment = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final ObservableList<PhotoDocument> photos = FXCollections.observableArrayList();

    // Zoom properties
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(1.0);
    private final BooleanProperty panEnabled = new SimpleBooleanProperty(true);

    // Annotation properties
    private final BooleanProperty annotationMode = new SimpleBooleanProperty(false);
    private final ObjectProperty<PhotoAnnotation.AnnotationType> selectedAnnotationType = 
            new SimpleObjectProperty<>(PhotoAnnotation.AnnotationType.NOTE);
    private final ObservableList<PhotoAnnotation> annotations = FXCollections.observableArrayList();
    private final ObjectProperty<PhotoAnnotation> selectedAnnotation = new SimpleObjectProperty<>();

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private PhotoRepository photoRepository;

    @Inject
    private SessionContext sessionContext;

    @Inject
    private QAService qaService;

    private OrderBusiness currentOrder;
    private PhotoDocument selectedPhoto;

    /**
     * Default constructor for use by the ViewLoader.
     */
    public PhotoReviewViewModel() {
        // Default constructor
    }

    @Override
    public void onShow() {
        // Load the order if an order number was passed as a parameter
        String orderNumberStr = Router.getParameter("orderNumber");
        if (orderNumberStr != null) {
            loadOrder(orderNumberStr);
        }
    }

    /**
     * Loads an order by its order number.
     *
     * @param orderNumberStr the order number as a string
     */
    public void loadOrder(String orderNumberStr) {
        try {
            orderNumber.set(orderNumberStr);

            // Find the order by order number
            Optional<OrderBusiness> orderOpt = orderRepository.findByOrderNumber(new OrderNumber(orderNumberStr));

            if (orderOpt.isPresent()) {
                currentOrder = orderOpt.get();

                // Update order info
                orderInfo.set("Order: " + orderNumberStr + " - " + 
                             (currentOrder.getProductDescription() != null ? 
                              currentOrder.getProductDescription().toString() : "No description"));

                // Load photos
                List<PhotoDocument> orderPhotos = photoRepository.findByOrderId(currentOrder.getId());
                photos.setAll(orderPhotos);
            } else {
                errorMessage.set("Order not found: " + orderNumberStr);
            }
        } catch (Exception e) {
            errorMessage.set("Error loading order: " + e.getMessage());
        }
    }

    /**
     * Selects a photo for review and loads its annotations.
     *
     * @param photo the photo to select
     */
    public void selectPhoto(PhotoDocument photo) {
        this.selectedPhoto = photo;
        refreshAnnotations();
    }


    /**
     * Approves the currently selected photo.
     * 
     * @return true if the photo was approved successfully, false otherwise
     */
    public boolean approveSelectedPhoto() {
        try {
            if (selectedPhoto == null) {
                errorMessage.set("No photo selected");
                return false;
            }

            // Get the current user
            Optional<UserReference> userOpt = getUserReference();
            if (userOpt.isEmpty()) {
                errorMessage.set("No user is logged in");
                return false;
            }

            // Only approve if the photo is in PENDING status
            if (selectedPhoto.getStatus() != PhotoDocument.ApprovalStatus.PENDING) {
                errorMessage.set("Photo is already " + selectedPhoto.getStatus().name().toLowerCase());
                return false;
            }

            // Approve the photo
            selectedPhoto.approve(userOpt.get(), new Timestamp(Instant.now()));

            // Save the photo
            photoRepository.save(selectedPhoto);

            // Refresh the photo list to update the UI
            refreshPhotoList();

            return true;
        } catch (Exception e) {
            errorMessage.set("Error approving photo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Rejects the currently selected photo with the provided comment.
     * 
     * @return true if the photo was rejected successfully, false otherwise
     */
    public boolean rejectSelectedPhoto() {
        try {
            if (selectedPhoto == null) {
                errorMessage.set("No photo selected");
                return false;
            }

            // Get the current user
            Optional<UserReference> userOpt = getUserReference();
            if (userOpt.isEmpty()) {
                errorMessage.set("No user is logged in");
                return false;
            }

            // Ensure a comment is provided for rejection
            String rejectionComment = comment.get();
            if (rejectionComment == null || rejectionComment.trim().isEmpty()) {
                errorMessage.set("A comment is required when rejecting a photo");
                return false;
            }

            // Only reject if the photo is in PENDING status
            if (selectedPhoto.getStatus() != PhotoDocument.ApprovalStatus.PENDING) {
                errorMessage.set("Photo is already " + selectedPhoto.getStatus().name().toLowerCase());
                return false;
            }

            // Reject the photo
            selectedPhoto.reject(userOpt.get(), new Timestamp(Instant.now()), rejectionComment);

            // Save the photo
            photoRepository.save(selectedPhoto);

            // Refresh the photo list to update the UI
            refreshPhotoList();

            return true;
        } catch (Exception e) {
            errorMessage.set("Error rejecting photo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets a UserReference from the current session.
     * 
     * @return an Optional containing the UserReference if available, or empty if not
     */
    private Optional<UserReference> getUserReference() {
        return sessionContext.getUser().map(user -> new UserReference(user.getId(), user.getUsername()));
    }

    /**
     * Refreshes the photo list to reflect any changes in the photos.
     */
    private void refreshPhotoList() {
        if (currentOrder != null) {
            List<PhotoDocument> orderPhotos = photoRepository.findByOrderId(currentOrder.getId());
            photos.setAll(orderPhotos);
        }
    }

    /**
     * Approves the current order and navigates to the approval summary view.
     * If any photos are still pending, they will be automatically approved.
     */
    public void approveOrder() {
        try {
            if (currentOrder == null) {
                errorMessage.set("No order selected");
                return;
            }

            // Get the current user
            Optional<UserReference> userOpt = getUserReference();
            if (userOpt.isEmpty()) {
                errorMessage.set("No user is logged in");
                return;
            }

            // Approve any pending photos
            boolean allPhotosProcessed = true;
            List<PhotoDocument> orderPhotos = photoRepository.findByOrderId(currentOrder.getId());
            for (PhotoDocument photo : orderPhotos) {
                if (photo.getStatus() == PhotoDocument.ApprovalStatus.PENDING) {
                    try {
                        photo.approve(userOpt.get(), new Timestamp(Instant.now()));
                        photoRepository.save(photo);
                    } catch (Exception e) {
                        allPhotosProcessed = false;
                        errorMessage.set("Error approving photo: " + e.getMessage());
                    }
                }
            }

            if (!allPhotosProcessed) {
                return;
            }

            // Update order status to APPROVED
            currentOrder.setStatus(OrderStatus.APPROVED);

            // Save the order
            orderRepository.save(currentOrder);

            // Navigate to the approval summary view
            Map<String, Object> params = new HashMap<>();
            params.put("orderNumber", orderNumber.get());
            params.put("approved", true);
            params.put("comment", comment.get());

            Router.navigateTo(com.belman.presentation.usecases.qa.summary.ApprovalSummaryView.class, params);
        } catch (Exception e) {
            errorMessage.set("Error approving order: " + e.getMessage());
        }
    }

    /**
     * Rejects the current order and navigates to the approval summary view.
     * If any photos are still pending, they will be automatically rejected with the provided comment.
     */
    public void rejectOrder() {
        try {
            if (currentOrder == null) {
                errorMessage.set("No order selected");
                return;
            }

            // Ensure a comment is provided for rejection
            String rejectionComment = comment.get();
            if (rejectionComment == null || rejectionComment.trim().isEmpty()) {
                errorMessage.set("A comment is required when rejecting an order");
                return;
            }

            // Get the current user
            Optional<UserReference> userOpt = getUserReference();
            if (userOpt.isEmpty()) {
                errorMessage.set("No user is logged in");
                return;
            }

            // Reject any pending photos
            boolean allPhotosProcessed = true;
            List<PhotoDocument> orderPhotos = photoRepository.findByOrderId(currentOrder.getId());
            for (PhotoDocument photo : orderPhotos) {
                if (photo.getStatus() == PhotoDocument.ApprovalStatus.PENDING) {
                    try {
                        photo.reject(userOpt.get(), new Timestamp(Instant.now()), rejectionComment);
                        photoRepository.save(photo);
                    } catch (Exception e) {
                        allPhotosProcessed = false;
                        errorMessage.set("Error rejecting photo: " + e.getMessage());
                    }
                }
            }

            if (!allPhotosProcessed) {
                return;
            }

            // Update order status to REJECTED
            currentOrder.setStatus(OrderStatus.REJECTED);

            // Save the order
            orderRepository.save(currentOrder);

            // Navigate to the approval summary view
            Map<String, Object> params = new HashMap<>();
            params.put("orderNumber", orderNumber.get());
            params.put("approved", false);
            params.put("comment", rejectionComment);

            Router.navigateTo(com.belman.presentation.usecases.qa.summary.ApprovalSummaryView.class, params);
        } catch (Exception e) {
            errorMessage.set("Error rejecting order: " + e.getMessage());
        }
    }

    /**
     * Gets the order number property.
     *
     * @return the order number property
     */
    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    /**
     * Gets the order info property.
     *
     * @return the order info property
     */
    public StringProperty orderInfoProperty() {
        return orderInfo;
    }

    /**
     * Gets the comment property.
     *
     * @return the comment property
     */
    public StringProperty commentProperty() {
        return comment;
    }

    /**
     * Gets the error message property.
     *
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Gets the photos list.
     *
     * @return the photos list
     */
    public ObservableList<PhotoDocument> getPhotos() {
        return photos;
    }

    /**
     * Gets the zoom factor property.
     *
     * @return the zoom factor property
     */
    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    /**
     * Gets the current zoom factor.
     *
     * @return the current zoom factor
     */
    public double getZoomFactor() {
        return zoomFactor.get();
    }

    /**
     * Sets the zoom factor.
     *
     * @param factor the new zoom factor
     */
    public void setZoomFactor(double factor) {
        // Clamp the zoom factor between 1.0 and 5.0
        double clampedFactor = Math.max(1.0, Math.min(5.0, factor));
        zoomFactor.set(clampedFactor);
    }

    /**
     * Increases the zoom factor by the specified amount.
     *
     * @param amount the amount to increase the zoom factor by
     */
    public void zoomIn(double amount) {
        setZoomFactor(getZoomFactor() + amount);
    }

    /**
     * Decreases the zoom factor by the specified amount.
     *
     * @param amount the amount to decrease the zoom factor by
     */
    public void zoomOut(double amount) {
        setZoomFactor(getZoomFactor() - amount);
    }

    /**
     * Resets the zoom factor to 1.0.
     */
    public void resetZoom() {
        zoomFactor.set(1.0);
    }

    /**
     * Gets the pan enabled property.
     *
     * @return the pan enabled property
     */
    public BooleanProperty panEnabledProperty() {
        return panEnabled;
    }

    /**
     * Gets the annotation mode property.
     *
     * @return the annotation mode property
     */
    public BooleanProperty annotationModeProperty() {
        return annotationMode;
    }

    /**
     * Gets the selected annotation type property.
     *
     * @return the selected annotation type property
     */
    public ObjectProperty<PhotoAnnotation.AnnotationType> selectedAnnotationTypeProperty() {
        return selectedAnnotationType;
    }

    /**
     * Gets the annotations list.
     *
     * @return the annotations list
     */
    public ObservableList<PhotoAnnotation> getAnnotations() {
        return annotations;
    }

    /**
     * Gets the selected annotation property.
     *
     * @return the selected annotation property
     */
    public ObjectProperty<PhotoAnnotation> selectedAnnotationProperty() {
        return selectedAnnotation;
    }

    /**
     * Creates a new annotation at the specified coordinates.
     *
     * @param x the x-coordinate (as percentage of image width, 0.0-1.0)
     * @param y the y-coordinate (as percentage of image height, 0.0-1.0)
     * @param text the text content of the annotation
     * @return the created annotation, or null if creation failed
     */
    public PhotoAnnotation createAnnotation(double x, double y, String text) {
        try {
            if (selectedPhoto == null) {
                errorMessage.set("No photo selected");
                return null;
            }

            // Create a new annotation using the QA service
            PhotoAnnotation annotation = qaService.createAnnotation(
                    selectedPhoto.getId(),
                    x,
                    y,
                    text,
                    selectedAnnotationType.get()
            );

            if (annotation == null) {
                errorMessage.set("Failed to add annotation to photo");
                return null;
            }

            // Refresh the annotations list
            refreshAnnotations();

            return annotation;
        } catch (Exception e) {
            errorMessage.set("Error creating annotation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates an existing annotation.
     *
     * @param annotation the annotation to update
     * @return true if the annotation was updated successfully, false otherwise
     */
    public boolean updateAnnotation(PhotoAnnotation annotation) {
        try {
            if (selectedPhoto == null) {
                errorMessage.set("No photo selected");
                return false;
            }

            // Update the annotation using the QA service
            boolean updated = qaService.updateAnnotation(selectedPhoto.getId(), annotation);
            if (!updated) {
                errorMessage.set("Failed to update annotation");
                return false;
            }

            // Refresh the annotations list
            refreshAnnotations();

            return true;
        } catch (Exception e) {
            errorMessage.set("Error updating annotation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes the specified annotation.
     *
     * @param annotation the annotation to delete
     * @return true if the annotation was deleted successfully, false otherwise
     */
    public boolean deleteAnnotation(PhotoAnnotation annotation) {
        try {
            if (selectedPhoto == null) {
                errorMessage.set("No photo selected");
                return false;
            }

            // Remove the annotation using the QA service
            boolean removed = qaService.deleteAnnotation(selectedPhoto.getId(), annotation.getId());
            if (!removed) {
                errorMessage.set("Failed to remove annotation");
                return false;
            }

            // Refresh the annotations list
            refreshAnnotations();

            return true;
        } catch (Exception e) {
            errorMessage.set("Error deleting annotation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Refreshes the annotations list from the selected photo.
     */
    private void refreshAnnotations() {
        if (selectedPhoto != null) {
            annotations.setAll(qaService.getAnnotations(selectedPhoto.getId()));
        } else {
            annotations.clear();
        }
    }


    /**
     * Navigates back to the previous view.
     */
    public void navigateBack() {
        Router.navigateBack();
    }
}
