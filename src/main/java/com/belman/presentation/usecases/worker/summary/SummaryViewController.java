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
    private StackPane loadingPane;

    @FXML
    private VBox emptyStatePane;

    @FXML
    private TextField dateField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField workTypeField;

    @FXML
    private TextArea notesTextArea;

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

        // Set the date field to today's date in a user-friendly format
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        dateField.setText(today.format(formatter));

        // Set default values for location and work type fields
        locationField.setPromptText("Example: Assembly Hall, Station 3");
        workTypeField.setPromptText("Example: Welding, Assembly, Inspection");

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

                if (file.exists()) {
                    // Create a photo item with the image URL
                    String fileUrl = file.toURI().toString();
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
                }
            } catch (Exception e) {
                System.err.println("Error loading photo: " + e.getMessage());
            }
        }

        // Update the photo gallery
        photoGallery.setPhotoItems(photoItems);
    }

    /**
     * Handles selection of a photo.
     *
     * @param item the selected photo item
     */
    private void handlePhotoSelected(PhotoItem item) {
        // This method can be used to show details about the selected photo
        // or to enable actions like retaking the photo
        if (item != null && item.getUserData() instanceof PhotoDocument) {
            PhotoDocument doc = (PhotoDocument) item.getUserData();
            System.out.println("Selected photo: " + doc.getTemplate());
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
        getViewModel().submitPhotos();
    }

    /**
     * Handles the back button click.
     */
    @FXML
    private void handleBackClick() {
        getViewModel().goBack();
    }

}
