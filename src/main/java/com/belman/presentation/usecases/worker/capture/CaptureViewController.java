package com.belman.presentation.usecases.worker.capture;

import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Controller for the CaptureView.
 * Handles UI interactions for capturing photos for a specific template.
 */
public class CaptureViewController extends BaseController<CaptureViewModel> {

    @FXML
    private Label templateNameLabel;

    @FXML
    private Label templateDescriptionLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView previewImageView;

    @FXML
    private Button takePhotoButton;

    @FXML
    private Button retakePhotoButton;

    @FXML
    private Button confirmPhotoButton;

    @FXML
    private Button cancelButton;

    @FXML
    private HBox photoActionsPane;

    @FXML
    private HBox reviewActionsPane;

    @FXML
    private StackPane loadingPane;

    @FXML
    private VBox cameraUnavailablePane;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        templateNameLabel.textProperty().bind(getViewModel().templateNameProperty());
        templateDescriptionLabel.textProperty().bind(getViewModel().templateDescriptionProperty());
        statusLabel.textProperty().bind(getViewModel().statusMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loadingPane.visibleProperty().bind(getViewModel().loadingProperty());

        // Show/hide action panes based on whether a photo has been taken
        photoActionsPane.visibleProperty().bind(getViewModel().photoTakenProperty().not());
        photoActionsPane.managedProperty().bind(getViewModel().photoTakenProperty().not());
        reviewActionsPane.visibleProperty().bind(getViewModel().photoTakenProperty());
        reviewActionsPane.managedProperty().bind(getViewModel().photoTakenProperty());

        // Update the preview image when a photo is taken
        getViewModel().capturedPhotoFileProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    Image image = new Image(new FileInputStream(newVal));
                    previewImageView.setImage(image);
                } catch (FileNotFoundException e) {
                    errorLabel.setText("Error loading preview image: " + e.getMessage());
                }
            } else {
                previewImageView.setImage(null);
            }
        });

        // Show camera unavailable pane if camera is not available
        cameraUnavailablePane.visibleProperty().bind(
            getViewModel().errorMessageProperty().isNotEmpty().and(
                getViewModel().errorMessageProperty().isEqualTo("Camera is not available on this device.")
            )
        );
        cameraUnavailablePane.managedProperty().bind(cameraUnavailablePane.visibleProperty());
    }

    /**
     * Handles the take photo button click.
     */
    @FXML
    private void handleTakePhotoClick() {
        getViewModel().takePhoto();
    }

    /**
     * Handles the retake photo button click.
     */
    @FXML
    private void handleRetakePhotoClick() {
        getViewModel().retakePhoto();
    }

    /**
     * Handles the confirm photo button click.
     */
    @FXML
    private void handleConfirmPhotoClick() {
        getViewModel().confirmPhoto();
    }

    /**
     * Handles the cancel button click.
     */
    @FXML
    private void handleCancelClick() {
        getViewModel().cancel();
    }
}