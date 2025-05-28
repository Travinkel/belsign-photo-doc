package com.belman.presentation.usecases.admin.settings;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

/**
 * Controller for the system settings view.
 * Handles UI interactions for the system settings screen.
 */
public final class SystemSettingsViewController extends BaseController<SystemSettingsViewModel> {

    @Inject
    private SessionContext sessionContext;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField databaseUrlField;

    @FXML
    private TextField databaseUserField;

    @FXML
    private TextField databasePasswordField;

    @FXML
    private CheckBox enableAutoBackupCheckbox;

    @FXML
    private ComboBox<String> backupIntervalComboBox;

    @FXML
    private TextField backupLocationField;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        welcomeLabel.textProperty().bind(getViewModel().welcomeMessageProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());

        // Initialize ComboBox items
        backupIntervalComboBox.getItems().addAll("Daily", "Weekly", "Monthly");
    }

    /**
     * Handles the save button click.
     */
    @FXML
    private void handleSave() {
        getViewModel().saveSettings();
    }

    /**
     * Handles the back button click.
     */
    @FXML
    private void handleBack() {
        getViewModel().navigateBack();
    }
}
