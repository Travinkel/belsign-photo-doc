package com.belman.ui.usecases.photo.review;

import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.ui.session.SessionManager;
import com.belman.ui.base.BaseController;
import com.belman.ui.components.TouchFriendlyDialog;
import com.belman.ui.navigation.Router;
import com.belman.ui.usecases.admin.usermanagement.UserManagementView;
import com.belman.ui.usecases.photo.upload.PhotoUploadView;
import com.belman.ui.usecases.photo.upload.TouchFriendlyPhotoListCell;
import com.belman.ui.usecases.qa.dashboard.QADashboardView;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    public void setupBindings() {
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
     * Shows an error message using a touch-friendly dialog.
     */
    @Override
    protected void showError(String message) {
        TouchFriendlyDialog.showError("Error", message);
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
     * Shows an information message using a touch-friendly dialog.
     */
    protected void showInfo(String message) {
        TouchFriendlyDialog.showInformation("Information", message);
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
                    // Fallback to QADashboardView if no specific role is found
                    Router.navigateTo(QADashboardView.class);
                }
                return;
            }
        }

        // Fallback to QADashboardView if no user is logged in or if an error occurs
        Router.navigateTo(QADashboardView.class);
    }
}