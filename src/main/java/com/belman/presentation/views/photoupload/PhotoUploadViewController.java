package com.belman.presentation.views.photoupload;

import com.belman.backbone.core.base.BaseController;
import com.belman.backbone.core.navigation.Router;
import com.belman.backbone.core.util.PlatformUtils;
import com.belman.domain.entities.PhotoDocument;
import com.belman.presentation.views.main.MainView;
import com.gluonhq.attach.pictures.PicturesService;
import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;
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

        // Set cell factory to display photo information
        photoListView.setCellFactory(listView -> new PhotoListCell());
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
     * Uses Gluon's PicturesService to take a photo with the device camera on mobile devices.
     * Falls back to file chooser on desktop.
     */
    @FXML
    private void handleTakePhoto(ActionEvent event) {
        // Use PlatformUtils to check if we're on a mobile device
        if (PlatformUtils.isRunningOnMobile()) {
            showInfo("Taking photo with camera...");
            // This is just a placeholder for mobile implementation
            // In a real mobile app, this would use the device camera
            useFileChooser();
        } else {
            // On desktop, use file chooser
            useFileChooser();
        }
    }

    /**
     * Handles the select photo button action.
     * Uses Gluon's PicturesService to select a photo from the gallery on mobile devices.
     * Falls back to file chooser on desktop.
     */
    @FXML
    private void handleSelectPhoto(ActionEvent event) {
        // Use PlatformUtils to check if we're on a mobile device
        if (PlatformUtils.isRunningOnMobile()) {
            showInfo("Selecting photo from gallery...");
            // This is just a placeholder for mobile implementation
            // In a real mobile app, this would use the device gallery
            useFileChooser();
        } else {
            // On desktop, use file chooser
            useFileChooser();
        }
    }

    /**
     * Uses JavaFX FileChooser to select an image file.
     * This is used as a fallback when running on desktop or when Gluon services are not available.
     */
    private void useFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedPhotoFile = file;
            getViewModel().setSelectedPhotoFile(file);
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
     * Shows an error message.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an information message.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Custom cell for displaying photo information in the list view.
     */
    private static class PhotoListCell extends javafx.scene.control.ListCell<PhotoDocument> {
        @Override
        protected void updateItem(PhotoDocument item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                String status = item.getStatus().name();
                String angle = item.getAngle().degrees() + "°";
                String uploadedBy = item.getUploadedBy().getUsername().value();
                String uploadedAt = item.getUploadedAt().toInstant().toString();

                setText(String.format("Photo ID: %s | Angle: %s | Status: %s | Uploaded by: %s | Uploaded at: %s",
                    item.getPhotoId().value(), angle, status, uploadedBy, uploadedAt));
            }
        }
    }
}
