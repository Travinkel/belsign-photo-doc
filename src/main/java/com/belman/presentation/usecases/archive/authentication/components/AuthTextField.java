package com.belman.presentation.usecases.archive.authentication.components;

import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

/**
 * A styled text field for authentication inputs (username, password, etc.).
 * This component provides a consistent look and feel for authentication text fields.
 */
public class AuthTextField extends TextField {
    
    /**
     * Creates a new AuthTextField with the specified prompt text.
     *
     * @param promptText the prompt text to display when the field is empty
     */
    public AuthTextField(String promptText) {
        super();
        setPromptText(promptText);
        initialize();
    }
    
    /**
     * Creates a new AuthTextField with no prompt text.
     */
    public AuthTextField() {
        super();
        initialize();
    }
    
    /**
     * Initializes the text field with the appropriate styles and properties.
     */
    private void initialize() {
        // Add the auth-text-field style class
        getStyleClass().add("auth-text-field");
        
        // Set default properties
        setPrefHeight(40);
        setMaxWidth(Double.MAX_VALUE);
    }
    
    /**
     * Creates a new password field with the specified prompt text.
     * This is a convenience method for creating a password field with the same styling.
     *
     * @param promptText the prompt text to display when the field is empty
     * @return a new password field
     */
    public static PasswordField createPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.getStyleClass().add("auth-text-field");
        passwordField.setPrefHeight(40);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        return passwordField;
    }
    
    /**
     * Sets this text field as required.
     * This applies the required-field style class.
     *
     * @return this text field for method chaining
     */
    public AuthTextField setRequired() {
        getStyleClass().add("required-field");
        return this;
    }
    
    /**
     * Sets this text field as invalid.
     * This applies the invalid-field style class.
     *
     * @return this text field for method chaining
     */
    public AuthTextField setInvalid() {
        getStyleClass().add("invalid-field");
        return this;
    }
    
    /**
     * Clears the invalid state of this text field.
     * This removes the invalid-field style class.
     *
     * @return this text field for method chaining
     */
    public AuthTextField clearInvalid() {
        getStyleClass().remove("invalid-field");
        return this;
    }
}