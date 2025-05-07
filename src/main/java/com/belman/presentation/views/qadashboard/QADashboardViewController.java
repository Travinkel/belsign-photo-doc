package com.belman.presentation.views.qadashboard;

import com.belman.presentation.core.BaseController;
import com.belman.presentation.components.TouchFriendlyDialog;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

/**
 * Controller for the QA dashboard view.
 * Provides access to QA-specific functionality like photo review and report generation.
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

    @Override
    public void initializeBinding() {
        // Bind UI components to ViewModel properties
        welcomeLabel.textProperty().bind(getViewModel().welcomeMessageProperty());
        searchField.textProperty().bindBidirectional(getViewModel().searchTextProperty());

        // Bind button states
        reviewPhotosButton.disableProperty().bind(
            Bindings.isNull(pendingOrdersListView.getSelectionModel().selectedItemProperty())
        );

        generateReportButton.disableProperty().bind(
            Bindings.isNull(pendingOrdersListView.getSelectionModel().selectedItemProperty())
        );

        // Bind list view to pending orders
        pendingOrdersListView.setItems(getViewModel().getPendingOrders());

        // Set selection listener
        pendingOrdersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().selectOrder(newVal);
            }
        });

        // Initialize data
        getViewModel().loadPendingOrders();
    }

    /**
     * Handles the search button action.
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        getViewModel().searchOrders();
    }

    /**
     * Handles the review photos button action.
     */
    @FXML
    private void handleReviewPhotos(ActionEvent event) {
        String selectedOrder = pendingOrdersListView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            getViewModel().navigateToPhotoReview(selectedOrder);
        }
    }

    /**
     * Handles the generate report button action.
     */
    @FXML
    private void handleGenerateReport(ActionEvent event) {
        String selectedOrder = pendingOrdersListView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            boolean success = getViewModel().generateReport(selectedOrder);
            if (success) {
                showInfo("ReportAggregate generated successfully");
            } else {
                showError(getViewModel().errorMessageProperty().get());
            }
        }
    }

    /**
     * Shows an error message using a touch-friendly dialog.
     */
    private void showError(String message) {
        TouchFriendlyDialog.showError("Error", message);
    }

    /**
     * Shows an information message using a touch-friendly dialog.
     */
    private void showInfo(String message) {
        TouchFriendlyDialog.showInformation("Information", message);
    }
}
