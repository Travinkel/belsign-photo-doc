package com.belman.ui.views.main;


import com.belman.ui.base.BaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the main view.
 */
public class MainViewController extends BaseController<MainViewModel> {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Button logoutButton;

    @Override
    protected void setupBindings() {
        // Bind UI components to ViewModel properties if they exist
        // This is important for tests where the FXML might not have these elements
        if (welcomeLabel != null) {
            welcomeLabel.textProperty().bind(getViewModel().welcomeMessageProperty());
        }

        // Set up event handlers if the button exists
        if (logoutButton != null) {
            logoutButton.setOnAction(this::handleLogoutButtonAction);
        }
    }

    @Override
    public void initializeBinding() {
        // Call setupBindings to avoid duplication
        setupBindings();
    }

    /**
     * Handles the logout button action.
     * 
     * @param event the action event
     */
    private void handleLogoutButtonAction(ActionEvent event) {
        getViewModel().logout();
    }
}
