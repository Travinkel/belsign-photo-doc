package com.belman.ui.views.photoupload;

import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.services.CameraService;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.bootstrap.camera.CameraServiceFactory;
import com.belman.service.session.SessionManager;
import com.belman.ui.base.BaseController;
import com.belman.ui.components.TouchFriendlyDialog;
import com.belman.ui.navigation.Router;
import com.belman.ui.views.qadashboard.QADashboardView;
import com.belman.ui.views.usermanagement.UserManagementView;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;

/**
 * Controller for the photo upload view.
 */
public class PhotoUploadViewController extends BaseController<PhotoUploadViewModel> {

    @FXML
    private TextField orderNumberField;

    @FXML
    private Label orderInfoLabel;

    @FXML
    private TextField angleField;

    @FXML
    private Button takePhotoButton;

    @FXML
    private Button selectPhotoButton;

    @FXML
    private Button uploadButton;

    @FXML
    private ListView<PhotoDocument> photoListView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    @FXML
    private ProgressIndicator progressIndicator;

    private File selectedPhotoFile;

    @Override
    public void setupBindings() {
        // Bind text fields to view model properties
        orderNumberField.textProperty().bindBidirectional(getViewModel().orderNumberProperty());
        orderInfoLabel.textProperty().bind(getViewModel().orderInfoProperty());
        angleField.textProperty().bindBidirectional(getViewModel().photoAngleProperty());

        // Bind button states
        uploadButton.disableProperty().bind(
                Bindings.not(getViewModel().photoSelectedProperty())
                        .or(Bindings.not(getViewModel().orderSelectedProperty()))
        );

        deleteButton.disableProperty().bind(
                Bindings.isNull(photoListView.getSelectionModel().selectedItemProperty())
        );

        // Bind list view to photos list
        photoListView.setItems(getViewModel().getPhotos());

        // Set cell factory to use touch-friendly photo list cells
        photoListView.setCellFactory(listView -> new TouchFriendlyPhotoListCell());
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
     * Handles the take photo button action.
     * Uses the CameraService to take a photo with the device camera.
     */
    @FXML
    private void handleTakePhoto(ActionEvent event) {
        CameraService cameraService = getCameraService();

        if (cameraService.isCameraAvailable()) {
            // Show progress indicator
            progressIndicator.setVisible(true);

            // Disable buttons while taking photo
            takePhotoButton.setDisable(true);
            selectPhotoButton.setDisable(true);

            // Take photo in a background thread
            new Thread(() -> {
                try {
                    cameraService.takePhoto().ifPresentOrElse(file -> {
                        // Update UI on JavaFX thread
                        javafx.application.Platform.runLater(() -> {
                            selectedPhotoFile = file;
                            getViewModel().setSelectedPhotoFile(file);
                            showInfo("Photo taken successfully");

                            // Hide progress indicator and re-enable buttons
                            progressIndicator.setVisible(false);
                            takePhotoButton.setDisable(false);
                            selectPhotoButton.setDisable(false);
                        });
                    }, () -> {
                        // Handle case where no photo was taken (user cancelled)
                        javafx.application.Platform.runLater(() -> {
                            showInfo("Photo capture cancelled");

                            // Hide progress indicator and re-enable buttons
                            progressIndicator.setVisible(false);
                            takePhotoButton.setDisable(false);
                            selectPhotoButton.setDisable(false);
                        });
                    });
                } catch (Exception e) {
                    // Handle errors
                    javafx.application.Platform.runLater(() -> {
                        showError("Error taking photo: " + e.getMessage());

                        // Hide progress indicator and re-enable buttons
                        progressIndicator.setVisible(false);
                        takePhotoButton.setDisable(false);
                        selectPhotoButton.setDisable(false);
                    });
                }
            }).start();
        } else {
            showError("Camera is not available on this device");
        }
    }

    /**
     * Gets a CameraService instance appropriate for the current platform.
     *
     * @return a CameraService instance
     */
    private CameraService getCameraService() {
        return CameraServiceFactory.getCameraService();
    }

    /**
     * Shows an information message using a touch-friendly dialog.
     */
    protected void showInfo(String message) {
        TouchFriendlyDialog.showInformation("Information", message);
    }

    /**
     * Handles the select photo button action.
     * Uses the CameraService to select a photo from the gallery.
     */
    @FXML
    private void handleSelectPhoto(ActionEvent event) {
        CameraService cameraService = getCameraService();

        if (cameraService.isGalleryAvailable()) {
            // Show progress indicator
            progressIndicator.setVisible(true);

            // Disable buttons while selecting photo
            takePhotoButton.setDisable(true);
            selectPhotoButton.setDisable(true);

            // Select photo in a background thread
            new Thread(() -> {
                try {
                    cameraService.selectPhoto().ifPresentOrElse(file -> {
                        // Update UI on JavaFX thread
                        javafx.application.Platform.runLater(() -> {
                            selectedPhotoFile = file;
                            getViewModel().setSelectedPhotoFile(file);
                            showInfo("Photo selected successfully");

                            // Hide progress indicator and re-enable buttons
                            progressIndicator.setVisible(false);
                            takePhotoButton.setDisable(false);
                            selectPhotoButton.setDisable(false);
                        });
                    }, () -> {
                        // Handle case where no photo was selected (user cancelled)
                        javafx.application.Platform.runLater(() -> {
                            showInfo("Photo selection cancelled");

                            // Hide progress indicator and re-enable buttons
                            progressIndicator.setVisible(false);
                            takePhotoButton.setDisable(false);
                            selectPhotoButton.setDisable(false);
                        });
                    });
                } catch (Exception e) {
                    // Handle errors
                    javafx.application.Platform.runLater(() -> {
                        showError("Error selecting photo: " + e.getMessage());

                        // Hide progress indicator and re-enable buttons
                        progressIndicator.setVisible(false);
                        takePhotoButton.setDisable(false);
                        selectPhotoButton.setDisable(false);
                    });
                }
            }).start();
        } else {
            showError("Photo gallery is not available on this device");
        }
    }

    /**
     * Handles the upload button action.
     */
    @FXML
    private void handleUpload(ActionEvent event) {
        // Show progress indicator
        progressIndicator.setVisible(true);

        // Disable buttons during upload
        takePhotoButton.setDisable(true);
        selectPhotoButton.setDisable(true);
        uploadButton.setDisable(true);

        // Upload photo in a background thread
        new Thread(() -> {
            boolean uploaded = getViewModel().uploadPhoto();

            // Update UI on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                if (uploaded) {
                    showInfo("Photo uploaded successfully");
                } else {
                    showError(getViewModel().errorMessageProperty().get());
                }

                // Hide progress indicator and re-enable buttons
                progressIndicator.setVisible(false);
                takePhotoButton.setDisable(false);
                selectPhotoButton.setDisable(false);
                // uploadButton will be disabled if no photo is selected (handled by binding)
            });
        }).start();
    }

    /**
     * Handles the delete photo button action.
     */
    @FXML
    private void handleDeletePhoto(ActionEvent event) {
        PhotoDocument selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null) {
            // Show progress indicator
            progressIndicator.setVisible(true);

            // Disable buttons during delete
            takePhotoButton.setDisable(true);
            selectPhotoButton.setDisable(true);
            deleteButton.setDisable(true);

            // Delete photo in a background thread
            new Thread(() -> {
                boolean deleted = getViewModel().deletePhoto(selectedPhoto);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    if (deleted) {
                        showInfo("Photo deleted successfully");
                    } else {
                        showError(getViewModel().errorMessageProperty().get());
                    }

                    // Hide progress indicator and re-enable buttons
                    progressIndicator.setVisible(false);
                    takePhotoButton.setDisable(false);
                    selectPhotoButton.setDisable(false);
                    // deleteButton will be disabled if no photo is selected (handled by binding)
                });
            }).start();
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
