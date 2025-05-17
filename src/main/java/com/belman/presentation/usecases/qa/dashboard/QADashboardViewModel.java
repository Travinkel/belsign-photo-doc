package com.belman.presentation.usecases.qa.dashboard;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.specification.OrderStatusSpecification;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.qa.review.PhotoReviewView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ViewModel for the QA dashboard view.
 * Provides data and operations for QA-specific functionality.
 */
public class QADashboardViewModel extends BaseViewModel<QADashboardViewModel> {
    private final StringProperty welcomeMessage = new SimpleStringProperty("Welcome to QA Dashboard");
    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final ObservableList<String> pendingOrders = FXCollections.observableArrayList();
    private final FilteredList<String> filteredOrders = new FilteredList<>(pendingOrders);

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private SessionContext sessionContext;

    private String selectedOrder;

    /**
     * Default constructor for use by the ViewLoader.
     */
    public QADashboardViewModel() {
        // Default constructor
    }

    @Override
    public void onShow() {
        // Update welcome message with user name if available
        sessionContext.getUser().ifPresent(user -> {
            welcomeMessage.set("Welcome, " + user.getUsername().value() + "!");
        });

        // Load pending orders
        loadPendingOrders();
    }

    /**
     * Loads pending orders that need QA review.
     */
    public void loadPendingOrders() {
        try {
            // Create a specification for orders with status COMPLETED
            OrderStatusSpecification completedOrdersSpec = new OrderStatusSpecification(OrderStatus.COMPLETED);

            // Get orders with status COMPLETED
            List<OrderBusiness> orderBusinesses = orderRepository.findBySpecification(completedOrdersSpec);

            // Convert to order numbers for display
            List<String> orderNumbers = orderBusinesses.stream()
                    .map(order -> order.getOrderNumber().toString())
                    .collect(Collectors.toList());

            pendingOrders.setAll(orderNumbers);

            // Apply any existing search filter
            filterOrders();
        } catch (Exception e) {
            errorMessage.set("Error loading orders: " + e.getMessage());
        }
    }

    /**
     * Filters the orders list based on the search text.
     */
    private void filterOrders() {
        String filter = searchText.get().toLowerCase();

        if (filter == null || filter.isEmpty()) {
            filteredOrders.setPredicate(order -> true);
        } else {
            filteredOrders.setPredicate(order ->
                    order.toLowerCase().contains(filter)
            );
        }
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
     * Filters orders based on the search text.
     */
    public void searchOrders() {
        filterOrders();
    }

    /**
     * Selects an order for further operations.
     *
     * @param orderNumber the order number to select
     */
    public void selectOrder(String orderNumber) {
        this.selectedOrder = orderNumber;
    }

    /**
     * Navigates to the photo review view for the selected order.
     *
     * @param orderNumber the order number to review
     */
    public void navigateToPhotoReview(String orderNumber) {
        try {
            // Create parameters to pass to the photo review view
            Map<String, Object> params = new HashMap<>();
            params.put("orderNumber", orderNumber);

            // Navigate to the photo review view
            Router.navigateTo(PhotoReviewView.class, params);
        } catch (Exception e) {
            errorMessage.set("Error navigating to photo review: " + e.getMessage());
        }
    }

    /**
     * Gets the welcome message property.
     *
     * @return the welcome message property
     */
    public StringProperty welcomeMessageProperty() {
        return welcomeMessage;
    }

    /**
     * Gets the search text property.
     *
     * @return the search text property
     */
    public StringProperty searchTextProperty() {
        return searchText;
    }

    /**
     * Gets the pending orders list.
     *
     * @return the pending orders list
     */
    public ObservableList<String> getPendingOrders() {
        return filteredOrders;
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    public void logout() {
        try {
            // Log out the user
            if (sessionContext != null) {
                sessionContext.navigateToLogin();
            }
        } catch (Exception e) {
            errorMessage.set("Error logging out: " + e.getMessage());
        }
    }
}