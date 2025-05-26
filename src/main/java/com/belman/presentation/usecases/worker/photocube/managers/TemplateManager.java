package com.belman.presentation.usecases.worker.photocube.managers;

import com.belman.common.di.Inject;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.application.usecase.photo.PhotoTemplateService;
import com.belman.presentation.providers.PhotoTemplateLabelProvider;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.photocube.PhotoTemplateStatusViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages template loading, status tracking, and filtering for the PhotoCubeViewModel.
 * This class is responsible for template-related operations.
 */
public class TemplateManager {

    @Inject
    private PhotoTemplateService photoTemplateService;

    // Properties for UI binding
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final ObjectProperty<PhotoTemplate> selectedTemplate = new SimpleObjectProperty<>();
    private final MapProperty<PhotoTemplate, Boolean> templateCompletionStatus = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private final ListProperty<PhotoDocument> takenPhotos = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty photosCompleted = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPhotosRequired = new SimpleIntegerProperty(0);
    private final ListProperty<PhotoTemplateStatusViewModel> templateStatusList = 
        new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty showRemainingOnly = new SimpleBooleanProperty(false);
    private final ListProperty<PhotoTemplateStatusViewModel> filteredTemplateStatusList = 
        new SimpleListProperty<>(FXCollections.observableArrayList());

    // Templates for the dashboard - will be loaded from PhotoTemplateService
    private List<PhotoTemplate> requiredTemplates = new ArrayList<>();

    /**
     * Loads templates for the specified order.
     * 
     * @param orderId the ID of the order
     * @return true if templates were loaded successfully, false otherwise
     */
    public boolean loadTemplates(OrderId orderId) {
        // Ensure UI updates happen on the JavaFX application thread
        javafx.application.Platform.runLater(() -> {
            statusMessage.set("Loading templates...");
        });

        try {
            // Get available templates from the PhotoTemplateService
            System.out.println("[DEBUG_LOG] Getting available templates for order ID: " + orderId.id());
            requiredTemplates = photoTemplateService.getAvailableTemplates(orderId);
            System.out.println("[DEBUG_LOG] Found " + requiredTemplates.size() + " templates for order ID: " + orderId.id());

            // Log template details for debugging
            if (!requiredTemplates.isEmpty()) {
                System.out.println("[DEBUG_LOG] Template details for order ID: " + orderId.id());
                for (int i = 0; i < requiredTemplates.size(); i++) {
                    PhotoTemplate template = requiredTemplates.get(i);
                    System.out.println("[DEBUG_LOG]   Template " + (i+1) + ": " + 
                        "Name=" + template.name() + 
                        ", Description=" + template.description() + 
                        ", RequiredFields=" + template.requiredFields());
                }
            }

            // If no templates are available, show a clear non-technical message
            if (requiredTemplates.isEmpty()) {
                System.out.println("[DEBUG_LOG] No templates found for order ID: " + orderId.id() + " - showing error message");
                javafx.application.Platform.runLater(() -> {
                    errorMessage.set("No photo templates available. Please contact your supervisor to set up templates.");
                    statusMessage.set("Waiting for templates to be assigned. Please refresh or contact your supervisor.");
                });
                return false;
            }

            // Set the total required photos count
            javafx.application.Platform.runLater(() -> {
                totalPhotosRequired.set(requiredTemplates.size());
            });

            updateTemplateStatus(Collections.emptyList());

            if (!requiredTemplates.isEmpty()) {
                javafx.application.Platform.runLater(() ->
                        javafx.application.Platform.runLater(() ->
                                selectTemplate(requiredTemplates.get(0))
                        )
                );
            }



            return true;
        } catch (Exception e) {
            final String errorMsg = e.getMessage(); // Create a final copy for the lambda
            javafx.application.Platform.runLater(() -> {
                errorMessage.set("Error loading templates: " + errorMsg + ". Please try again or contact support.");
            });
            return false;
        }
    }

    /**
     * Updates the template status based on the taken photos.
     * 
     * @param photos the list of photos taken for the order
     */
    public void updateTemplateStatus(List<PhotoDocument> photos) {
        try {
            // Prepare data outside of Platform.runLater to minimize work on UI thread
            final int photosCount = photos.size();

            // Initialize the template completion status map
            final Map<PhotoTemplate, Boolean> completionStatus = new HashMap<>();

            // Create template status view models for each template
            final ObservableList<PhotoTemplateStatusViewModel> statusList = FXCollections.observableArrayList();

            for (PhotoTemplate template : requiredTemplates) {
                boolean isCompleted = photos.stream()
                    .anyMatch(photo -> photo.getTemplate().equals(template));
                completionStatus.put(template, isCompleted);

                // Create a status view model for this template
                PhotoTemplateStatusViewModel statusViewModel = new PhotoTemplateStatusViewModel(
                    template, isCompleted, isCompleted, true);

                statusList.add(statusViewModel);
            }

            // Get selected template from context
            final PhotoTemplate selectedTemplateFromContext = WorkerFlowContext.getSelectedTemplate();
            final PhotoTemplate firstTemplate = !requiredTemplates.isEmpty() ? requiredTemplates.get(0) : null;

            // Update UI on JavaFX application thread
            javafx.application.Platform.runLater(() -> {
                // Update the photos completed count
                photosCompleted.set(photosCount);
                takenPhotos.setAll(photos);

                // Update the observable lists
                templateCompletionStatus.putAll(completionStatus);
                templateStatusList.setAll(statusList);

                // Update the filtered template list to ensure UI is refreshed immediately
                updateFilteredTemplateList();

                // Check if there's a selected template in the WorkerFlowContext
                if (selectedTemplateFromContext != null) {
                    selectedTemplate.set(selectedTemplateFromContext);

                    // Update the selected state in the status view models only if the list is not empty
                    if (!templateStatusList.isEmpty()) {
                        for (PhotoTemplateStatusViewModel statusViewModel : templateStatusList) {
                            statusViewModel.setSelected(
                                statusViewModel.getTemplate().equals(selectedTemplateFromContext));
                        }
                    }
                } else if (firstTemplate != null) {
                    // Auto-select the first template if none is selected
                    selectedTemplate.set(firstTemplate);
                    WorkerFlowContext.setSelectedTemplate(firstTemplate);

                    // Update the selected state in the status view models only if the list is not empty
                    if (!templateStatusList.isEmpty()) {
                        for (PhotoTemplateStatusViewModel statusViewModel : templateStatusList) {
                            statusViewModel.setSelected(
                                statusViewModel.getTemplate().equals(firstTemplate));
                        }
                    }
                }

                statusMessage.set("Ready to take photos. " + photosCompleted.get() + " of " + 
                                totalPhotosRequired.get() + " photos taken.");
            });
        } catch (Exception e) {
            final String errorMsg = e.getMessage(); // Create a final copy for the lambda
            javafx.application.Platform.runLater(() -> {
                errorMessage.set("Error updating template status: " + errorMsg + ". Please try again or contact support.");
            });
        }
    }

    /**
     * Selects a photo template for capture.
     *
     * @param template the template to select
     */
    public void selectTemplate(PhotoTemplate template) {
        // Ensure UI updates happen on the JavaFX application thread
        javafx.application.Platform.runLater(() -> {
            statusMessage.set("Selecting template...");
        });

        try {
            // Store the selected template in the worker flow context (this is not a UI operation)
            WorkerFlowContext.setSelectedTemplate(template);

            // Get the display label outside of Platform.runLater to minimize work on UI thread
            final String displayLabel = PhotoTemplateLabelProvider.getDisplayLabel(template);

            // Update UI on JavaFX application thread
            javafx.application.Platform.runLater(() -> {
                selectedTemplate.set(template);

                // Update the selected state in the status view models only if the list is not empty
                if (!templateStatusList.isEmpty()) {
                    for (PhotoTemplateStatusViewModel statusViewModel : templateStatusList) {
                        statusViewModel.setSelected(
                            statusViewModel.getTemplate().equals(template));
                    }
                }

                // Update the filtered template list to ensure UI is refreshed immediately
                updateFilteredTemplateList();

                statusMessage.set("Template selected: " + displayLabel + ". Ready to capture.");
            });
        } catch (Exception e) {
            final String errorMsg = e.getMessage(); // Create a final copy for the lambda
            javafx.application.Platform.runLater(() -> {
                errorMessage.set("Error selecting template: " + errorMsg + ". Please try again.");
            });
        }
    }

    /**
     * Updates the template status after a photo is captured.
     * 
     * @param savedPhoto the saved photo document
     */
    public void updateAfterPhotoCapture(PhotoDocument savedPhoto) {
        try {
            // Get template outside of Platform.runLater to minimize work on UI thread
            final PhotoTemplate template = savedPhoto.getTemplate();

            // Update UI on JavaFX application thread
            javafx.application.Platform.runLater(() -> {
                // Add the photo to the taken photos list
                takenPhotos.add(savedPhoto);

                // Update the photos completed count
                photosCompleted.set(photosCompleted.get() + 1);

                // Update the template completion status
                templateCompletionStatus.put(template, true);

                // Update the template status view model only if the list is not empty
                if (!templateStatusList.isEmpty()) {
                    for (PhotoTemplateStatusViewModel statusViewModel : templateStatusList) {
                        if (statusViewModel.getTemplate().equals(template)) {
                            statusViewModel.setCaptured(true);
                            statusViewModel.setValidated(true);
                            break;
                        }
                    }
                }

                // Update the filtered template list to ensure UI is refreshed immediately
                updateFilteredTemplateList();

                // Update the status message
                statusMessage.set("Photo captured successfully. " + photosCompleted.get() + " of " + 
                                totalPhotosRequired.get() + " photos taken.");
            });

            // Auto-select the next template if available
            selectNextTemplate();
        } catch (Exception e) {
            final String errorMsg = e.getMessage(); // Create a final copy for the lambda
            javafx.application.Platform.runLater(() -> {
                errorMessage.set("Error updating template status after photo capture: " + errorMsg + ". Please try again.");
            });
        }
    }

    /**
     * Selects the next template that doesn't have a photo yet.
     */
    private void selectNextTemplate() {
        // Find the next template that doesn't have a photo
        for (PhotoTemplate template : requiredTemplates) {
            Boolean isCompleted = templateCompletionStatus.get(template);
            if (isCompleted == null || !isCompleted) {
                // Select this template
                selectTemplate(template);
                return;
            }
        }

        // All templates have photos, check if we can go to summary
        if (areAllPhotosTaken()) {
            javafx.application.Platform.runLater(() -> {
                statusMessage.set("All required photos have been taken. You can now proceed to the summary.");
            });
        }
    }

    /**
     * Checks if all required photos have been taken.
     *
     * @param orderId the ID of the order
     * @return true if all required photos have been taken, false otherwise
     */
    public boolean areAllPhotosTaken(OrderId orderId) {
        try {
            // Use the PhotoTemplateService to check if all required photos have been taken
            return photoTemplateService.hasAllRequiredPhotos(orderId);
        } catch (Exception e) {
            // If there's an error, fall back to checking the template completion status map
            final String errorMsg = e.getMessage(); // Create a final copy for the lambda
            javafx.application.Platform.runLater(() -> {
                errorMessage.set("Error checking if all photos are taken: " + errorMsg);
            });
            return templateCompletionStatus.values().stream().allMatch(Boolean::booleanValue);
        }
    }

    /**
     * Gets the missing required templates for the specified order.
     * These are templates that don't have photos yet.
     * 
     * @param orderId the ID of the order
     * @return a list of templates that don't have photos yet
     */
    public List<PhotoTemplate> getMissingRequiredTemplates(OrderId orderId) {
        try {
            // Get all available templates for the order
            List<PhotoTemplate> allTemplates = photoTemplateService.getAvailableTemplates(orderId);

            // Filter out templates that already have photos
            List<PhotoTemplate> missingTemplates = new ArrayList<>();
            for (PhotoTemplate template : allTemplates) {
                Boolean isCompleted = templateCompletionStatus.get(template);
                if (isCompleted == null || !isCompleted) {
                    missingTemplates.add(template);
                }
            }

            return missingTemplates;
        } catch (Exception e) {
            // If there's an error, log it and return an empty list
            final String errorMsg = e.getMessage(); // Create a final copy for the lambda
            javafx.application.Platform.runLater(() -> {
                errorMessage.set("Error getting missing templates for order: " + errorMsg);
            });
            return Collections.emptyList();
        }
    }

    /**
     * Sets whether to show only remaining templates.
     * When true, only templates that haven't been captured yet will be shown.
     * When false, all templates will be shown.
     * 
     * @param showRemainingOnly whether to show only remaining templates
     */
    public void setShowRemainingOnly(boolean showRemainingOnly) {
        this.showRemainingOnly.set(showRemainingOnly);
        updateFilteredTemplateList();
    }

    /**
     * Updates the filtered template list based on the showRemainingOnly property.
     * When showRemainingOnly is true, only templates that haven't been captured yet will be included.
     * When showRemainingOnly is false, all templates will be included.
     */
    private void updateFilteredTemplateList() {
        try {
            // First check if the filtered template list is null to avoid NPE
            if (filteredTemplateStatusList == null) {
                System.err.println("Filtered template list is null, cannot update it");
                return;
            }

            // Check if the template status list is null or empty to avoid NPE
            if (templateStatusList == null || templateStatusList.isEmpty()) {
                // If the template status list is empty, ensure the filtered list is also empty
                System.out.println("Template status list is null or empty, clearing filtered list");
                try {
                    // Clear the list safely on the JavaFX application thread
                    javafx.application.Platform.runLater(() -> {
                        try {
                            safelyClearList(filteredTemplateStatusList);
                        } catch (Exception e) {
                            System.err.println("Error clearing filtered template list: " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error scheduling clear operation: " + e.getMessage());
                }
                return;
            }

            if (showRemainingOnly.get()) {
                try {
                    // Create a safe copy of the template status list to avoid concurrent modification
                    final List<PhotoTemplateStatusViewModel> safeList = new ArrayList<>();
                    try {
                        // Add all items from the template status list, with null check for each item
                        for (PhotoTemplateStatusViewModel item : templateStatusList) {
                            if (item != null) {
                                safeList.add(item);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error creating safe copy of template status list: " + e.getMessage());
                        // If we can't create a safe copy, use the original list
                        safeList.addAll(templateStatusList);
                    }

                    // Filter out completed templates with additional error handling
                    final List<PhotoTemplateStatusViewModel> remainingTemplates = new ArrayList<>();
                    for (PhotoTemplateStatusViewModel template : safeList) {
                        try {
                            if (template != null && !template.isCaptured()) {
                                remainingTemplates.add(template);
                            }
                        } catch (Exception e) {
                            System.err.println("Error checking if template is captured: " + e.getMessage());
                            // Include it in the remaining templates to be safe
                            if (template != null) {
                                remainingTemplates.add(template);
                            }
                        }
                    }

                    // Check if the filtered list would be empty
                    if (remainingTemplates.isEmpty() && !safeList.isEmpty()) {
                        // If all templates are captured and we're trying to show only remaining,
                        // show a message and revert to showing all templates
                        System.out.println("All templates captured, showing all templates instead of empty filtered list");

                        // Update UI on JavaFX application thread
                        javafx.application.Platform.runLater(() -> {
                            errorMessage.set("All templates have been captured. Showing all templates.");
                            showRemainingOnly.set(false);

                            try {
                                // Set all templates safely
                                safelySetAllItems(filteredTemplateStatusList, safeList);
                            } catch (Exception e) {
                                System.err.println("Error setting all templates in filtered list: " + e.getMessage());
                            }
                        });
                    } else {
                        // Set the filtered list to the remaining templates
                        System.out.println("Setting filtered list to " + remainingTemplates.size() + " remaining templates");

                        // Update UI on JavaFX application thread
                        javafx.application.Platform.runLater(() -> {
                            try {
                                // Set remaining templates safely
                                safelySetAllItems(filteredTemplateStatusList, remainingTemplates);
                            } catch (Exception e) {
                                System.err.println("Error setting remaining templates in filtered list: " + e.getMessage());
                                // Fallback to showing all templates
                                try {
                                    safelySetAllItems(filteredTemplateStatusList, safeList);
                                } catch (Exception ex) {
                                    System.err.println("Error in fallback for filtered list: " + ex.getMessage());
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    System.err.println("Error filtering templates: " + e.getMessage());
                    // Fallback to showing all templates
                    final List<PhotoTemplateStatusViewModel> finalTemplateStatusList = new ArrayList<>(templateStatusList);

                    // Update UI on JavaFX application thread
                    javafx.application.Platform.runLater(() -> {
                        try {
                            safelySetAllItems(filteredTemplateStatusList, finalTemplateStatusList);
                        } catch (Exception ex) {
                            System.err.println("Error in fallback for filtered list: " + ex.getMessage());
                        }
                    });
                }
            } else {
                // Show all templates
                System.out.println("Showing all " + templateStatusList.size() + " templates");
                final List<PhotoTemplateStatusViewModel> finalTemplateStatusList = new ArrayList<>(templateStatusList);

                // Update UI on JavaFX application thread
                javafx.application.Platform.runLater(() -> {
                    try {
                        // Set all templates safely
                        safelySetAllItems(filteredTemplateStatusList, finalTemplateStatusList);
                    } catch (Exception e) {
                        System.err.println("Error setting all templates in filtered list: " + e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error updating filtered template list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Safely clears a list property.
     * This method adds additional error handling to prevent exceptions when clearing a list.
     * 
     * @param listProperty the list property to clear
     */
    private <T> void safelyClearList(ListProperty<T> listProperty) {
        if (listProperty == null) {
            return;
        }

        try {
            // Create a new empty observable list
            ObservableList<T> emptyList = FXCollections.observableArrayList();

            // Set the list property to the empty list
            listProperty.setAll(emptyList);
        } catch (Exception e) {
            System.err.println("Error clearing list: " + e.getMessage());

            try {
                // Fallback to using clear() method
                listProperty.clear();
            } catch (Exception ex) {
                System.err.println("Error using clear() method: " + ex.getMessage());
            }
        }
    }

    /**
     * Safely sets all items in a list property.
     * This method adds additional error handling to prevent exceptions when setting items in a list.
     * 
     * @param listProperty the list property to update
     * @param items the items to set
     */
    private <T> void safelySetAllItems(ListProperty<T> listProperty, List<T> items) {
        if (listProperty == null || items == null) {
            return;
        }

        try {
            // Create a new observable list with the items
            ObservableList<T> observableList = FXCollections.observableArrayList();

            // Add each item with null check
            for (T item : items) {
                if (item != null) {
                    observableList.add(item);
                }
            }

            // Set the list property to the new list
            listProperty.setAll(observableList);
        } catch (Exception e) {
            System.err.println("Error setting items in list: " + e.getMessage());

            try {
                // Fallback to clearing and adding each item
                listProperty.clear();
                for (T item : items) {
                    if (item != null) {
                        listProperty.add(item);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error using clear() and add() methods: " + ex.getMessage());
            }
        }
    }

    /**
     * Checks if the given template is the last remaining template in the filtered list.
     * This is used to prevent IndexOutOfBoundsException when clearing selection after
     * selecting the last remaining template when showRemainingOnly is true.
     * 
     * @param template the template to check
     * @return true if this is the last remaining template, false otherwise
     */
    public boolean isLastRemainingTemplate(PhotoTemplate template) {
        // If template is null, it can't be the last remaining template
        if (template == null) {
            return false;
        }

        // If not showing remaining only, this check is not relevant
        if (!showRemainingOnly.get()) {
            return false;
        }

        // If template status list is null or empty, return false
        if (templateStatusList == null || templateStatusList.isEmpty()) {
            return false;
        }

        try {
            // Count how many templates are not captured yet
            long remainingCount = templateStatusList.stream()
                .filter(status -> !status.isCaptured())
                .count();

            // If there's only one remaining and it's this template, it's the last one
            if (remainingCount == 1) {
                return templateStatusList.stream()
                    .filter(status -> !status.isCaptured())
                    .anyMatch(status -> status.getTemplate() != null && status.getTemplate().equals(template));
            }

            return false;
        } catch (Exception e) {
            // Log the error but don't crash
            System.err.println("Error in isLastRemainingTemplate: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if all required photos have been taken.
     * This is a convenience method that doesn't require an order ID.
     *
     * @return true if all required photos have been taken, false otherwise
     */
    public boolean areAllPhotosTaken() {
        return templateCompletionStatus.values().stream().allMatch(Boolean::booleanValue);
    }

    // Getters for properties

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public ObjectProperty<PhotoTemplate> selectedTemplateProperty() {
        return selectedTemplate;
    }

    public MapProperty<PhotoTemplate, Boolean> templateCompletionStatusProperty() {
        return templateCompletionStatus;
    }

    public ListProperty<PhotoDocument> takenPhotosProperty() {
        return takenPhotos;
    }

    public IntegerProperty photosCompletedProperty() {
        return photosCompleted;
    }

    public IntegerProperty totalPhotosRequiredProperty() {
        return totalPhotosRequired;
    }

    public ListProperty<PhotoTemplateStatusViewModel> templateStatusListProperty() {
        // Create a default empty list to return if needed
        ObservableList<PhotoTemplateStatusViewModel> emptyList = FXCollections.observableArrayList();
        SimpleListProperty<PhotoTemplateStatusViewModel> emptyProperty = new SimpleListProperty<>(emptyList);

        try {
            // First check if both lists are null or empty
            if ((templateStatusList == null || templateStatusList.isEmpty()) && 
                (filteredTemplateStatusList == null || filteredTemplateStatusList.isEmpty())) {
                // If both lists are empty, return an empty list
                System.out.println("Both template lists are empty, returning empty list");
                return emptyProperty;
            }

            // Check if the appropriate list is empty
            if (showRemainingOnly.get()) {
                // If showing remaining only and filtered list is empty but main list is not,
                // automatically switch to showing all templates
                if ((filteredTemplateStatusList == null || filteredTemplateStatusList.isEmpty()) && 
                    templateStatusList != null && !templateStatusList.isEmpty()) {
                    // Only switch if we have captured all templates
                    boolean allCaptured = true;
                    try {
                        allCaptured = templateStatusList.stream()
                            .allMatch(PhotoTemplateStatusViewModel::isCaptured);
                    } catch (Exception e) {
                        System.err.println("Error checking if all templates are captured: " + e.getMessage());
                    }

                    if (allCaptured) {
                        // Log this action
                        System.out.println("All templates captured, automatically switching to show all templates");
                        // Set a user-friendly message
                        errorMessage.set("All templates have been captured. Showing all templates.");
                        // Switch to showing all templates
                        showRemainingOnly.set(false);
                        // Return the main list if it's not null, otherwise return an empty list
                        return templateStatusList != null ? templateStatusList : emptyProperty;
                    }
                }

                // Return filtered list if it's not null and not empty, otherwise return an empty list
                if (filteredTemplateStatusList != null && !filteredTemplateStatusList.isEmpty()) {
                    return filteredTemplateStatusList;
                } else {
                    System.out.println("Filtered template list is null or empty, returning empty list");
                    return emptyProperty;
                }
            } else {
                // Return main list if it's not null and not empty, otherwise return an empty list
                if (templateStatusList != null && !templateStatusList.isEmpty()) {
                    return templateStatusList;
                } else {
                    System.out.println("Main template list is null or empty, returning empty list");
                    return emptyProperty;
                }
            }
        } catch (Exception e) {
            // Log the error and return an empty list
            System.err.println("Error in templateStatusListProperty: " + e.getMessage());
            return emptyProperty;
        }
    }

    public BooleanProperty showRemainingOnlyProperty() {
        return showRemainingOnly;
    }

    public List<PhotoTemplate> getRequiredTemplates() {
        return requiredTemplates;
    }

    public ListProperty<PhotoTemplateStatusViewModel> filteredTemplateStatusListProperty() {
        return filteredTemplateStatusList;
    }

    public PhotoTemplate getSelectedTemplate() {
        return selectedTemplate.get();
    }
}
