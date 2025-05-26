package com.belman.presentation.usecases.worker.photocube;

import com.belman.domain.photo.PhotoTemplate;
import com.belman.presentation.base.BaseController;
import com.belman.presentation.flow.commands.CommandManager;
import com.belman.presentation.flow.commands.CapturePhotoCommand;
import com.belman.presentation.flow.commands.GoToSummaryCommand;
import com.belman.presentation.flow.commands.NavigateBackCommand;
import com.belman.presentation.flow.commands.RefreshTemplatesCommand;
import com.belman.presentation.flow.commands.StartCameraPreviewCommand;
import com.belman.presentation.flow.commands.ToggleShowRemainingCommand;
import com.belman.presentation.providers.PhotoTemplateLabelProvider;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;

/**
 * Controller for the PhotoCubeView.
 * Handles UI interactions for the Progressive Capture Dashboard.
 */
public class PhotoCubeViewController extends BaseController<PhotoCubeViewModel> {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private HBox errorContainer;

    @FXML
    private ImageView photoPreviewImageView;

    @FXML
    private VBox noPhotoPlaceholder;

    @FXML
    private VBox cameraActiveIndicator;

    @FXML
    private Button startCameraButton;

    @FXML
    private Button captureButton;

    @FXML
    private Label selectedTemplateLabel;

    @FXML
    private Label templateDescriptionLabel;

    @FXML
    private Label topProgressLabel;


    @FXML
    private Button summaryButton;

    @FXML
    private StackPane loadingPane;

    @FXML
    private ListView<PhotoTemplateStatusViewModel> templateListView;

    @FXML
    private CheckBox showRemainingToggle;

    @FXML
    private HBox centerContainer;

    @FXML
    private HBox steppedProgressContainer;

    @FXML
    private VBox photoOverlay;

    @FXML
    private Label overlayTemplateNameLabel;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        orderNumberLabel.textProperty().bind(getViewModel().orderNumberProperty());
        statusLabel.textProperty().bind(getViewModel().statusMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loadingPane.visibleProperty().bind(getViewModel().loadingProperty());

        // Add listener for window width changes after scene is set
        centerContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs2, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.widthProperty().addListener((obs3, oldWidth, newWidth) -> {
                            if (newWidth.doubleValue() < 800) {
                                // Apply narrow layout for screens < 800px wide
                                centerContainer.getParent().getStyleClass().add("narrow-layout");
                            } else {
                                // Remove narrow layout for screens >= 800px wide
                                centerContainer.getParent().getStyleClass().remove("narrow-layout");
                            }
                        });
                    }
                });
            }
        });

        // Show error container when there's an error
        errorContainer.visibleProperty().bind(errorLabel.textProperty().isNotEmpty());

        // Bind photo preview
        photoPreviewImageView.imageProperty().bind(getViewModel().currentPhotoPreviewProperty());

        // Show/hide placeholders based on camera state
        noPhotoPlaceholder.visibleProperty().bind(
            getViewModel().currentPhotoPreviewProperty().isNull()
                .and(getViewModel().cameraActiveProperty().not())
        );

        cameraActiveIndicator.visibleProperty().bind(getViewModel().cameraActiveProperty());

        // Show photo overlay when a photo is captured
        photoOverlay.visibleProperty().bind(
            getViewModel().currentPhotoPreviewProperty().isNotNull()
                .and(getViewModel().cameraActiveProperty().not())
        );

        // Add visible style class when overlay is visible
        photoOverlay.visibleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                photoOverlay.getStyleClass().add("visible");
            } else {
                photoOverlay.getStyleClass().remove("visible");
            }
        });

        // Bind overlay template name with user-friendly label
        getViewModel().selectedTemplateProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                overlayTemplateNameLabel.setText(PhotoTemplateLabelProvider.getDisplayLabel(newVal));
            } else {
                overlayTemplateNameLabel.setText("");
            }
        });

        // Enable/disable buttons based on state
        captureButton.disableProperty().bind(getViewModel().cameraActiveProperty().not());
        startCameraButton.disableProperty().bind(
            getViewModel().loadingProperty()
                .or(getViewModel().selectedTemplateProperty().isNull())
                .or(getViewModel().cameraActiveProperty())
        );

        // Progress is now only shown in the top bar

        // Create a custom binding for the percentage calculation
        topProgressLabel.textProperty().bind(
            javafx.beans.binding.Bindings.createStringBinding(
                () -> {
                    int completed = getViewModel().photosCompletedProperty().get();
                    int total = getViewModel().totalPhotosRequiredProperty().get();
                    int percentage = total > 0 ? (completed * 100) / total : 0;
                    return completed + "/" + total + " photos (" + percentage + "%)";
                },
                getViewModel().photosCompletedProperty(),
                getViewModel().totalPhotosRequiredProperty()
            )
        );

        // Initialize stepped progress bar when photos completed or total photos change
        getViewModel().photosCompletedProperty().addListener((obs, oldVal, newVal) -> {
            initializeSteppedProgressBar(
                getViewModel().totalPhotosRequiredProperty().get(),
                newVal.intValue()
            );
        });

        getViewModel().totalPhotosRequiredProperty().addListener((obs, oldVal, newVal) -> {
            initializeSteppedProgressBar(
                newVal.intValue(),
                getViewModel().photosCompletedProperty().get()
            );
        });

        // Initialize stepped progress bar with current values
        initializeSteppedProgressBar(
            getViewModel().totalPhotosRequiredProperty().get(),
            getViewModel().photosCompletedProperty().get()
        );

        // Disable the summary button when loading or when not all photos are taken
        summaryButton.disableProperty().bind(
            getViewModel().loadingProperty().or(
                getViewModel().photosCompletedProperty().lessThan(
                    getViewModel().totalPhotosRequiredProperty()
                )
            )
        );

        // Bind selected template info with user-friendly label
        getViewModel().selectedTemplateProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTemplateLabel.setText(PhotoTemplateLabelProvider.getDisplayLabel(newVal));
                templateDescriptionLabel.setText(newVal.description());

                // Add tooltip with detailed instructions
                Tooltip tooltip = new Tooltip(PhotoTemplateLabelProvider.getTooltip(newVal));
                Tooltip.install(selectedTemplateLabel, tooltip);
            } else {
                selectedTemplateLabel.setText("None selected");
                templateDescriptionLabel.setText("");
                Tooltip.uninstall(selectedTemplateLabel, null);
            }
        });

        // Set up the template list view
        setupTemplateListView();
    }

    /**
     * Sets up the template list view with the template status view models.
     */
    private void setupTemplateListView() {
        // Bind the list view to the template status list in the view model
        // First clear the selection to prevent IndexOutOfBoundsException
        safelyClearSelection();
        templateListView.itemsProperty().bind(getViewModel().filteredTemplateStatusListProperty());

        // Set cell factory to display template status
        templateListView.setCellFactory(lv -> new ListCell<PhotoTemplateStatusViewModel>() {
            @Override
            protected void updateItem(PhotoTemplateStatusViewModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("status-completed", "status-captured", 
                                             "status-required", "status-optional");
                } else {
                    // Create a layout for the cell
                    HBox cellContent = new HBox(10);
                    cellContent.getStyleClass().add("template-cell");

                    // Required/Optional indicator
                    Label requiredIndicator = new Label(item.isRequired() ? "★" : "☆");
                    requiredIndicator.getStyleClass().add(item.isRequired() ? "required-indicator" : "optional-indicator");
                    requiredIndicator.setTooltip(new Tooltip(item.isRequired() ? 
                        "Required: This photo must be taken to complete the documentation" : 
                        "Optional: This photo is recommended but not required"));

                    // Template name with user-friendly label
                    Label nameLabel = new Label(PhotoTemplateLabelProvider.getDisplayLabel(item.getTemplate()));
                    nameLabel.getStyleClass().add("template-name");

                    // Add tooltip with detailed instructions
                    Tooltip tooltip = new Tooltip(PhotoTemplateLabelProvider.getTooltip(item.getTemplate()));
                    tooltip.getStyleClass().add("template-tooltip");
                    Tooltip.install(nameLabel, tooltip);

                    // Status indicator
                    Label statusLabel = new Label(item.getStatusText());
                    statusLabel.getStyleClass().add(item.getStatusStyleClass());

                    // Add to cell
                    cellContent.getChildren().addAll(requiredIndicator, nameLabel, statusLabel);

                    // Set the cell content
                    setText(null);
                    setGraphic(cellContent);

                    // Add style class based on status
                    getStyleClass().removeAll("status-completed", "status-captured", 
                                             "status-required", "status-optional");
                    getStyleClass().add(item.getStatusStyleClass());

                    // Apply visual effects for completed templates
                    if (item.isCaptured()) {
                        // Add checkmark to completed templates
                        Label checkmark = new Label("✓");
                        checkmark.getStyleClass().add("template-checkmark");
                        cellContent.getChildren().add(checkmark);

                        // Apply fade effect to completed templates
                        cellContent.setOpacity(0.8);
                    } else {
                        cellContent.setOpacity(1.0);
                    }

                    // Add selected style if this template is selected
                    if (item.isSelected()) {
                        getStyleClass().add("selected-template");
                    } else {
                        getStyleClass().remove("selected-template");
                    }
                }
            }
        });

        // Handle selection
        try {
            // First check if the selection model is not null
            if (templateListView.getSelectionModel() != null) {
                templateListView.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, newVal) -> {
                        if (newVal != null) {
                            try {
                                // First check if this is the last remaining template
                                boolean isLastRemaining = getViewModel().isLastRemainingTemplate(newVal.getTemplate());

                                // Select the template
                                getViewModel().selectTemplate(newVal.getTemplate());

                                // If this was the last remaining template and showRemainingOnly is true,
                                // the list might be empty now, so don't try to clear the selection
                                if (!isLastRemaining) {
                                    // Use the safe method to clear selection
                                    safelyClearSelection();
                                }
                            } catch (Exception e) {
                                System.err.println("Error selecting template: " + e.getMessage());
                            }
                        }
                    }
                );
            } else {
                System.err.println("Warning: templateListView.getSelectionModel() is null");
            }
        } catch (Exception e) {
            System.err.println("Error setting up template selection listener: " + e.getMessage());
        }
    }

    /**
     * Handles the start camera button click.
     */
    @FXML
    private void handleStartCameraClick() {
        // Create and execute the command
        StartCameraPreviewCommand command = new StartCameraPreviewCommand(getViewModel());
        CommandManager.getInstance().execute(command);
    }

    /**
     * Handles the capture photo button click.
     */
    @FXML
    private void handleCapturePhotoClick() {
        // Note: This would typically use CapturePhotoCommand, but that requires a File, OrderId, and PhotoTemplate
        // For simplicity, we'll continue to use the ViewModel method directly
        getViewModel().capturePhoto();
    }

    /**
     * Handles the summary button click.
     */
    @FXML
    private void handleSummaryButtonClick() {
        // Create and execute the command
        GoToSummaryCommand command = new GoToSummaryCommand(getViewModel());
        CommandManager.getInstance().execute(command);
    }

    /**
     * Handles the navigate back button click.
     */
    @FXML
    private void handleNavigateBackClick() {
        // Create and execute the command
        NavigateBackCommand command = new NavigateBackCommand(getViewModel());
        CommandManager.getInstance().execute(command);
    }

    /**
     * Handles the refresh templates button click.
     * This method is called when the user clicks the refresh button when no templates are available.
     */
    @FXML
    private void handleRefreshTemplates() {
        // Create and execute the command
        RefreshTemplatesCommand command = new RefreshTemplatesCommand(getViewModel());
        CommandManager.getInstance().execute(command);
    }

    /**
     * Handles the "Show remaining only" toggle.
     * This method is called when the user toggles the "Show remaining only" checkbox.
     */
    @FXML
    private void handleShowRemainingToggle() {
        // Get the checkbox state
        boolean showRemainingOnly = showRemainingToggle.isSelected();

        // Clear the selection before updating the filtered list
        // This prevents IndexOutOfBoundsException when the filtered list becomes empty
        safelyClearSelection();

        // Create and execute the command
        try {
            ToggleShowRemainingCommand command = new ToggleShowRemainingCommand(getViewModel(), showRemainingOnly);
            CommandManager.getInstance().execute(command)
                .exceptionally(ex -> {
                    System.err.println("Error toggling show remaining only: " + ex.getMessage());
                    // Try to recover by resetting the checkbox to its previous state
                    try {
                        showRemainingToggle.setSelected(!showRemainingOnly);
                    } catch (Exception e) {
                        // Ignore if we can't reset the checkbox
                    }
                    return null;
                });
        } catch (Exception e) {
            System.err.println("Error creating or executing command: " + e.getMessage());
            // Try to recover by resetting the checkbox to its previous state
            try {
                showRemainingToggle.setSelected(!showRemainingOnly);
            } catch (Exception ex) {
                // Ignore if we can't reset the checkbox
            }
        }
    }

    /**
     * Safely clears the selection in the template list view.
     * This method adds additional checks to prevent IndexOutOfBoundsException.
     */
    private void safelyClearSelection() {
        Platform.runLater(() -> {
            try {
                if (templateListView.getSelectionModel() != null) {
                    templateListView.getSelectionModel().clearSelection();
                }
            } catch (Exception e) {
                System.err.println("Failed to clear ListView selection: " + e.getMessage());
            }
        });
    }

    /**
     * Initializes the stepped progress bar.
     * This method creates step indicators based on the number of templates.
     * 
     * @param totalSteps the total number of steps
     * @param completedSteps the number of completed steps
     */
    private void initializeSteppedProgressBar(int totalSteps, int completedSteps) {
        // Clear existing steps
        steppedProgressContainer.getChildren().clear();

        // Calculate percentage for tooltip
        int percentage = totalSteps > 0 ? (completedSteps * 100) / totalSteps : 0;

        // Add percentage indicator at the beginning
        Label percentLabel = new Label(percentage + "%");
        percentLabel.getStyleClass().add("progress-percent");
        percentLabel.setTooltip(new Tooltip(completedSteps + " of " + totalSteps + " photos completed"));
        steppedProgressContainer.getChildren().add(percentLabel);

        // Create step indicators
        for (int i = 0; i < totalSteps; i++) {
            StackPane step = new StackPane();
            step.getStyleClass().add("progress-step");

            // Set a fixed size for consistent appearance
            step.setPrefWidth(12);
            step.setPrefHeight(12);

            // Add tooltip showing step number
            Tooltip stepTooltip = new Tooltip("Step " + (i + 1) + " of " + totalSteps);
            Tooltip.install(step, stepTooltip);

            // Mark completed steps
            if (i < completedSteps) {
                step.getStyleClass().add("completed");
            }

            // Mark current step
            if (i == completedSteps && i < totalSteps) {
                step.getStyleClass().add("current");
            }

            // Add to container
            steppedProgressContainer.getChildren().add(step);
        }
    }
}
