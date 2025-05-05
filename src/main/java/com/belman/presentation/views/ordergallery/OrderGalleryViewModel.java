package com.belman.presentation.views.ordergallery;

import com.belman.presentation.core.BaseViewModel;
import com.belman.application.core.Inject;
import com.belman.presentation.navigation.Router;
import com.belman.domain.aggregates.Order;
import com.belman.domain.aggregates.User;
import com.belman.domain.repositories.OrderRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View model for the order gallery view.
 */
public class OrderGalleryViewModel extends BaseViewModel<OrderGalleryViewModel> {

    @Inject
    private OrderRepository orderRepository;

    private final SessionManager sessionManager = SessionManager.getInstance();

    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty orderDetails = new SimpleStringProperty("");

    private final BooleanProperty orderSelected = new SimpleBooleanProperty(false);
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    private final ObjectProperty<Order> selectedOrder = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> fromDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> toDate = new SimpleObjectProperty<>();

    private final ListProperty<Order> orders = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Order> filteredOrders = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Override
    public void onShow() {
        // Load all orders when the view is shown
        loadAllOrders();
    }

    /**
     * Loads all orders from the repository.
     */
    public void loadAllOrders() {
        isLoading.set(true);
        errorMessage.set("");

        try {
            List<Order> allOrders = orderRepository.findAll();
            orders.setAll(allOrders);
            filteredOrders.setAll(allOrders);
            isLoading.set(false);
        } catch (Exception e) {
            errorMessage.set("Error loading orders: " + e.getMessage());
            isLoading.set(false);
        }
    }

    /**
     * Searches for orders based on the search text.
     * The search text can be an order number, customer name, or other order property.
     */
    public void searchOrders() {
        String search = searchText.get();
        if (search == null || search.isBlank()) {
            // If search text is empty, show all orders
            filteredOrders.setAll(orders);
            return;
        }

        // Filter orders based on search text
        List<Order> filtered = orders.stream()
            .filter(order -> {
                // Check if order number contains search text
                if (order.getOrderNumber() != null && 
                    order.getOrderNumber().toString().toLowerCase().contains(search.toLowerCase())) {
                    return true;
                }

                // Check if customer name contains search text
                if (order.getCustomer() != null && 
                    order.getCustomer().getName().toLowerCase().contains(search.toLowerCase())) {
                    return true;
                }

                // Add more search criteria as needed

                return false;
            })
            .collect(Collectors.toList());

        filteredOrders.setAll(filtered);
    }

    /**
     * Filters orders by date range.
     */
    public void filterByDateRange() {
        LocalDate from = fromDate.get();
        LocalDate to = toDate.get();

        if (from == null && to == null) {
            // If no date range is specified, show all orders
            filteredOrders.setAll(orders);
            return;
        }

        // Filter orders based on date range
        List<Order> filtered = orders.stream()
            .filter(order -> {
                LocalDate orderDate = order.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                if (from != null && to != null) {
                    return !orderDate.isBefore(from) && !orderDate.isAfter(to);
                } else if (from != null) {
                    return !orderDate.isBefore(from);
                } else {
                    return !orderDate.isAfter(to);
                }
            })
            .collect(Collectors.toList());

        filteredOrders.setAll(filtered);
    }

    /**
     * Sets the selected order and updates the order details.
     * 
     * @param order the selected order
     */
    public void selectOrder(Order order) {
        if (order != null) {
            selectedOrder.set(order);
            orderSelected.set(true);

            // Update order details
            StringBuilder details = new StringBuilder();
            details.append("Order Number: ").append(order.getOrderNumber()).append("\n");

            if (order.getCustomer() != null) {
                details.append("Customer: ").append(order.getCustomer().getName()).append("\n");
            }

            details.append("Created: ").append(order.getCreatedAt().toString()).append("\n");
            details.append("Status: ").append(order.getStatus()).append("\n");

            // Add more details as needed

            orderDetails.set(details.toString());
        } else {
            selectedOrder.set(null);
            orderSelected.set(false);
            orderDetails.set("");
        }
    }

    /**
     * Creates a new order with the specified order number.
     * 
     * @param orderNumberStr the order number for the new order
     * @return true if the order was created successfully, false otherwise
     */
    public boolean createOrder(String orderNumberStr) {
        if (orderNumberStr == null || orderNumberStr.isBlank()) {
            errorMessage.set("Please enter an order number");
            return false;
        }

        try {
            OrderNumber orderNum = new OrderNumber(orderNumberStr);

            // Check if order already exists
            boolean exists = orders.stream()
                .anyMatch(order -> order.getOrderNumber() != null && order.getOrderNumber().equals(orderNum));

            if (exists) {
                errorMessage.set("Order with this number already exists");
                return false;
            }

            // Create new order
            OrderId orderId = OrderId.newId();
            User currentUser = SessionManager.getInstance().getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not logged in"));
            Order newOrder = new Order(orderId, orderNum, currentUser, Timestamp.now());

            // Save the new order
            orderRepository.save(newOrder);

            // Refresh the orders list
            loadAllOrders();

            // Select the new order
            selectOrder(newOrder);

            return true;
        } catch (IllegalArgumentException e) {
            errorMessage.set("Invalid order number format");
            return false;
        } catch (Exception e) {
            errorMessage.set("Error creating order: " + e.getMessage());
            return false;
        }
    }

    // Getters for properties

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty orderDetailsProperty() {
        return orderDetails;
    }

    public BooleanProperty orderSelectedProperty() {
        return orderSelected;
    }

    public BooleanProperty isLoadingProperty() {
        return isLoading;
    }

    public ObjectProperty<Order> selectedOrderProperty() {
        return selectedOrder;
    }

    public ObjectProperty<LocalDate> fromDateProperty() {
        return fromDate;
    }

    public ObjectProperty<LocalDate> toDateProperty() {
        return toDate;
    }

    public ListProperty<Order> ordersProperty() {
        return orders;
    }

    public ListProperty<Order> filteredOrdersProperty() {
        return filteredOrders;
    }

    public ObservableList<Order> getFilteredOrders() {
        return filteredOrders.get();
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
