package com.belman.ui.views.ordergallery;

import com.belman.common.di.Inject;
import com.belman.domain.common.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;
import com.belman.service.session.SessionManager;
import com.belman.ui.base.BaseViewModel;
import com.belman.ui.navigation.Router;
import com.belman.ui.usecases.authentication.login.LoginView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View model for the order gallery view.
 */
public class OrderGalleryViewModel extends BaseViewModel<OrderGalleryViewModel> {

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty orderDetails = new SimpleStringProperty("");
    private final BooleanProperty orderSelected = new SimpleBooleanProperty(false);
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> selectedOrder = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> fromDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> toDate = new SimpleObjectProperty<>();
    private final ListProperty<OrderBusiness> orderBusinesses = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    private final ListProperty<OrderBusiness> filteredOrderBusinesses = new SimpleListProperty<>(
            FXCollections.observableArrayList());
    @Inject
    private OrderRepository orderRepository;

    @Override
    public void onShow() {
        // Load all orderBusinesses when the view is shown
        loadAllOrders();
    }

    /**
     * Loads all orderBusinesses from the repository.
     */
    public void loadAllOrders() {
        isLoading.set(true);
        errorMessage.set("");

        try {
            List<OrderBusiness> allOrderBusinesses = orderRepository.findAll();
            orderBusinesses.setAll(allOrderBusinesses);
            filteredOrderBusinesses.setAll(allOrderBusinesses);
            isLoading.set(false);
        } catch (Exception e) {
            errorMessage.set("Error loading orderBusinesses: " + e.getMessage());
            isLoading.set(false);
        }
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Searches for orderBusinesses based on the search text.
     * The search text can be an order number, customer name, or other order property.
     */
    public void searchOrders() {
        String search = searchText.get();
        if (search == null || search.isBlank()) {
            // If search text is empty, show all orderBusinesses
            filteredOrderBusinesses.setAll(orderBusinesses);
            return;
        }

        // Filter orderBusinesses based on search text
        List<OrderBusiness> filtered = orderBusinesses.stream()
                .filter(order -> {
                    // Check if order number contains search text
                    if (order.getOrderNumber() != null &&
                        order.getOrderNumber().toString().toLowerCase().contains(search.toLowerCase())) {
                        return true;
                    }

                    // Check if customer ID contains search text
                    return order.getCustomerId() != null &&
                           order.getCustomerId().id().toLowerCase().contains(search.toLowerCase());

                    // Add more search criteria as needed
                })
                .collect(Collectors.toList());

        filteredOrderBusinesses.setAll(filtered);
    }

    /**
     * Filters orderBusinesses by date range.
     */
    public void filterByDateRange() {
        LocalDate from = fromDate.get();
        LocalDate to = toDate.get();

        if (from == null && to == null) {
            // If no date range is specified, show all orderBusinesses
            filteredOrderBusinesses.setAll(orderBusinesses);
            return;
        }

        // Filter orderBusinesses based on date range
        List<OrderBusiness> filtered = orderBusinesses.stream()
                .filter(order -> {
                    LocalDate orderDate = order.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate();

                    if (from != null && to != null) {
                        return !orderDate.isBefore(from) && !orderDate.isAfter(to);
                    } else if (from != null) {
                        return !orderDate.isBefore(from);
                    } else {
                        return !orderDate.isAfter(to);
                    }
                })
                .collect(Collectors.toList());

        filteredOrderBusinesses.setAll(filtered);
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
            boolean exists = orderBusinesses.stream()
                    .anyMatch(order -> order.getOrderNumber() != null && order.getOrderNumber().equals(orderNum));

            if (exists) {
                errorMessage.set("OrderBusiness with this number already exists");
                return false;
            }

            // Create new order
            OrderId orderId = OrderId.newId();
            UserBusiness currentUser = SessionManager.getInstance().getCurrentUser()
                    .orElseThrow(() -> new IllegalStateException("User not logged in"));
            UserReference userRef = UserReference.from(currentUser);
            OrderBusiness newOrderBusiness = new OrderBusiness(orderId, orderNum, userRef, Timestamp.now());

            // Save the new order
            orderRepository.save(newOrderBusiness);

            // Refresh the orderBusinesses list
            loadAllOrders();

            // Select the new order
            selectOrder(newOrderBusiness);

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

    /**
     * Sets the selected orderBusiness and updates the orderBusiness details.
     *
     * @param orderBusiness the selected orderBusiness
     */
    public void selectOrder(OrderBusiness orderBusiness) {
        if (orderBusiness != null) {
            selectedOrder.set(orderBusiness);
            orderSelected.set(true);

            // Update orderBusiness details
            StringBuilder details = new StringBuilder();
            details.append("OrderBusiness Number: ").append(orderBusiness.getOrderNumber()).append("\n");

            if (orderBusiness.getCustomerId() != null) {
                details.append("Customer ID: ").append(orderBusiness.getCustomerId().id()).append("\n");
            }

            details.append("Created: ").append(orderBusiness.getCreatedAt().toString()).append("\n");
            details.append("Status: ").append(orderBusiness.getStatus()).append("\n");

            // Add more details as needed

            orderDetails.set(details.toString());
        } else {
            selectedOrder.set(null);
            orderSelected.set(false);
            orderDetails.set("");
        }
    }

    public StringProperty searchTextProperty() {
        return searchText;
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

    public ObjectProperty<OrderBusiness> selectedOrderProperty() {
        return selectedOrder;
    }

    public ObjectProperty<LocalDate> fromDateProperty() {
        return fromDate;
    }

    public ObjectProperty<LocalDate> toDateProperty() {
        return toDate;
    }

    public ListProperty<OrderBusiness> ordersProperty() {
        return orderBusinesses;
    }

    public ListProperty<OrderBusiness> filteredOrdersProperty() {
        return filteredOrderBusinesses;
    }

    public ObservableList<OrderBusiness> getFilteredOrders() {
        return filteredOrderBusinesses.get();
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
