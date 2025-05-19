package com.belman.presentation.usecases.worker.photocube;

import com.belman.domain.photo.PhotoTemplate;
import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the PhotoCubeView.
 * Handles UI interactions for the unfolded cube layout.
 */
public class PhotoCubeViewController extends BaseController<PhotoCubeViewModel> {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private GridPane cubeGridPane;

    @FXML
    private Button topButton;

    @FXML
    private Button frontButton;

    @FXML
    private Button backButton;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;

    @FXML
    private Button bottomButton;

    @FXML
    private Button summaryButton;

    @FXML
    private StackPane loadingPane;

    // Map to store template buttons
    private final Map<PhotoTemplate, Button> templateButtons = new HashMap<>();

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        orderNumberLabel.textProperty().bind(getViewModel().orderNumberProperty());
        statusLabel.textProperty().bind(getViewModel().statusMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loadingPane.visibleProperty().bind(getViewModel().loadingProperty());

        // Disable the summary button when loading or when not all photos are taken
        summaryButton.disableProperty().bind(
            getViewModel().loadingProperty().or(
                getViewModel().photosCompletedProperty().lessThan(
                    getViewModel().totalPhotosRequiredProperty()
                )
            )
        );

        // Initialize the template buttons map
        initializeTemplateButtons();

        // Update button states based on completion status
        getViewModel().templateCompletionStatusProperty().addListener((obs, oldVal, newVal) -> {
            updateButtonStates();
        });
    }

    /**
     * Initializes the template buttons map.
     */
    private void initializeTemplateButtons() {
        templateButtons.put(PhotoTemplate.TOP_VIEW_OF_JOINT, topButton);
        templateButtons.put(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY, frontButton);
        templateButtons.put(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY, backButton);
        templateButtons.put(PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY, leftButton);
        templateButtons.put(PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY, rightButton);
        templateButtons.put(PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY, bottomButton);
    }

    /**
     * Updates the button states based on the completion status.
     */
    private void updateButtonStates() {
        for (Map.Entry<PhotoTemplate, Button> entry : templateButtons.entrySet()) {
            PhotoTemplate template = entry.getKey();
            Button button = entry.getValue();

            // Get the completion status for this template
            Boolean isCompleted = getViewModel().templateCompletionStatusProperty().get(template);

            // Update the button style based on completion status
            if (isCompleted != null && isCompleted) {
                button.getStyleClass().add("template-completed");
                button.getStyleClass().remove("template-pending");
            } else {
                button.getStyleClass().add("template-pending");
                button.getStyleClass().remove("template-completed");
            }
        }
    }

    /**
     * Handles the top button click.
     */
    @FXML
    private void handleTopButtonClick() {
        getViewModel().selectTemplate(PhotoTemplate.TOP_VIEW_OF_JOINT);
    }

    /**
     * Handles the front button click.
     */
    @FXML
    private void handleFrontButtonClick() {
        getViewModel().selectTemplate(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY);
    }

    /**
     * Handles the back button click.
     */
    @FXML
    private void handleBackButtonClick() {
        getViewModel().selectTemplate(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY);
    }

    /**
     * Handles the left button click.
     */
    @FXML
    private void handleLeftButtonClick() {
        getViewModel().selectTemplate(PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY);
    }

    /**
     * Handles the right button click.
     */
    @FXML
    private void handleRightButtonClick() {
        getViewModel().selectTemplate(PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY);
    }

    /**
     * Handles the bottom button click.
     */
    @FXML
    private void handleBottomButtonClick() {
        getViewModel().selectTemplate(PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY);
    }

    /**
     * Handles the summary button click.
     */
    @FXML
    private void handleSummaryButtonClick() {
        getViewModel().goToSummary();
    }

    /**
     * Handles the navigate back button click.
     */
    @FXML
    private void handleNavigateBackClick() {
        getViewModel().goBack();
    }
}
