package com.belman.presentation.usecases.worker.completed;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.session.SessionContext;
import com.belman.domain.security.AuthenticationService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.archive.authentication.login.LoginView;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.assignedorder.AssignedOrderView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for the CompletedView.
 * Manages the state and logic for displaying a completion message after submitting photos.
 */
public class CompletedViewModel extends BaseViewModel<CompletedViewModel> {

    private final AuthenticationService authenticationService = ServiceLocator.getService(AuthenticationService.class);

    // Properties for UI binding
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty completionMessage = new SimpleStringProperty("Order completed successfully!");

    @Override
    public void onShow() {
        // Get the completed order number from the worker flow context
        String completedOrderNumber = (String) WorkerFlowContext.getAttribute("completedOrderNumber");
        if (completedOrderNumber != null && !completedOrderNumber.isEmpty()) {
            orderNumber.set(completedOrderNumber);
            completionMessage.set("Order " + completedOrderNumber + " has been completed successfully!");
        }
    }

    /**
     * Navigates back to the assigned order view.
     */
    public void goToAssignedOrder() {
        // Clear the worker flow context
        WorkerFlowContext.clear();

        // Navigate to the assigned order view
        Router.navigateTo(AssignedOrderView.class);
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
            // Handle logout error
            System.err.println("Error logging out: " + e.getMessage());
        }
    }

    // Getters for properties

    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    public StringProperty completionMessageProperty() {
        return completionMessage;
    }
}
