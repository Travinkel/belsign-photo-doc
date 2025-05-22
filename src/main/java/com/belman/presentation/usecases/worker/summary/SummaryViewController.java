package com.belman.presentation.usecases.worker.summary;

import com.belman.domain.photo.PhotoDocument;
import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private ListView<PhotoDocument> photoListView;

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

        // Bind the photo list to the view model
        photoListView.setItems(getViewModel().takenPhotosProperty());

        // Set up the cell factory for the photo list
        photoListView.setCellFactory(listView -> new PhotoListCell());

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

    /**
     * Custom list cell for displaying photo documents.
     */
    private static class PhotoListCell extends ListCell<PhotoDocument> {
        private final ImageView imageView = new ImageView();
        private final Label templateLabel = new Label();
        private final VBox container = new VBox(5, imageView, templateLabel);

        public PhotoListCell() {
            imageView.setFitWidth(120);
            imageView.setFitHeight(90);
            imageView.setPreserveRatio(true);
            container.getStyleClass().add("photo-list-cell");
            templateLabel.getStyleClass().add("template-label");
        }

        @Override
        protected void updateItem(PhotoDocument item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                // Set the template name with user-friendly label
                templateLabel.setText(com.belman.presentation.providers.PhotoTemplateLabelProvider.getDisplayLabel(item.getTemplate()));

                // Load the image
                try {
                    File file = new File(item.getImagePath().path());
                    if (file.exists()) {
                        Image image = new Image(new FileInputStream(file), 120, 90, true, true);
                        imageView.setImage(image);
                    } else {
                        imageView.setImage(null);
                        templateLabel.setText(com.belman.presentation.providers.PhotoTemplateLabelProvider.getDisplayLabel(item.getTemplate()) + " (Image not found)");
                    }
                } catch (FileNotFoundException e) {
                    imageView.setImage(null);
                    templateLabel.setText(com.belman.presentation.providers.PhotoTemplateLabelProvider.getDisplayLabel(item.getTemplate()) + " (Error loading image)");
                }

                setGraphic(container);
                setText(null);
            }
        }
    }
}
