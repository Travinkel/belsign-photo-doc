package com.belman.presentation.views.photoreview;

import com.belman.backbone.core.base.BaseController;
import com.belman.backbone.core.navigation.Router;
import com.belman.domain.entities.PhotoDocument;
import com.belman.presentation.components.TouchFriendlyDialog;
import com.belman.presentation.views.main.MainView;
import com.belman.presentation.views.photoupload.TouchFriendlyPhotoListCell;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for the photo review view.
 */
public class PhotoReviewViewController extends BaseController<PhotoReviewViewModel> {

    @FXML
    private TextField orderNumberField;

    @FXML
    private Label orderInfoLabel;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Button searchButton;

    @FXML
    private Button approveButton;

    @FXML
    private Button rejectButton;

    @FXML
    private ListView<PhotoDocument> photoListView;

    @FXML
    private Button backButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @Override
    public void initializeBinding() {
        // Bind text fields to view model properties
        orderNumberField.textProperty().bindBidirectional(getViewModel().orderNumberProperty());
        orderInfoLabel.textProperty().bind(getViewModel().orderInfoProperty());
        commentTextArea.textProperty().bindBidirectional(getViewModel().commentTextProperty());

        // Bind button states
        approveButton.disableProperty().bind(
            Bindings.not(getViewModel().photoSelectedProperty())
        );

        rejectButton.disableProperty().bind(
            Bindings.not(getViewModel().photoSelectedProperty())
            .or(Bindings.isEmpty(commentTextArea.textProperty()))
        );

        // Bind list view to photos list
        photoListView.setItems(getViewModel().getPhotos());

        // Set cell factory to use touch-friendly photo list cells
        photoListView.setCellFactory(listView -> new TouchFriendlyPhotoListCell());

        // Set selection listener
        photoListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().setSelectedPhoto(newVal);
            }
        });
    }

    /**
     * Handles the search order button action.
     */
    @FXML
    private void handleSearchOrder(ActionEvent event) {
        String orderNum = orderNumberField.getText();
        boolean found = getViewModel().searchOrder(orderNum);

        if (!found) {
            showError(getViewModel().errorMessageProperty().get());
        }
    }

    /**
     * Handles the approve photo button action.
     */
    @FXML
    private void handleApprovePhoto(ActionEvent event) {
        // Show progress indicator
        progressIndicator.setVisible(true);

        // Disable buttons during approval
        approveButton.setDisable(true);
        rejectButton.setDisable(true);

        // Approve photo in a background thread
        new Thread(() -> {
            boolean approved = getViewModel().approvePhoto();

            // Update UI on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                if (approved) {
                    showInfo("Photo approved successfully");
                } else {
                    showError(getViewModel().errorMessageProperty().get());
                }

                // Hide progress indicator and re-enable buttons
                progressIndicator.setVisible(false);
                // Buttons will be disabled if no photo is selected (handled by binding)
            });
        }).start();
    }

    /**
     * Handles the reject photo button action.
     */
    @FXML
    private void handleRejectPhoto(ActionEvent event) {
        // Show progress indicator
        progressIndicator.setVisible(true);

        // Disable buttons during rejection
        approveButton.setDisable(true);
        rejectButton.setDisable(true);

        // Reject photo in a background thread
        new Thread(() -> {
            boolean rejected = getViewModel().rejectPhoto();

            // Update UI on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                if (rejected) {
                    showInfo("Photo rejected successfully");
                } else {
                    showError(getViewModel().errorMessageProperty().get());
                }

                // Hide progress indicator and re-enable buttons
                progressIndicator.setVisible(false);
                // Buttons will be disabled if no photo is selected (handled by binding)
            });
        }).start();
    }

    /**
     * Handles the back button action.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        Router.navigateTo(MainView.class);
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