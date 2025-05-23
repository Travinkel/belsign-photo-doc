package com.belman.presentation.usecases.qa.assignment;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.UserRole;
import com.belman.application.usecase.order.OrderService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.login.LoginView;
import com.belman.presentation.usecases.qa.dashboard.QADashboardView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewModel for the QAOrderAssignmentView.
 * Manages the state and logic for assigning orders to production workers.
 */
public class QAOrderAssignmentViewModel extends BaseViewModel<QAOrderAssignmentViewModel> {

    private final AuthenticationService authenticationService = ServiceLocator.getService(AuthenticationService.class);

    @Inject
    private OrderService orderService;

    @Inject
    private UserRepository userRepository;

    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Loading...");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> selectedOrder = new SimpleObjectProperty<>();
    private final ObjectProperty<UserBusiness> selectedWorker = new SimpleObjectProperty<>();
    private final ListProperty<OrderBusiness> orders = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<UserBusiness> productionWorkers = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Override
    public void onShow() {
        // Load orders and production workers
        loadOrders();
        loadProductionWorkers();
    }

    /**
     * Loads all orders from the OrderService.
     */
    private void loadOrders() {
        loading.set(true);
        statusMessage.set("Loading orders...");

        try {
            List<OrderBusiness> allOrders = orderService.getAllOrders();
            orders.setAll(allOrders);
            statusMessage.set("Orders loaded successfully.");
        } catch (Exception e) {
            errorMessage.set("Error loading orders: " + e.getMessage());
            statusMessage.set("Error loading orders");
        } finally {
            loading.set(false);
        }
    }

    /**
     * Loads all production workers from the UserRepository.
     */
    private void loadProductionWorkers() {
        loading.set(true);
        statusMessage.set("Loading production workers...");

        try {
            List<UserBusiness> allUsers = userRepository.findAll();
            List<UserBusiness> workers = allUsers.stream()
                    .filter(user -> user.getRoles().contains(UserRole.PRODUCTION))
                    .collect(Collectors.toList());
            productionWorkers.setAll(workers);
            statusMessage.set("Production workers loaded successfully.");
        } catch (Exception e) {
            errorMessage.set("Error loading production workers: " + e.getMessage());
            statusMessage.set("Error loading production workers");
        } finally {
            loading.set(false);
        }
    }

    /**
     * Assigns the selected order to the selected worker.
     */
    public void assignOrderToWorker() {
        OrderBusiness order = selectedOrder.get();
        UserBusiness worker = selectedWorker.get();

        if (order == null) {
            errorMessage.set("Please select an order to assign.");
            return;
        }

        if (worker == null) {
            errorMessage.set("Please select a worker to assign the order to.");
            return;
        }

        loading.set(true);
        statusMessage.set("Assigning order to worker...");

        try {
            // Set the assigned_to field on the order
            order.setAssignedTo(UserReference.from(worker));

            // Save the updated order
            orderService.saveOrder(order);

            statusMessage.set("Order assigned successfully.");
            errorMessage.set("");
        } catch (Exception e) {
            errorMessage.set("Error assigning order: " + e.getMessage());
            statusMessage.set("Error assigning order");
        } finally {
            loading.set(false);
        }
    }

    /**
     * Navigates back to the QA dashboard.
     */
    public void navigateToQADashboard() {
        Router.navigateTo(QADashboardView.class);
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        try {
            authenticationService.logout();
            SessionContext.clear();
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

    public ObjectProperty<OrderBusiness> selectedOrderProperty() {
        return selectedOrder;
    }

    public ObjectProperty<UserBusiness> selectedWorkerProperty() {
        return selectedWorker;
    }

    public ListProperty<OrderBusiness> ordersProperty() {
        return orders;
    }

    public ListProperty<UserBusiness> productionWorkersProperty() {
        return productionWorkers;
    }
}
