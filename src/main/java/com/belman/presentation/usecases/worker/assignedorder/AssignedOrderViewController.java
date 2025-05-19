package com.belman.presentation.usecases.worker.assignedorder;

import com.belman.domain.photo.PhotoTemplate;
import com.belman.presentation.base.BaseController;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * Controller for the AssignedOrderView.
 * Handles UI interactions for displaying the worker's assigned order.
 */
public class AssignedOrderViewController extends BaseController<AssignedOrderViewModel> {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label customerLabel;

    @FXML
    private Label projectLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label dueDateLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label photoCountLabel;

    @FXML
    private Label workerNameLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ListView<PhotoTemplate> photoTemplateListView;

    // Error handling elements
    @FXML
    private Label statusLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private StackPane loadingPane;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements with null checks
        if (orderNumberLabel != null) {
            orderNumberLabel.textProperty().bind(getViewModel().orderNumberProperty());
        }

        if (customerLabel != null) {
            customerLabel.textProperty().bind(getViewModel().customerNameProperty());
        }

        if (descriptionLabel != null) {
            descriptionLabel.textProperty().bind(getViewModel().productDescriptionProperty());
        }

        if (workerNameLabel != null) {
            workerNameLabel.textProperty().bind(getViewModel().workerNameProperty());
        }

        // Bind additional properties
        if (projectLabel != null) {
            projectLabel.textProperty().bind(getViewModel().projectNameProperty());
        }

        if (locationLabel != null) {
            locationLabel.textProperty().bind(getViewModel().locationProperty());
        }

        if (dueDateLabel != null) {
            dueDateLabel.textProperty().bind(getViewModel().dueDateProperty());
        }

        if (photoCountLabel != null) {
            photoCountLabel.textProperty().bind(getViewModel().photoCountProperty());
        }

        // Set up photo template list view if available
        if (photoTemplateListView != null) {
            photoTemplateListView.itemsProperty().bind(getViewModel().photoTemplatesProperty());

            // Set cell factory to display template names
            photoTemplateListView.setCellFactory(lv -> new ListCell<PhotoTemplate>() {
                @Override
                protected void updateItem(PhotoTemplate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.name() != null ? item.name() : "Unnamed Template");
                    }
                }
            });
        }

        // Bind error handling elements
        if (statusLabel != null) {
            statusLabel.textProperty().bind(getViewModel().statusMessageProperty());
            // Add style class for status messages
            statusLabel.getStyleClass().add("status-message");
        } else {
            System.err.println("Warning: statusLabel is null in AssignedOrderViewController");
        }

        if (errorLabel != null) {
            errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
            // Add style class for error messages
            errorLabel.getStyleClass().add("error-message");
            // Hide error label when there's no error
            errorLabel.visibleProperty().bind(getViewModel().errorMessageProperty().isNotEmpty());
        } else {
            System.err.println("Warning: errorLabel is null in AssignedOrderViewController");
        }

        if (loadingPane != null) {
            loadingPane.visibleProperty().bind(getViewModel().loadingProperty());
        } else {
            System.err.println("Warning: loadingPane is null in AssignedOrderViewController");
        }

        // Set up button actions
        if (startButton != null) {
            startButton.setOnAction(e -> handleStartPhotoProcess());

            // Disable the start button when loading or when there's an error
            startButton.disableProperty().bind(
                getViewModel().loadingProperty().or(
                    getViewModel().errorMessageProperty().isNotEmpty()
                )
            );
        }

        if (logoutButton != null) {
            logoutButton.setOnAction(e -> getViewModel().logout());
        }
    }

    @Override
    public void initialize() {
        // Additional initialization if needed
    }

    /**
     * Handles the "Start Photo Process" button click.
     * This method is called when the user clicks the start button.
     */
    private void handleStartPhotoProcess() {
        // Store the current order in the WorkerFlowContext
        WorkerFlowContext.setCurrentOrder(getViewModel().getCurrentOrder());

        // Start the photo process
        getViewModel().startPhotoProcess();
    }
}
