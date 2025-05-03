package com.belman.presentation.views.usermanagement;

import com.belman.backbone.core.base.BaseController;
import com.belman.backbone.core.navigation.Router;
import com.belman.domain.aggregates.User;
import com.belman.domain.enums.UserStatus;
import com.belman.presentation.components.TouchFriendlyDialog;
import com.belman.presentation.views.main.MainView;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller for the user management view.
 * Handles user interface interactions for managing user accounts.
 */
public class UserManagementViewController extends BaseController<UserManagementViewModel> {
    @FXML
    private Button backButton;

    @FXML
    private Button createUserButton;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<User> userListView;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<UserStatus> statusComboBox;

    @FXML
    private CheckBox adminRoleCheckBox;

    @FXML
    private CheckBox qaRoleCheckBox;

    @FXML
    private CheckBox productionRoleCheckBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button resetPasswordButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorMessageLabel;

    @Override
    public void initializeBinding() {
        // Bind UI components to ViewModel properties
        errorMessageLabel.textProperty().bind(getViewModel().errorMessageProperty());

        // Set up the status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(UserStatus.values()));

        // Set up the user list view
        userListView.setItems(getViewModel().getUsersProperty());
        userListView.setCellFactory(listView -> new UserListCell());

        // Bind the search field to the filter property
        searchField.textProperty().bindBidirectional(getViewModel().searchFilterProperty());

        // Bind the form fields to the selected user properties
        usernameField.textProperty().bindBidirectional(getViewModel().usernameProperty());
        firstNameField.textProperty().bindBidirectional(getViewModel().firstNameProperty());
        lastNameField.textProperty().bindBidirectional(getViewModel().lastNameProperty());
        emailField.textProperty().bindBidirectional(getViewModel().emailProperty());

        // Bind the status combo box to the selected user status property
        statusComboBox.valueProperty().bindBidirectional(getViewModel().statusProperty());

        // Bind the role checkboxes to the selected user roles properties
        adminRoleCheckBox.selectedProperty().bindBidirectional(getViewModel().adminRoleProperty());
        qaRoleCheckBox.selectedProperty().bindBidirectional(getViewModel().qaRoleProperty());
        productionRoleCheckBox.selectedProperty().bindBidirectional(getViewModel().productionRoleProperty());

        // Bind the password field to the password property
        passwordField.textProperty().bindBidirectional(getViewModel().passwordProperty());

        // Disable the form fields when no user is selected
        usernameField.disableProperty().bind(getViewModel().userSelectedProperty().not());
        firstNameField.disableProperty().bind(getViewModel().userSelectedProperty().not());
        lastNameField.disableProperty().bind(getViewModel().userSelectedProperty().not());
        emailField.disableProperty().bind(getViewModel().userSelectedProperty().not());
        statusComboBox.disableProperty().bind(getViewModel().userSelectedProperty().not());
        adminRoleCheckBox.disableProperty().bind(getViewModel().userSelectedProperty().not());
        qaRoleCheckBox.disableProperty().bind(getViewModel().userSelectedProperty().not());
        productionRoleCheckBox.disableProperty().bind(getViewModel().userSelectedProperty().not());
        passwordField.disableProperty().bind(getViewModel().userSelectedProperty().not());
        resetPasswordButton.disableProperty().bind(getViewModel().userSelectedProperty().not());
        saveButton.disableProperty().bind(getViewModel().userSelectedProperty().not());

        // Set up event handlers
        backButton.setOnAction(this::handleBackButtonAction);
        createUserButton.setOnAction(this::handleCreateUserButtonAction);
        resetPasswordButton.setOnAction(this::handleResetPasswordButtonAction);
        saveButton.setOnAction(this::handleSaveButtonAction);
        cancelButton.setOnAction(this::handleCancelButtonAction);

        // Set up selection listener for the user list view
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().selectUser(newVal);
            }
        });

        // Set up search field listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            getViewModel().filterUsers();
        });

        // Load users when the view is shown
        getViewModel().loadUsers();
    }

    /**
     * Handles the back button action.
     * 
     * @param event the action event
     */
    private void handleBackButtonAction(ActionEvent event) {
        Router.navigateTo(MainView.class);
    }

    /**
     * Handles the create user button action.
     * 
     * @param event the action event
     */
    private void handleCreateUserButtonAction(ActionEvent event) {
        getViewModel().createNewUser();
    }

    /**
     * Handles the reset password button action.
     * 
     * @param event the action event
     */
    private void handleResetPasswordButtonAction(ActionEvent event) {
        getViewModel().resetPassword();
    }

    /**
     * Handles the save button action.
     * 
     * @param event the action event
     */
    private void handleSaveButtonAction(ActionEvent event) {
        boolean success = getViewModel().saveUser();
        if (success) {
            showInfo("User saved successfully");
        }
    }

    /**
     * Handles the cancel button action.
     * 
     * @param event the action event
     */
    private void handleCancelButtonAction(ActionEvent event) {
        getViewModel().cancelEdit();
    }

    /**
     * Shows an error message using a touch-friendly dialog.
     * 
     * @param message the error message
     */
    private void showError(String message) {
        TouchFriendlyDialog.showError("Error", message);
    }

    /**
     * Shows an information message using a touch-friendly dialog.
     * 
     * @param message the information message
     */
    private void showInfo(String message) {
        TouchFriendlyDialog.showInformation("Information", message);
    }

    /**
     * Custom cell for displaying users in the list view.
     */
    private static class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(user.getUsername().value() + " (" + user.getStatus() + ")");
            }
        }
    }
}
