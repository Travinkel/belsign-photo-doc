package com.belman.presentation.usecases.qa.review;

import com.belman.domain.photo.PhotoDocument;
import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Controller for the photo review view.
 * Handles UI interactions for reviewing photos of an order.
 */
public class PhotoReviewViewController extends BaseController<PhotoReviewViewModel> {

    @FXML
    private TextField orderNumberField;

    @FXML
    private Label orderInfoLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private ListView<PhotoDocument> photoListView;

    @FXML
    private ImageView photoPreviewImageView;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Button approveButton;

    @FXML
    private Button rejectButton;

    @FXML
    private StackPane loadingPane;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        orderNumberField.textProperty().bindBidirectional(getViewModel().orderNumberProperty());
        orderInfoLabel.textProperty().bind(getViewModel().orderInfoProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
        commentTextArea.textProperty().bindBidirectional(getViewModel().commentProperty());

        // Bind the photo list to the view model
        photoListView.setItems(getViewModel().getPhotos());

        // Set up selection listener for the photo list
        photoListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().selectPhoto(newVal);
                displayPhoto(newVal);
            }
        });
    }

    /**
     * Displays a photo in the preview image view.
     *
     * @param photo the photo to display
     */
    private void displayPhoto(PhotoDocument photo) {
        try {
            File file = new File(photo.getImagePath().path());
            if (file.exists()) {
                Image image = new Image(new FileInputStream(file));
                photoPreviewImageView.setImage(image);
            } else {
                photoPreviewImageView.setImage(null);
                errorLabel.setText("Photo file not found: " + photo.getImagePath().path());
            }
        } catch (FileNotFoundException e) {
            photoPreviewImageView.setImage(null);
            errorLabel.setText("Error loading photo: " + e.getMessage());
        }
    }

    /**
     * Handles the search order button click.
     */
    @FXML
    private void handleSearchOrder() {
        String orderNumber = orderNumberField.getText();
        if (orderNumber != null && !orderNumber.isEmpty()) {
            getViewModel().loadOrder(orderNumber);
        } else {
            errorLabel.setText("Please enter an order number");
        }
    }

    /**
     * Handles the approve button click.
     */
    @FXML
    private void handleApproveOrder() {
        getViewModel().approveOrder();
    }

    /**
     * Handles the reject button click.
     */
    @FXML
    private void handleRejectOrder() {
        getViewModel().rejectOrder();
    }

    /**
     * Handles the back button click.
     */
    @FXML
    private void handleBack() {
        getViewModel().navigateBack();
    }
}