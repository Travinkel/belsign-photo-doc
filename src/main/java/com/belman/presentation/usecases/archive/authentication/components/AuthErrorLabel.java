package com.belman.presentation.usecases.archive.authentication.components;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * A styled label for displaying authentication errors.
 * This component provides a consistent look and feel for error messages in authentication forms.
 */
public class AuthErrorLabel extends Label {
    
    /**
     * Creates a new AuthErrorLabel with the specified text.
     *
     * @param text the error message text
     */
    public AuthErrorLabel(String text) {
        super(text);
        initialize();
    }
    
    /**
     * Creates a new AuthErrorLabel with no text.
     */
    public AuthErrorLabel() {
        super();
        initialize();
    }
    
    /**
     * Initializes the label with the appropriate styles and properties.
     */
    private void initialize() {
        // Add the auth-error-label style class
        getStyleClass().add("auth-error-label");
        
        // Set default properties
        setTextFill(Color.RED);
        setWrapText(true);
        setTextAlignment(TextAlignment.CENTER);
        setMaxWidth(Double.MAX_VALUE);
        
        // Hide the label initially
        setVisible(false);
        setManaged(false);
    }
    
    /**
     * Shows the error label with the specified message.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        setText(message);
        setVisible(true);
        setManaged(true);
    }
    
    /**
     * Hides the error label.
     */
    public void hideError() {
        setVisible(false);
        setManaged(false);
    }
    
    /**
     * Binds this label to an error message property.
     * The label will be shown when the property has a non-empty value and hidden otherwise.
     *
     * @param errorMessageProperty the error message property to bind to
     */
    public void bindToErrorMessage(javafx.beans.property.StringProperty errorMessageProperty) {
        // Bind the text property
        textProperty().bind(errorMessageProperty);
        
        // Add a listener to show/hide the label based on the error message
        errorMessageProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                setVisible(true);
                setManaged(true);
            } else {
                setVisible(false);
                setManaged(false);
            }
        });
    }
}