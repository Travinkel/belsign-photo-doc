package com.belman.presentation.views.qadashboard;

import com.belman.presentation.core.BaseViewModel;
import com.belman.application.core.Inject;
import com.belman.presentation.navigation.Router;
import com.belman.domain.aggregates.Order;
import com.belman.domain.repositories.OrderRepository;
import com.belman.infrastructure.service.SessionManager;
import com.belman.presentation.views.login.LoginView;
import com.belman.presentation.views.photoreview.PhotoReviewView;
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
    @Inject
    private OrderRepository orderRepository;

    private final SessionManager sessionManager = SessionManager.getInstance();

    private final StringProperty welcomeMessage = new SimpleStringProperty("Welcome to QA Dashboard");
    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    private final ObservableList<String> pendingOrders = FXCollections.observableArrayList();
    private final FilteredList<String> filteredOrders = new FilteredList<>(pendingOrders);

    private String selectedOrder;

    @Override
    public void onShow() {
        // Update welcome message with user name if available
        sessionManager.getCurrentUser().ifPresent(user -> {
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
            // In a real implementation, this would filter orders by status
            // For now, we'll just load all orders
            List<Order> orders = orderRepository.findAll();

            // Convert to order numbers for display
            List<String> orderNumbers = orders.stream()
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
     * Filters orders based on the search text.
     */
    public void searchOrders() {
        filterOrders();
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
     * Generates a report for the selected order.
     * 
     * @param orderNumber the order number to generate a report for
     * @return true if the report was generated successfully, false otherwise
     */
    public boolean generateReport(String orderNumber) {
        try {
            // In a real implementation, this would call the report service
            // For now, we'll just simulate success
            return true;
        } catch (Exception e) {
            errorMessage.set("Error generating report: " + e.getMessage());
            return false;
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
     * Gets the error message property.
     * 
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
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
