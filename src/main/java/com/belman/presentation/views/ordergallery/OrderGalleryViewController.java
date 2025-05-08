package com.belman.presentation.views.ordergallery;

import com.belman.presentation.core.BaseController;
import com.belman.presentation.navigation.Router;
import com.belman.business.domain.user.UserAggregate;
import com.belman.business.domain.user.UserRole;
import com.belman.business.domain.order.OrderAggregate;
import com.belman.data.service.SessionManager;
import com.belman.presentation.components.TouchFriendlyDialog;
import com.belman.presentation.views.photoupload.PhotoUploadView;
import com.belman.presentation.views.qadashboard.QADashboardView;
import com.belman.presentation.views.usermanagement.UserManagementView;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for the order gallery view.
 */
public class OrderGalleryViewController extends BaseController<OrderGalleryViewModel> {

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Button filterButton;

    @FXML
    private ListView<OrderAggregate> orderListView;

    @FXML
    private TextArea orderDetailsTextArea;

    @FXML
    private TextField newOrderNumberField;

    @FXML
    private Button createOrderButton;

    @FXML
    private Button backButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label errorLabel;

    @Override
    public void setupBindings() {
        // Bind text fields to view model properties
        searchTextField.textProperty().bindBidirectional(getViewModel().searchTextProperty());
        orderDetailsTextArea.textProperty().bind(getViewModel().orderDetailsProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());

        // Bind date pickers to view model properties
        fromDatePicker.valueProperty().bindBidirectional(getViewModel().fromDateProperty());
        toDatePicker.valueProperty().bindBidirectional(getViewModel().toDateProperty());

        // Bind progress indicator to loading state
        progressIndicator.visibleProperty().bind(getViewModel().isLoadingProperty());

        // Bind list view to filtered orders
        orderListView.setItems(getViewModel().getFilteredOrders());

        // Set selection listener
        orderListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().selectOrder(newVal);
            }
        });

        // Bind create order button to non-empty order number
        createOrderButton.disableProperty().bind(
            Bindings.isEmpty(newOrderNumberField.textProperty())
        );
    }

    /**
     * Handles the search button action.
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        getViewModel().searchOrders();
    }

    /**
     * Handles the filter button action.
     */
    @FXML
    private void handleFilter(ActionEvent event) {
        getViewModel().filterByDateRange();
    }

    /**
     * Handles the create order button action.
     */
    @FXML
    private void handleCreateOrder(ActionEvent event) {
        String orderNumber = newOrderNumberField.getText();
        boolean created = getViewModel().createOrder(orderNumber);

        if (created) {
            showInfo("OrderAggregate created successfully");
            newOrderNumberField.clear();
        } else {
            showError(getViewModel().errorMessageProperty().get());
        }
    }

    /**
     * Handles the back button action.
     * Navigates to the appropriate view based on the user's role.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        // Get the current user and check their role
        SessionManager sessionManager = SessionManager.getInstance();
        if (sessionManager != null && sessionManager.isLoggedIn()) {
            UserAggregate user = sessionManager.getCurrentUser().orElse(null);
            if (user != null) {
                // Navigate to the appropriate view based on the user's role
                if (user.getRoles().contains(UserRole.ADMIN)) {
                    Router.navigateTo(UserManagementView.class);
                } else if (user.getRoles().contains(UserRole.QA)) {
                    Router.navigateTo(QADashboardView.class);
                } else if (user.getRoles().contains(UserRole.PRODUCTION)) {
                    Router.navigateTo(PhotoUploadView.class);
                } else {
                    // Fallback to PhotoUploadView if no specific role is found
                    Router.navigateTo(PhotoUploadView.class);
                }
                return;
            }
        }

        // Fallback to PhotoUploadView if no user is logged in or if an error occurs
        Router.navigateTo(PhotoUploadView.class);
    }

    /**
     * Shows an error message using a touch-friendly dialog.
     */
    @Override
    protected void showError(String message) {
        TouchFriendlyDialog.showError("Error", message);
    }

    /**
     * Shows an information message using a touch-friendly dialog.
     */
    private void showInfo(String message) {
        TouchFriendlyDialog.showInformation("Information", message);
    }
}
