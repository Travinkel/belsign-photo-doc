package com.belman.presentation.usecases.worker.photocube;

import com.belman.domain.photo.PhotoTemplate;
import com.belman.presentation.base.BaseViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for a photo template status item in the Progressive Capture Dashboard.
 * This class represents a single template with its status (captured, validated, etc.)
 * and is used to populate the template list in the dashboard.
 */
public class PhotoTemplateStatusViewModel extends BaseViewModel<PhotoTemplateStatusViewModel> {

    // The photo template this status represents
    private final ObjectProperty<PhotoTemplate> template = new SimpleObjectProperty<>();

    // Whether a photo has been captured for this template
    private final BooleanProperty captured = new SimpleBooleanProperty(false);

    // Whether the captured photo has passed validation
    private final BooleanProperty validated = new SimpleBooleanProperty(false);

    // Optional validation message (e.g., why a photo failed validation)
    private final StringProperty validationMessage = new SimpleStringProperty("");

    // Whether this template is currently selected
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    // Whether this template is required (vs. optional)
    private final BooleanProperty required = new SimpleBooleanProperty(true);

    /**
     * Creates a new PhotoTemplateStatusViewModel with the specified template.
     * 
     * @param template the photo template
     */
    public PhotoTemplateStatusViewModel(PhotoTemplate template) {
        super();
        this.template.set(template);
    }

    /**
     * Creates a new PhotoTemplateStatusViewModel with the specified template and status.
     * 
     * @param template the photo template
     * @param captured whether a photo has been captured for this template
     * @param validated whether the captured photo has passed validation
     * @param required whether this template is required
     */
    public PhotoTemplateStatusViewModel(PhotoTemplate template, boolean captured, boolean validated, boolean required) {
        super();
        this.template.set(template);
        this.captured.set(captured);
        this.validated.set(validated);
        this.required.set(required);
    }

    /**
     * Default constructor required by BaseViewModel.
     */
    public PhotoTemplateStatusViewModel() {
        super();
    }

    // Getters and setters for properties

    public PhotoTemplate getTemplate() {
        return template.get();
    }

    public ObjectProperty<PhotoTemplate> templateProperty() {
        return template;
    }

    public void setTemplate(PhotoTemplate template) {
        this.template.set(template);
    }

    public boolean isCaptured() {
        return captured.get();
    }

    public BooleanProperty capturedProperty() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured.set(captured);
    }

    public boolean isValidated() {
        return validated.get();
    }

    public BooleanProperty validatedProperty() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated.set(validated);
    }

    public String getValidationMessage() {
        return validationMessage.get();
    }

    public StringProperty validationMessageProperty() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage.set(validationMessage);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public boolean isRequired() {
        return required.get();
    }

    public BooleanProperty requiredProperty() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required.set(required);
    }

    /**
     * Returns the status of this template as a string.
     * 
     * @return "Completed" if captured and validated, "Captured" if captured but not validated,
     *         "Required" if required but not captured, "Optional" if not required and not captured
     */
    public String getStatusText() {
        if (captured.get()) {
            return validated.get() ? "Completed" : "Captured";
        } else {
            return required.get() ? "Required" : "Optional";
        }
    }

    /**
     * Returns the CSS style class for this template's status.
     * 
     * @return "status-completed" if captured and validated, "status-captured" if captured but not validated,
     *         "status-required" if required but not captured, "status-optional" if not required and not captured
     */
    public String getStatusStyleClass() {
        if (captured.get()) {
            return validated.get() ? "status-completed" : "status-captured";
        } else {
            return required.get() ? "status-required" : "status-optional";
        }
    }
}
