package com.belman.presentation.usecases.worker.assignedorder;

import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Controller for the AssignedOrderView.
 * Handles UI interactions for displaying the worker's assigned order.
 */
public class AssignedOrderViewController extends BaseController<AssignedOrderViewModel> {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label productDescriptionLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button startPhotoProcessButton;

    @FXML
    private StackPane loadingPane;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        orderNumberLabel.textProperty().bind(getViewModel().orderNumberProperty());
        customerNameLabel.textProperty().bind(getViewModel().customerNameProperty());
        productDescriptionLabel.textProperty().bind(getViewModel().productDescriptionProperty());
        statusLabel.textProperty().bind(getViewModel().statusMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loadingPane.visibleProperty().bind(getViewModel().loadingProperty());

        // Disable the start button when loading or when there's an error
        startPhotoProcessButton.disableProperty().bind(
            getViewModel().loadingProperty().or(
                getViewModel().errorMessageProperty().isNotEmpty()
            )
        );
    }

    @Override
    public void initialize() {
        // Additional initialization if needed
    }

    /**
     * Handles the "Start Photo Process" button click.
     */
    @FXML
    private void handleStartPhotoProcess() {
        getViewModel().startPhotoProcess();
    }
}
