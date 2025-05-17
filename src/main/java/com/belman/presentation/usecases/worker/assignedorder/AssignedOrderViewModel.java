package com.belman.presentation.usecases.worker.assignedorder;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.security.AuthenticationService;
import com.belman.application.usecase.order.OrderService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.archive.authentication.login.LoginView;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeView;
import javafx.beans.property.*;

import java.util.List;

/**
 * ViewModel for the AssignedOrderView.
 * Manages the state and logic for displaying the worker's assigned order.
 */
public class AssignedOrderViewModel extends BaseViewModel<AssignedOrderViewModel> {

    private final AuthenticationService authenticationService = ServiceLocator.getService(AuthenticationService.class);

    @Inject
    private OrderService orderService;

    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Loading...");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> currentOrder = new SimpleObjectProperty<>();
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty customerName = new SimpleStringProperty("");
    private final StringProperty productDescription = new SimpleStringProperty("");

    @Override
    public void onShow() {
        // Load the current order for the user
        loadAssignedOrder();
    }

    /**
     * Loads the assigned order for the logged-in user.
     */
    private void loadAssignedOrder() {
        loading.set(true);
        statusMessage.set("Loading assigned order...");

        // Get the current user
        SessionContext.getCurrentUser().ifPresentOrElse(
            user -> {
                // Get all orders and find the first one for the current user
                List<OrderBusiness> orders = orderService.getAllOrders();

                // Filter orders for the current user
                List<OrderBusiness> userOrders = orders.stream()
                    .filter(o -> o.getCreatedBy() != null && 
                           o.getCreatedBy().id().equals(user.getId()))
                    .toList();

                if (userOrders.isEmpty()) {
                    errorMessage.set("No active order found for the current user.");
                    loading.set(false);
                    return;
                }

                // Set the current order to the first order for the user
                OrderBusiness order = userOrders.get(0);
                currentOrder.set(order);

                // Update the UI properties
                orderNumber.set(order.getOrderNumber().toString());
                // Use a placeholder for customer name since we don't have direct access to customer data
                customerName.set(order.getCustomerId() != null ? 
                    "Customer ID: " + order.getCustomerId().toString() : "Unknown Customer");
                productDescription.set(order.getProductDescription() != null ? 
                    order.getProductDescription().toString() : "No description available");

                statusMessage.set("Order loaded successfully.");
                loading.set(false);
            },
            () -> {
                errorMessage.set("No user is logged in.");
                loading.set(false);
            }
        );
    }

    /**
     * Starts the photo process for the current order.
     */
    public void startPhotoProcess() {
        if (currentOrder.get() == null) {
            errorMessage.set("No order is currently loaded.");
            return;
        }

        // Navigate to the PhotoCubeView
        Router.navigateTo(PhotoCubeView.class);
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    public void logout() {
        try {
            // Log out the user
            authenticationService.logout();

            // Clear the session context
            SessionContext.clear();

            // Navigate to the login view
            Router.navigateTo(LoginView.class);
        } catch (Exception e) {
            errorMessage.set("Error logging out: " + e.getMessage());
        }
    }

    // Getters for properties

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public ObjectProperty<OrderBusiness> currentOrderProperty() {
        return currentOrder;
    }

    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public StringProperty productDescriptionProperty() {
        return productDescription;
    }
}
