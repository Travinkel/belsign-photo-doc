package com.belman.presentation.usecases.qa.assignment;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.user.UserBusiness;
import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller for the QA order assignment view.
 * Handles UI interactions for assigning orders to production workers.
 */
public class QAOrderAssignmentViewController extends BaseController<QAOrderAssignmentViewModel> {

    @FXML
    private Label statusLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField orderSearchField;

    @FXML
    private TextField workerSearchField;

    @FXML
    private ListView<OrderBusiness> ordersListView;

    @FXML
    private ListView<UserBusiness> workersListView;

    @FXML
    private Button assignButton;

    @FXML
    private Button backButton;

    @FXML
    private Button logoutButton;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        statusLabel.textProperty().bind(getViewModel().statusMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());

        // Bind the orders list to the view model
        ordersListView.setItems(getViewModel().ordersProperty());

        // Bind the workers list to the view model
        workersListView.setItems(getViewModel().productionWorkersProperty());

        // Disable assign button when no order or worker is selected
        assignButton.disableProperty().bind(
                ordersListView.getSelectionModel().selectedItemProperty().isNull()
                .or(workersListView.getSelectionModel().selectedItemProperty().isNull())
        );

        // Set up selection listeners
        ordersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().selectedOrderProperty().set(newVal);
            }
        });

        workersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().selectedWorkerProperty().set(newVal);
            }
        });
    }

    /**
     * Handles the assign button click.
     */
    @FXML
    private void handleAssign() {
        getViewModel().assignOrderToWorker();
        
        // Clear selections after assignment
        ordersListView.getSelectionModel().clearSelection();
        workersListView.getSelectionModel().clearSelection();
    }

    /**
     * Handles the back button click.
     */
    @FXML
    private void handleBack() {
        getViewModel().navigateToQADashboard();
    }

    /**
     * Handles the logout button click.
     */
    @FXML
    private void handleLogout() {
        getViewModel().logout();
    }
}