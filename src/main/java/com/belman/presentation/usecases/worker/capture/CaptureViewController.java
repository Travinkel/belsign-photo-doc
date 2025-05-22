package com.belman.presentation.usecases.worker.capture;

import com.belman.common.logging.EmojiLogger;
import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Controller for the CaptureView.
 * Handles UI interactions for capturing photos for a specific template.
 */
public class CaptureViewController extends BaseController<CaptureViewModel> {

    private static final EmojiLogger logger = EmojiLogger.getLogger(CaptureViewController.class);

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
    private ScrollPane mockImagesScrollPane;

    @FXML
    private GridPane mockImagesGrid;

    @FXML
    private StackPane imagePreviewPane;

    @FXML
    private Button backToGridButton;

    @FXML
    private Button selectAnotherButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @FXML
    private HBox previewControls;

    @FXML
    private StackPane loadingIndicator;

    // Constants for the mock image grid
    private static final int THUMBNAIL_SIZE = 100;
    private static final int COLUMNS = 3;

    // Currently selected mock image
    private File selectedMockImage;

    @Override
    protected void setupBindings() {
        logger.debug("Setting up bindings for CaptureViewController");

        // Bind view model properties to UI elements
        logger.debug("Binding view model properties to UI elements");
        templateNameLabel.textProperty().bind(getViewModel().templateNameProperty());
        templateDescriptionLabel.textProperty().bind(getViewModel().templateDescriptionProperty());
        statusLabel.textProperty().bind(getViewModel().statusMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loadingIndicator.visibleProperty().bind(getViewModel().loadingProperty());

        // Show/hide preview pane based on whether a photo has been taken
        logger.debug("Setting up preview pane visibility bindings");
        imagePreviewPane.visibleProperty().bind(getViewModel().photoTakenProperty());
        imagePreviewPane.managedProperty().bind(getViewModel().photoTakenProperty());

        // Show/hide preview controls based on whether a photo has been taken
        logger.debug("Setting up preview controls visibility bindings");
        previewControls.visibleProperty().bind(getViewModel().photoTakenProperty());
        previewControls.managedProperty().bind(getViewModel().photoTakenProperty());

        // Update the preview image when a photo is taken
        logger.debug("Setting up captured photo listener");
        getViewModel().capturedPhotoFileProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                logger.debug("Loading preview image from file: {}", newVal.getAbsolutePath());
                try {
                    Image image = new Image(new FileInputStream(newVal));
                    previewImageView.setImage(image);
                    logger.debug("Preview image loaded successfully");
                } catch (FileNotFoundException e) {
                    logger.error("Error loading preview image", e);
                    errorLabel.setText("Error loading preview image: " + e.getMessage());
                }
            } else {
                logger.debug("Clearing preview image");
                previewImageView.setImage(null);
            }
        });

        // Setup event handlers for buttons
        logger.debug("Setting up button event handlers");
        backToGridButton.setOnAction(e -> handleBackToGridClick());
        selectAnotherButton.setOnAction(e -> handleSelectAnotherClick());
        confirmButton.setOnAction(e -> handleConfirmClick());

        // Load mock images when they become available
        logger.debug("Setting up mock images loaded listener");
        getViewModel().mockImagesLoadedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                logger.debug("Mock images loaded, populating grid");
                populateMockImagesGrid();
            }
        });
    }

    /**
     * Populates the mock images grid with thumbnails of the available mock images.
     */
    private void populateMockImagesGrid() {
        logger.debug("Populating mock images grid");

        // Clear the grid
        mockImagesGrid.getChildren().clear();
        logger.debug("Cleared existing images from grid");

        // Get the available mock images
        List<File> mockImages = getViewModel().getAvailableMockImages();
        logger.debug("Retrieved {} mock images from view model", mockImages.size());

        // Add each image to the grid
        int row = 0;
        int col = 0;

        for (File imageFile : mockImages) {
            try {
                logger.trace("Loading thumbnail for image: {}", imageFile.getName());

                // Create a thumbnail
                ImageView thumbnail = new ImageView(new Image(new FileInputStream(imageFile), THUMBNAIL_SIZE, THUMBNAIL_SIZE, true, true));
                thumbnail.setFitWidth(THUMBNAIL_SIZE);
                thumbnail.setFitHeight(THUMBNAIL_SIZE);

                // Add a click handler
                thumbnail.setOnMouseClicked(e -> handleMockImageClick(imageFile));

                // Add to the grid
                mockImagesGrid.add(thumbnail, col, row);
                logger.trace("Added thumbnail to grid at position ({}, {})", col, row);

                // Update row and column
                col++;
                if (col >= COLUMNS) {
                    col = 0;
                    row++;
                }
            } catch (FileNotFoundException e) {
                logger.error("Error loading mock image: {}", imageFile.getName(), e);
                errorLabel.setText("Error loading mock image: " + e.getMessage());
            }
        }

        logger.debug("Finished populating mock images grid with {} images", mockImages.size());
    }

    /**
     * Handles a click on a mock image in the grid.
     * 
     * @param imageFile the image file that was clicked
     */
    private void handleMockImageClick(File imageFile) {
        logger.debug("Mock image clicked: {}", imageFile.getName());

        // Store the selected image
        selectedMockImage = imageFile;
        logger.debug("Stored selected mock image");

        // Tell the view model about the selection
        getViewModel().selectMockImage(imageFile);
        logger.debug("Notified view model about image selection");

        // Show the preview pane
        imagePreviewPane.setVisible(true);
        logger.debug("Made preview pane visible");
    }

    /**
     * Handles a click on the "Back to Grid" button.
     */
    private void handleBackToGridClick() {
        logger.debug("Back to Grid button clicked");

        // Hide the preview pane
        imagePreviewPane.setVisible(false);
        logger.debug("Hidden preview pane");

        // Clear the selection
        selectedMockImage = null;
        getViewModel().retakePhoto();
        logger.debug("Cleared selection and notified view model to retake photo");
    }

    /**
     * Handles a click on the "Select Another" button.
     */
    private void handleSelectAnotherClick() {
        logger.debug("Select Another button clicked");
        // Same as back to grid
        handleBackToGridClick();
    }

    /**
     * Handles a click on the "Confirm" button.
     */
    private void handleConfirmClick() {
        logger.info("Confirm button clicked, confirming photo");
        // Confirm the selected image
        getViewModel().confirmPhoto();
        logger.info("Photo confirmed successfully");
    }

    /**
     * Handles the cancel button click.
     */
    @FXML
    private void handleCancelClick() {
        logger.info("Cancel button clicked, cancelling photo capture");
        getViewModel().cancel();
        logger.debug("Photo capture cancelled");
    }
}
