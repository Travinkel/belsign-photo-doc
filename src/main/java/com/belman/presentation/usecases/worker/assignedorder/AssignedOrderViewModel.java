package com.belman.presentation.usecases.worker.assignedorder;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.UserRole;
import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.worker.WorkerService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.login.LoginView;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeView;
import javafx.beans.property.*;

import java.util.Arrays;
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
    private final BooleanProperty devMode = new SimpleBooleanProperty(false);

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
        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Starting to load assigned orders");

        try {
            // Get the current user
            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Getting current user from SessionContext");
            SessionContext.getCurrentUser().ifPresentOrElse(
                user -> {
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Current user found: " + user.getUsername().value() + ", ID: " + user.getId().id());

                    // Get all orders
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Fetching all orders from OrderService");
                    List<OrderBusiness> orders = orderService.getAllOrders();
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Found " + orders.size() + " total orders in database");

                    // Log all orders for debugging
                    if (orders.isEmpty()) {
                        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: No orders found in database");
                    } else {
                        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Listing all orders in database:");
                        for (int i = 0; i < orders.size(); i++) {
                            OrderBusiness order = orders.get(i);
                            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Order " + (i+1) + ": " + 
                                "Number=" + (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null") + ", " +
                                "ID=" + order.getId().id() + ", " +
                                "CreatedBy=" + (order.getCreatedBy() != null ? order.getCreatedBy().id() : "null") + ", " +
                                "Status=" + order.getStatus());
                        }
                    }

                    // Filter orders based on user role
                    List<OrderBusiness> userOrders;
                    boolean isProductionWorker = user.getRoles().contains(UserRole.PRODUCTION);
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: User is " + (isProductionWorker ? "a production worker" : "not a production worker"));

                    // For production workers, show orders assigned to them
                    if (isProductionWorker) {
                        // For production users, show orders assigned to them
                        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Production user - showing assigned orders");

                        userOrders = orders.stream()
                            .filter(o -> {
                                if (o.getAssignedTo() == null) {
                                    return false;
                                }

                                String orderAssignedToId = o.getAssignedTo().id().id();
                                String userId = user.getId().id();

                                // Compare case-insensitive to handle UUID case differences
                                boolean isAssigned = orderAssignedToId.equalsIgnoreCase(userId);

                                System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Order " + o.getId().id() + 
                                    " assigned to " + orderAssignedToId + 
                                    " - matches current user: " + isAssigned);

                                return isAssigned;
                            })
                            .toList();

                        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: After filtering, found " + userOrders.size() + " orders assigned to production worker");
                        if (!userOrders.isEmpty()) {
                            statusMessage.set("Production user: Showing assigned orders (" + userOrders.size() + " total)");
                        }
                    } else if (user.getRoles().contains(UserRole.QA)) {
                        // For QA users, show orders with status COMPLETED that need approval
                        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: QA user - showing orders that need approval");
                        userOrders = orders.stream()
                            .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                            .toList();
                        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: After filtering, found " + userOrders.size() + " COMPLETED orders for QA user");
                    } else {
                        // For other users (Admin), filter orders for the current user
                        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Regular user, filtering orders for current user: " + user.getId().id());
                        userOrders = orders.stream()
                            .filter(o -> {
                                boolean matches = o.getCreatedBy() != null && o.getCreatedBy().id().equals(user.getId());
                                System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Order " + o.getId().id() + 
                                    " created by " + (o.getCreatedBy() != null ? o.getCreatedBy().id() : "null") + 
                                    " - matches current user: " + matches);
                                return matches;
                            })
                            .toList();
                        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: After filtering, found " + userOrders.size() + " orders for current user");
                    }

                    if (userOrders.isEmpty()) {
                        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: No orders found for current user");
                        errorMessage.set("No active orders assigned to you. Please contact your supervisor or QA team to assign an order for documentation.");
                        statusMessage.set("Waiting for order assignment");
                        loading.set(false);
                        return;
                    }

                    // Auto-select the first order for the user
                    OrderBusiness order = userOrders.get(0);
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Auto-selecting first order: " + 
                        "Number=" + (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null") + ", " +
                        "ID=" + order.getId().id());
                    currentOrder.set(order);

                    // Store the order in the WorkerFlowContext
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Storing order in WorkerFlowContext");
                    WorkerFlowContext.setCurrentOrder(order);

                    // Update the UI properties
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Updating UI properties with order data");
                    orderNumber.set(order.getOrderNumber().value());
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Set orderNumber property: " + orderNumber.get());

                    // Use a placeholder for customer name since we don't have direct access to customer data
                    customerName.set(order.getCustomerId() != null ? 
                        "Customer ID: " + order.getCustomerId().toString() : "Unknown Customer");
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Set customerName property: " + customerName.get());

                    productDescription.set(order.getProductDescription() != null ? 
                        order.getProductDescription().toString() : "No description available");
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Set productDescription property: " + productDescription.get());

                    // Set project name (placeholder)
                    projectName.set("Project: " + order.getOrderNumber().value());
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Set projectName property: " + projectName.get());

                    // Set location (placeholder)
                    location.set("Location: Production Floor");
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Set location property: " + location.get());

                    // Set due date (placeholder)
                    dueDate.set("Due: " + java.time.LocalDate.now().plusDays(7));
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Set dueDate property: " + dueDate.get());

                    // Load photo templates for this order
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Loading photo templates for order: " + order.getId().id());
                    loadPhotoTemplates(order);

                    statusMessage.set("Order loaded successfully.");
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Order loaded successfully");
                    loading.set(false);
                },
                () -> {
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: No user found in SessionContext");
                    errorMessage.set("Your session has expired or you are not logged in. Please return to the login screen and sign in again.");
                    statusMessage.set("Authentication required");
                    loading.set(false);

                    // Automatically navigate back to login after a short delay
                    System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Scheduling navigation to login screen after delay");
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000); // 3 seconds delay
                            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Navigating to login screen after delay");
                            Router.navigateTo(LoginView.class);
                        } catch (InterruptedException e) {
                            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Interrupted while waiting to navigate to login");
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
        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Loading photo templates for order: " + 
                           order.getId().id() + ", Number: " + 
                           (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null"));

        try {
            // Get templates for this order using the getAvailableTemplates method
            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Calling workerService.getAvailableTemplates for order ID: " + order.getId().id());
            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: WorkerService class: " + workerService.getClass().getName());

            List<PhotoTemplate> templates = workerService.getAvailableTemplates(order.getId());
            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Retrieved " + templates.size() + " photo templates");

            // Log each template
            for (int i = 0; i < templates.size(); i++) {
                PhotoTemplate template = templates.get(i);
                System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Template " + (i+1) + ": " + 
                                  "Name=" + template.name() + ", " +
                                  "Description=" + template.description());
            }

            // Check if templates list is empty and add default templates if needed
            if (templates.isEmpty()) {
                System.out.println("[DEBUG_LOG] AssignedOrderViewModel: No templates returned from service, adding default templates");

                // Add default templates
                templates = Arrays.asList(
                    PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                    PhotoTemplate.BACK_VIEW_OF_ASSEMBLY,
                    PhotoTemplate.TOP_VIEW_OF_JOINT,
                    PhotoTemplate.SIDE_VIEW_OF_WELD
                );

                System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Added " + templates.size() + " default templates");
            }

            // Update the templates list
            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Updating photoTemplates list with " + templates.size() + " templates");
            photoTemplates.setAll(templates);

            // Set the photo count
            String countText = templates.size() + " photos required";
            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Setting photoCount to: " + countText);
            photoCount.set(countText);

            // Check if there are multiple templates
            boolean hasMultiple = templates.size() > 1;
            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Setting hasMultipleTemplates to: " + hasMultiple);
            hasMultipleTemplates.set(hasMultiple);

            // If there's only one template, auto-select it
            if (templates.size() == 1) {
                PhotoTemplate template = templates.get(0);
                System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Only one template found, auto-selecting: " + template.name());
                WorkerFlowContext.setSelectedTemplate(template);
            } else if (templates.size() > 1) {
                System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Multiple templates found (" + templates.size() + "), user will need to select one");
            } else {
                System.out.println("[DEBUG_LOG] AssignedOrderViewModel: No templates found for this order even after adding defaults");
            }

            System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Photo templates loaded successfully");
        } catch (Exception e) {
            // Log the full error for debugging
            System.err.println("[DEBUG_LOG] AssignedOrderViewModel: Error loading photo templates: " + e.getMessage());
            e.printStackTrace();

            // Set a user-friendly error message
            String userMessage = "Unable to load photo templates for this order. ";

            // Add more context based on the type of error
            if (e.getMessage() != null) {
                if (e.getMessage().contains("database") || e.getMessage().contains("SQL")) {
                    userMessage += "There seems to be a database issue. ";
                    System.err.println("[DEBUG_LOG] AssignedOrderViewModel: Database-related error detected");
                } else if (e.getMessage().contains("template") || e.getMessage().contains("not found")) {
                    userMessage += "The required templates may not be configured properly. ";
                    System.err.println("[DEBUG_LOG] AssignedOrderViewModel: Template configuration error detected");
                }
            }

            userMessage += "You can still view order details, but photo capture may not be available. Please contact your supervisor.";
            System.err.println("[DEBUG_LOG] AssignedOrderViewModel: Setting error message: " + userMessage);
            errorMessage.set(userMessage);

            // Set photo count to indicate the issue
            System.err.println("[DEBUG_LOG] AssignedOrderViewModel: Setting photoCount to indicate templates are unavailable");
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

    /**
     * Gets the dev mode property.
     * This property is used to control the visibility of development-only UI elements.
     *
     * @return the dev mode property
     */
    public BooleanProperty devModeProperty() {
        return devMode;
    }

    /**
     * Creates a test order for development and testing purposes.
     * This method is disabled in production mode.
     */
    public void createTestOrder() {
        errorMessage.set("Test order creation is not available in production mode");
    }

    /**
     * Refreshes the photo templates for the current order.
     * This method can be called when the user wants to manually refresh the templates.
     */
    public void refreshPhotoTemplates() {
        if (currentOrder.get() == null) {
            errorMessage.set("Cannot refresh templates: No order is currently loaded.");
            return;
        }

        System.out.println("[DEBUG_LOG] AssignedOrderViewModel: Manually refreshing photo templates for order: " + 
                          currentOrder.get().getId().id());

        loading.set(true);
        statusMessage.set("Refreshing photo templates...");

        // Load photo templates for the current order
        loadPhotoTemplates(currentOrder.get());

        loading.set(false);
        statusMessage.set("Photo templates refreshed.");
    }
}
