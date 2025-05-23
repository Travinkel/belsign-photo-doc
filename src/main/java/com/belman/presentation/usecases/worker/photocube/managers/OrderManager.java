package com.belman.presentation.usecases.worker.photocube.managers;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.user.UserBusiness;
import com.belman.application.usecase.order.OrderProgressService;
import com.belman.application.usecase.order.OrderService;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Optional;

/**
 * Manages order loading and related operations for the PhotoCubeViewModel.
 * This class is responsible for retrieving and managing the current order.
 * 
 * <p>NOTE: This class is an implementation detail of the PhotoCubeViewModel and is not intended
 * to be used directly by other components. It is public only to allow for dependency injection
 * and testing. In a future refactoring, this class could be moved to the same package as
 * PhotoCubeViewModel to allow for package-private access.</p>
 */
public class OrderManager {

    @Inject
    private OrderService orderService;

    @Inject
    private OrderProgressService orderProgressService;

    // Properties for UI binding
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Loading...");
    private final ObjectProperty<OrderBusiness> currentOrder = new SimpleObjectProperty<>();
    private final StringProperty orderNumber = new SimpleStringProperty("");

    /**
     * Loads the current order from the WorkerFlowContext or from the session.
     * 
     * @return true if the order was loaded successfully, false otherwise
     */
    public boolean loadCurrentOrder() {
        statusMessage.set("Loading order...");

        try {
            // Get the current order from the WorkerFlowContext
            OrderBusiness order = WorkerFlowContext.getCurrentOrder();

            if (order == null) {
                // If no order is in the context, try to get one from the session
                return loadOrderFromSession();
            } else {
                // Use the order from the WorkerFlowContext
                currentOrder.set(order);

                // Format the order number in a user-friendly way
                String friendlyOrderNumber = order.getOrderNumber().toString().replace("ORD-", "Order #");
                orderNumber.set(friendlyOrderNumber);

                return true;
            }
        } catch (Exception e) {
            errorMessage.set("Unexpected error loading order: " + e.getMessage() + ". Please try again or contact support.");
            return false;
        }
    }

    /**
     * Loads an order from the session context.
     * 
     * @return true if the order was loaded successfully, false otherwise
     */
    private boolean loadOrderFromSession() {
        try {
            // Try to get the current user from the session
            return SessionContext.getCurrentUser().map(user -> {
                try {
                    // Get the assigned order for the current user using OrderProgressService
                    Optional<OrderBusiness> assignedOrderOpt = orderProgressService.getAssignedOrder(user);

                    if (assignedOrderOpt.isEmpty()) {
                        // If no assigned order is found, try to get all orders as a fallback
                        return loadOrderFromAllOrders(user);
                    } else {
                        // Use the assigned order
                        OrderBusiness assignedOrder = assignedOrderOpt.get();
                        System.out.println("[DEBUG_LOG] Found assigned order for user: " + user.getUsername().value() + 
                            " - Order ID: " + assignedOrder.getId().id() + 
                            ", Order Number: " + (assignedOrder.getOrderNumber() != null ? assignedOrder.getOrderNumber().value() : "null"));

                        currentOrder.set(assignedOrder);

                        // Format the order number in a user-friendly way
                        String friendlyOrderNumber = assignedOrder.getOrderNumber().toString().replace("ORD-", "Order #");
                        orderNumber.set(friendlyOrderNumber);

                        // Store the order in the WorkerFlowContext for future use
                        WorkerFlowContext.setCurrentOrder(assignedOrder);

                        return true;
                    }
                } catch (Exception e) {
                    errorMessage.set("Error loading orders: " + e.getMessage() + ". Please try again or contact support.");
                    return false;
                }
            }).orElseGet(() -> {
                errorMessage.set("No user is logged in. Please log in to continue.");
                return false;
            });
        } catch (Exception e) {
            errorMessage.set("Error accessing session: " + e.getMessage() + ". Please try again or contact support.");
            return false;
        }
    }

    /**
     * Loads an order from all available orders as a fallback.
     * 
     * @param user the current user
     * @return true if an order was loaded successfully, false otherwise
     */
    private boolean loadOrderFromAllOrders(UserBusiness user) {
        try {
            // Get all orders
            List<OrderBusiness> orders = orderService.getAllOrders();

            if (orders.isEmpty()) {
                errorMessage.set("No orders found in the system. Please contact your supervisor to create an order.");
                return false;
            }

            // Log that we're falling back to created orders
            System.out.println("[DEBUG_LOG] No assigned orders found for user: " + user.getUsername().value() + ", falling back to created orders");

            // Filter orders created by the current user as a fallback
            List<OrderBusiness> userOrders = orders.stream()
                .filter(o -> o.getCreatedBy() != null && 
                       o.getCreatedBy().id().equals(user.getId()))
                .toList();

            if (userOrders.isEmpty()) {
                errorMessage.set("No active orders assigned to you. Please contact your supervisor to assign an order for documentation.");
                return false;
            }

            // Set the current order to the first order created by the user
            OrderBusiness userOrder = userOrders.get(0);
            currentOrder.set(userOrder);
            orderNumber.set(userOrder.getOrderNumber().toString());

            // Store the order in the WorkerFlowContext for future use
            WorkerFlowContext.setCurrentOrder(userOrder);

            return true;
        } catch (Exception e) {
            errorMessage.set("Error loading orders: " + e.getMessage() + ". Please try again or contact support.");
            return false;
        }
    }

    /**
     * Gets the current order.
     * 
     * @return the current order
     */
    public OrderBusiness getCurrentOrder() {
        return currentOrder.get();
    }

    /**
     * Gets the current order ID.
     * 
     * @return the current order ID, or null if no order is loaded
     */
    public OrderId getCurrentOrderId() {
        OrderBusiness order = currentOrder.get();
        return order != null ? order.getId() : null;
    }

    // Getters for properties

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public ObjectProperty<OrderBusiness> currentOrderProperty() {
        return currentOrder;
    }

    public StringProperty orderNumberProperty() {
        return orderNumber;
    }
}
