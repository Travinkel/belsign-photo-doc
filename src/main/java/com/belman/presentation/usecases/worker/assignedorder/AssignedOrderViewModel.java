package com.belman.presentation.usecases.worker.assignedorder;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.security.AuthenticationService;
import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.worker.WorkerService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.authentication.login.LoginView;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
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

    @Inject
    private WorkerService workerService;

    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Loading...");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> currentOrder = new SimpleObjectProperty<>();
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty customerName = new SimpleStringProperty("");
    private final StringProperty productDescription = new SimpleStringProperty("");
    private final StringProperty workerName = new SimpleStringProperty("");
    private final StringProperty projectName = new SimpleStringProperty("");
    private final StringProperty location = new SimpleStringProperty("");
    private final StringProperty dueDate = new SimpleStringProperty("");
    private final StringProperty photoCount = new SimpleStringProperty("");
    private final ListProperty<PhotoTemplate> photoTemplates = new SimpleListProperty<>(javafx.collections.FXCollections.observableArrayList());
    private final BooleanProperty hasMultipleTemplates = new SimpleBooleanProperty(false);

    @Override
    public void onShow() {
        // Load the current order for the user
        loadAssignedOrder();

        // Load the current worker's name
        loadWorkerName();
    }

    /**
     * Loads the current worker's name from the session.
     */
    private void loadWorkerName() {
        SessionContext.getCurrentUser().ifPresent(user -> {
            workerName.set(user.getUsername().value());
        });
    }

    /**
     * Loads the assigned order for the logged-in user.
     * Auto-selects the first available order if only one is assigned.
     */
    private void loadAssignedOrder() {
        loading.set(true);
        statusMessage.set("Loading assigned order...");

        try {
            // Get the current user
            SessionContext.getCurrentUser().ifPresentOrElse(
                user -> {
                    // Get all orders
                    List<OrderBusiness> orders = orderService.getAllOrders();

                    // In dev mode, show all orders regardless of creator
                    // In production, filter orders for the current user
                    List<OrderBusiness> userOrders;
                    if (com.belman.bootstrap.config.DevModeConfig.isDevMode()) {
                        // In dev mode, show all orders
                        userOrders = orders;
                        if (!orders.isEmpty()) {
                            statusMessage.set("Dev mode: Showing all orders");
                        }
                    } else {
                        // In production, filter orders for the current user
                        userOrders = orders.stream()
                            .filter(o -> o.getCreatedBy() != null && 
                                   o.getCreatedBy().id().equals(user.getId()))
                            .toList();
                    }

                    if (userOrders.isEmpty()) {
                        errorMessage.set("No active orders assigned to you. Please contact your supervisor or QA team to assign an order for documentation.");
                        statusMessage.set("Waiting for order assignment");
                        loading.set(false);
                        return;
                    }

                    // Auto-select the first order for the user
                    OrderBusiness order = userOrders.get(0);
                    currentOrder.set(order);

                    // Store the order in the WorkerFlowContext
                    WorkerFlowContext.setCurrentOrder(order);

                    // Update the UI properties
                    orderNumber.set(order.getOrderNumber().toString());

                    // Use a placeholder for customer name since we don't have direct access to customer data
                    customerName.set(order.getCustomerId() != null ? 
                        "Customer ID: " + order.getCustomerId().toString() : "Unknown Customer");

                    productDescription.set(order.getProductDescription() != null ? 
                        order.getProductDescription().toString() : "No description available");

                    // Set project name (placeholder)
                    projectName.set("Project: " + order.getOrderNumber().toString());

                    // Set location (placeholder)
                    location.set("Location: Production Floor");

                    // Set due date (placeholder)
                    dueDate.set("Due: " + java.time.LocalDate.now().plusDays(7));

                    // Load photo templates for this order
                    loadPhotoTemplates(order);

                    statusMessage.set("Order loaded successfully.");
                    loading.set(false);
                },
                () -> {
                    errorMessage.set("Your session has expired or you are not logged in. Please return to the login screen and sign in again.");
                    statusMessage.set("Authentication required");
                    loading.set(false);

                    // Automatically navigate back to login after a short delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000); // 3 seconds delay
                            Router.navigateTo(LoginView.class);
                        } catch (InterruptedException e) {
                            // Ignore
                        }
                    }).start();
                }
            );
        } catch (Exception e) {
            // Log the full error for debugging
            System.err.println("Error loading order: " + e.getMessage());
            e.printStackTrace();

            // Set a user-friendly error message
            String userMessage = "Unable to load your assigned orders. ";

            // Add more context based on the type of error
            if (e.getMessage() != null) {
                if (e.getMessage().contains("database") || e.getMessage().contains("SQL") || 
                    e.getMessage().contains("Connection")) {
                    userMessage += "There seems to be a database connection issue. ";
                } else if (e.getMessage().contains("permission") || e.getMessage().contains("access")) {
                    userMessage += "You may not have permission to view these orders. ";
                }
            }

            userMessage += "Please try again later or contact technical support if the problem persists.";
            errorMessage.set(userMessage);
            statusMessage.set("Error loading data");
            loading.set(false);
        }
    }

    /**
     * Loads photo templates for the given order.
     * 
     * @param order the order to load templates for
     */
    private void loadPhotoTemplates(OrderBusiness order) {
        try {
            // Get templates for this order using the getAvailableTemplates method
            List<PhotoTemplate> templates = workerService.getAvailableTemplates(order.getId());

            // Update the templates list
            photoTemplates.setAll(templates);

            // Set the photo count
            photoCount.set(templates.size() + " photos required");

            // Check if there are multiple templates
            hasMultipleTemplates.set(templates.size() > 1);

            // If there's only one template, auto-select it
            if (templates.size() == 1) {
                WorkerFlowContext.setSelectedTemplate(templates.get(0));
            }
        } catch (Exception e) {
            // Log the full error for debugging
            System.err.println("Error loading photo templates: " + e.getMessage());
            e.printStackTrace();

            // Set a user-friendly error message
            String userMessage = "Unable to load photo templates for this order. ";

            // Add more context based on the type of error
            if (e.getMessage() != null) {
                if (e.getMessage().contains("database") || e.getMessage().contains("SQL")) {
                    userMessage += "There seems to be a database issue. ";
                } else if (e.getMessage().contains("template") || e.getMessage().contains("not found")) {
                    userMessage += "The required templates may not be configured properly. ";
                }
            }

            userMessage += "You can still view order details, but photo capture may not be available. Please contact your supervisor.";
            errorMessage.set(userMessage);

            // Set photo count to indicate the issue
            photoCount.set("Photo templates unavailable");
        }
    }

    /**
     * Starts the photo process for the current order.
     * If there's only one template, it will be auto-selected.
     */
    public void startPhotoProcess() {
        if (currentOrder.get() == null) {
            errorMessage.set("Cannot start photo session: No order is currently loaded. Please wait while we try to load your assigned orders or contact your supervisor.");
            statusMessage.set("Waiting for order data");

            // Try to reload the order data
            loadAssignedOrder();
            return;
        }

        // Make sure the order is stored in the WorkerFlowContext
        WorkerFlowContext.setCurrentOrder(currentOrder.get());

        // If there's only one template, auto-select it
        if (photoTemplates.size() == 1) {
            WorkerFlowContext.setSelectedTemplate(photoTemplates.get(0));
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

            // Clear the worker flow context
            WorkerFlowContext.clear();

            // Navigate to the login view
            Router.navigateTo(LoginView.class);
        } catch (Exception e) {
            // Log the full error for debugging
            System.err.println("Error logging out: " + e.getMessage());
            e.printStackTrace();

            // Set a user-friendly error message
            errorMessage.set("Unable to log out properly. Please close the application and restart it to ensure you are fully logged out.");

            // Even if logout fails, try to navigate to login screen anyway
            try {
                // Clear the worker flow context
                WorkerFlowContext.clear();

                // Navigate to the login view
                Router.navigateTo(LoginView.class);
            } catch (Exception navEx) {
                System.err.println("Failed to navigate to login view after logout error: " + navEx.getMessage());
            }
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

    public StringProperty workerNameProperty() {
        return workerName;
    }

    public StringProperty projectNameProperty() {
        return projectName;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public StringProperty dueDateProperty() {
        return dueDate;
    }

    public StringProperty photoCountProperty() {
        return photoCount;
    }

    public ListProperty<PhotoTemplate> photoTemplatesProperty() {
        return photoTemplates;
    }

    public BooleanProperty hasMultipleTemplatesProperty() {
        return hasMultipleTemplates;
    }
}
