package com.belman.presentation.usecases.qa.review;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoRepository;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private PhotoRepository photoRepository;

    @Inject
    private SessionContext sessionContext;

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
     * Selects a photo for review.
     *
     * @param photo the photo to select
     */
    public void selectPhoto(PhotoDocument photo) {
        this.selectedPhoto = photo;
    }

    /**
     * Approves the current order and navigates to the approval summary view.
     */
    public void approveOrder() {
        try {
            if (currentOrder == null) {
                errorMessage.set("No order selected");
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
     */
    public void rejectOrder() {
        try {
            if (currentOrder == null) {
                errorMessage.set("No order selected");
                return;
            }

            // Ensure a comment is provided for rejection
            if (comment.get() == null || comment.get().trim().isEmpty()) {
                errorMessage.set("A comment is required when rejecting an order");
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
            params.put("comment", comment.get());

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
     * Navigates back to the previous view.
     */
    public void navigateBack() {
        Router.navigateBack();
    }
}
