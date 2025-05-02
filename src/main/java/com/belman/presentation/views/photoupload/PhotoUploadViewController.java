package com.belman.presentation.views.photoupload;

import com.belman.backbone.core.base.BaseController;
import com.belman.backbone.core.navigation.Router;
import com.belman.backbone.core.util.PlatformUtils;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.services.CameraService;
import com.belman.infrastructure.service.MockCameraService;
import com.belman.presentation.components.TouchFriendlyDialog;
import com.belman.presentation.views.main.MainView;
import com.belman.presentation.views.photoupload.TouchFriendlyPhotoListCell;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private File selectedPhotoFile;

    @Override
    public void initializeBinding() {
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
     * Gets a CameraService instance appropriate for the current platform.
     * 
     * @return a CameraService instance
     */
    private CameraService getCameraService() {
        // For now, we'll use the MockCameraService for all platforms
        // In a real implementation, we would use a platform-specific implementation
        return new MockCameraService();
    }

    /**
     * Handles the take photo button action.
     * Uses the CameraService to take a photo with the device camera.
     */
    @FXML
    private void handleTakePhoto(ActionEvent event) {
        CameraService cameraService = getCameraService();

        if (cameraService.isCameraAvailable()) {
            cameraService.takePhoto().ifPresent(file -> {
                selectedPhotoFile = file;
                getViewModel().setSelectedPhotoFile(file);
                showInfo("Photo taken successfully");
            });
        } else {
            showError("Camera is not available on this device");
        }
    }

    /**
     * Handles the select photo button action.
     * Uses the CameraService to select a photo from the gallery.
     */
    @FXML
    private void handleSelectPhoto(ActionEvent event) {
        CameraService cameraService = getCameraService();

        if (cameraService.isGalleryAvailable()) {
            cameraService.selectPhoto().ifPresent(file -> {
                selectedPhotoFile = file;
                getViewModel().setSelectedPhotoFile(file);
                showInfo("Photo selected successfully");
            });
        } else {
            showError("Photo gallery is not available on this device");
        }
    }

    /**
     * Handles the upload button action.
     */
    @FXML
    private void handleUpload(ActionEvent event) {
        boolean uploaded = getViewModel().uploadPhoto();

        if (uploaded) {
            showInfo("Photo uploaded successfully");
        } else {
            showError(getViewModel().errorMessageProperty().get());
        }
    }

    /**
     * Handles the delete photo button action.
     */
    @FXML
    private void handleDeletePhoto(ActionEvent event) {
        PhotoDocument selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null) {
            boolean deleted = getViewModel().deletePhoto(selectedPhoto);

            if (deleted) {
                showInfo("Photo deleted successfully");
            } else {
                showError(getViewModel().errorMessageProperty().get());
            }
        }
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
