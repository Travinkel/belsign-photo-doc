package com.belman.ui.usecases.order.gallery;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.ui.session.SessionManager;
import com.belman.ui.base.BaseController;
import com.belman.ui.components.TouchFriendlyDialog;
import com.belman.ui.navigation.Router;
import com.belman.ui.usecases.admin.usermanagement.UserManagementView;
import com.belman.ui.usecases.photo.upload.PhotoUploadView;
import com.belman.ui.usecases.qa.dashboard.QADashboardView;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private ListView<OrderBusiness> orderListView;

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
     * Shows an error message using a touch-friendly dialog.
     */
    @Override
    protected void showError(String message) {
        TouchFriendlyDialog.showError("Error", message);
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
            showInfo("OrderBusiness created successfully");
            newOrderNumberField.clear();
        } else {
            showError(getViewModel().errorMessageProperty().get());
        }
    }

    /**
     * Shows an information message using a touch-friendly dialog.
     */
    private void showInfo(String message) {
        TouchFriendlyDialog.showInformation("Information", message);
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
            UserBusiness user = sessionManager.getCurrentUser().orElse(null);
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
}