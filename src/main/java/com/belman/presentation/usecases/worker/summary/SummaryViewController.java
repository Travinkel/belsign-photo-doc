package com.belman.presentation.usecases.worker.summary;

import com.belman.domain.photo.PhotoDocument;
import com.belman.presentation.base.BaseController;
import com.belman.presentation.components.PhotoGalleryComponent;
import com.belman.presentation.components.PhotoGalleryComponent.PhotoItem;
import com.belman.presentation.components.UIComponentUtils;
import com.belman.presentation.providers.PhotoTemplateLabelProvider;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the SummaryView.
 * Handles UI interactions for displaying a summary of all photos taken for an order.
 */
public class SummaryViewController extends BaseController<SummaryViewModel> {

    @FXML
    private StackPane rootPane;

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Label photosCountLabel;

    @FXML
    private StackPane photoGalleryContainer;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

    @FXML
    private Button retakePhotoButton;

    @FXML
    private StackPane loadingPane;

    @FXML
    private VBox emptyStatePane;


    // The photo gallery component
    private PhotoGalleryComponent photoGallery;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        orderNumberLabel.textProperty().bind(getViewModel().orderNumberProperty());
        statusLabel.textProperty().bind(getViewModel().statusMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loadingPane.visibleProperty().bind(getViewModel().loadingProperty());
        photosCountLabel.textProperty().bind(getViewModel().photosCountProperty().asString("%d photos taken"));


        // Create and configure the photo gallery
        setupPhotoGallery();

        // Show empty state when there are no photos
        emptyStatePane.visibleProperty().bind(getViewModel().takenPhotosProperty().emptyProperty());
        emptyStatePane.managedProperty().bind(emptyStatePane.visibleProperty());

        // Disable the submit button when there are no photos or when loading
        submitButton.disableProperty().bind(
            getViewModel().takenPhotosProperty().emptyProperty().or(
                getViewModel().loadingProperty()
            )
        );
    }

    /**
     * Sets up the photo gallery component.
     */
    private void setupPhotoGallery() {
        // Create the photo gallery component
        photoGallery = new PhotoGalleryComponent();
        photoGallery.setThumbnailSize(150.0); // Larger thumbnails for better visibility
        photoGallery.setEmptyText("No photos captured");
        photoGallery.setSelectionMode(true);

        // Clear the container before adding the photo gallery
        photoGalleryContainer.getChildren().clear();

        // Add the photo gallery to the container
        photoGalleryContainer.getChildren().add(photoGallery);

        // Set up callbacks
        photoGallery.setOnPhotoSelected(this::handlePhotoSelected);
        photoGallery.setOnPhotoDoubleClicked(this::handlePhotoDoubleClicked);

        // Listen for changes to the taken photos list
        getViewModel().takenPhotosProperty().addListener((ListChangeListener<PhotoDocument>) c -> {
            updatePhotoGallery();
        });

        // Initial update
        updatePhotoGallery();

        // Enable the retake button if a photo is selected
        if (photoGallery.getSelectedPhoto() != null) {
            retakePhotoButton.setDisable(false);
        }
    }

    /**
     * Updates the photo gallery with the current photos.
     */
    private void updatePhotoGallery() {
        List<PhotoItem> photoItems = new ArrayList<>();

        for (PhotoDocument doc : getViewModel().takenPhotosProperty()) {
            try {
                // Create a file object for the image
                File file = new File(doc.getImagePath().path());
                String fileUrl;

                if (file.exists()) {
                    // Use the existing file
                    fileUrl = file.toURI().toString();
                } else {
                    // Try to find the file in the resources directory
                    String fileName = new File(doc.getImagePath().path()).getName();
                    File resourceFile = new File("src/main/resources/photos/" + fileName);

                    if (resourceFile.exists()) {
                        fileUrl = resourceFile.toURI().toString();
                    } else {
                        // Use a placeholder image
                        fileUrl = getClass().getResource("/com/belman/assets/icons/camera.svg").toString();
                        System.out.println("Using placeholder image for " + doc.getImagePath().path());
                    }
                }

                // Create a photo item with the image URL
                PhotoItem item = new PhotoItem(fileUrl);

                // Set the caption to the template name
                item.setCaption(PhotoTemplateLabelProvider.getDisplayLabel(doc.getTemplate()));

                // Set the status based on the approval status
                if (doc.isApproved()) {
                    item.setStatus("Approved");
                } else if (doc.isPending()) {
                    item.setStatus("Pending");
                } else {
                    item.setStatus("Rejected");
                }

                // Store the photo document as user data for reference
                item.setUserData(doc);

                photoItems.add(item);
            } catch (Exception e) {
                System.err.println("Error loading photo: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Update the photo gallery
        photoGallery.setPhotoItems(photoItems);

        // Enable the retake button if a photo is selected
        if (photoGallery.getSelectedPhoto() != null) {
            retakePhotoButton.setDisable(false);
        }
    }

    /**
     * Handles selection of a photo.
     *
     * @param item the selected photo item
     */
    private void handlePhotoSelected(PhotoItem item) {
        // Enable/disable the retake button based on whether a photo is selected
        if (item != null && item.getUserData() instanceof PhotoDocument) {
            PhotoDocument doc = (PhotoDocument) item.getUserData();
            System.out.println("Selected photo: " + doc.getTemplate());

            // Enable the retake button
            retakePhotoButton.setDisable(false);
        } else {
            // Disable the retake button if no photo is selected
            retakePhotoButton.setDisable(true);
        }
    }

    /**
     * Handles double-clicking on a photo.
     *
     * @param item the double-clicked photo item
     */
    private void handlePhotoDoubleClicked(PhotoItem item) {
        // Show the photo in a larger view
        photoGallery.showZoomOverlay(item);
    }

    /**
     * Handles the submit button click.
     */
    @FXML
    private void handleSubmitClick() {
        try {
            // Show a confirmation dialog before submitting photos
            UIComponentUtils.showConfirmation(
                rootPane,
                "Submit Photos",
                "Are you sure you want to submit these photos? This action cannot be undone.\n\n" +
                "After submission, the photos will be sent to the Quality Assurance team for review.",
                "Submit",
                "Cancel",
                confirmed -> {
                    if (confirmed) {
                        // User confirmed, submit the photos
                        getViewModel().submitPhotos();
                    }
                }
            );
        } catch (IOException e) {
            System.err.println("Error showing confirmation dialog: " + e.getMessage());
            // Fallback to direct submission if the dialog fails
            getViewModel().submitPhotos();
        }
    }

    /**
     * Handles the back button click.
     */
    @FXML
    private void handleBackClick() {
        getViewModel().goBack();
    }

    /**
     * Handles the retake photo button click.
     * This method is called when the user clicks the "Retake Selected Photo" button.
     */
    @FXML
    private void handleRetakePhotoClick() {
        // Get the selected photo from the photo gallery
        PhotoItem selectedItem = photoGallery.getSelectedPhoto();

        if (selectedItem == null || !(selectedItem.getUserData() instanceof PhotoDocument)) {
            // No photo selected or invalid user data
            System.out.println("No photo selected or invalid user data");
            return;
        }

        // Get the photo document from the selected item
        PhotoDocument photoDocument = (PhotoDocument) selectedItem.getUserData();
        System.out.println("Retaking photo for template: " + photoDocument.getTemplate().name());

        // Call the view model to handle retaking the photo
        getViewModel().retakePhoto(photoDocument);
    }

}
