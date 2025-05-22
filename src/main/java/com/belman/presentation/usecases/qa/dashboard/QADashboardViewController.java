package com.belman.presentation.usecases.qa.dashboard;

import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

/**
 * Controller for the QA dashboard view.
 * Handles UI interactions for the QA dashboard screen.
 */
public class QADashboardViewController extends BaseController<QADashboardViewModel> {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<String> pendingOrdersListView;

    @FXML
    private Button reviewPhotosButton;

    @FXML
    private Button generateReportButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label errorLabel;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        welcomeLabel.textProperty().bind(getViewModel().welcomeMessageProperty());
        searchField.textProperty().bindBidirectional(getViewModel().searchTextProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());

        // Bind the pending orders list to the view model
        pendingOrdersListView.setItems(getViewModel().getPendingOrders());

        // Disable buttons when no order is selected
        reviewPhotosButton.disableProperty().bind(
                pendingOrdersListView.getSelectionModel().selectedItemProperty().isNull()
        );
        generateReportButton.disableProperty().bind(
                pendingOrdersListView.getSelectionModel().selectedItemProperty().isNull()
        );

        // Set up selection listener for the pending orders list
        pendingOrdersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().selectOrder(newVal);
            }
        });
    }

    /**
     * Handles the search button click.
     */
    @FXML
    private void handleSearch() {
        getViewModel().searchOrders();
    }

    /**
     * Handles the review photos button click.
     */
    @FXML
    private void handleReviewPhotos() {
        String selectedOrder = pendingOrdersListView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            getViewModel().navigateToPhotoReview(selectedOrder);
        }
    }

    /**
     * Handles the generate report button click.
     */
    @FXML
    private void handleGenerateReport() {
        getViewModel().generateReportPreview();
    }

    /**
     * Handles the logout button click.
     */
    @FXML
    private void handleLogout() {
        getViewModel().logout();
    }
}
