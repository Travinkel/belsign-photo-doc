package com.belman.ui.usecases.authentication.components;

import javafx.scene.control.Button;

/**
 * A styled button for authentication actions (login, logout, etc.).
 * This component provides a consistent look and feel for authentication buttons.
 */
public class AuthButton extends Button {
    
    /**
     * Creates a new AuthButton with the specified text.
     *
     * @param text the text to display on the button
     */
    public AuthButton(String text) {
        super(text);
        initialize();
    }
    
    /**
     * Creates a new AuthButton with no text.
     */
    public AuthButton() {
        super();
        initialize();
    }
    
    /**
     * Initializes the button with the appropriate styles and properties.
     */
    private void initialize() {
        // Add the auth-button style class
        getStyleClass().add("auth-button");
        
        // Set default properties
        setPrefWidth(200);
        setPrefHeight(40);
        setMaxWidth(Double.MAX_VALUE);
    }
    
    /**
     * Sets this button as the primary action button.
     * This applies the primary-button style class.
     *
     * @return this button for method chaining
     */
    public AuthButton setPrimary() {
        getStyleClass().add("primary-button");
        return this;
    }
    
    /**
     * Sets this button as the secondary action button.
     * This applies the secondary-button style class.
     *
     * @return this button for method chaining
     */
    public AuthButton setSecondary() {
        getStyleClass().add("secondary-button");
        return this;
    }
    
    /**
     * Sets this button as the danger action button.
     * This applies the danger-button style class.
     *
     * @return this button for method chaining
     */
    public AuthButton setDanger() {
        getStyleClass().add("danger-button");
        return this;
    }
}