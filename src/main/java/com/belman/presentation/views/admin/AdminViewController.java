package com.belman.presentation.views.admin;

import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.presentation.base.BaseController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Set;

/**
 * Controller for the admin management view.
 * This class is Gluon-aware and uses the backbone framework.
 */
public class AdminViewController extends BaseController<AdminViewModel> {

    // User creation form
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<UserRole> roleComboBox;
    @FXML private Button createUserButton;
    @FXML private Button clearFormButton;

    // User list
    @FXML private TableView<UserBusiness> userTable;
    @FXML private TableColumn<UserBusiness, String> usernameColumn;
    @FXML private TableColumn<UserBusiness, String> nameColumn;
    @FXML private TableColumn<UserBusiness, String> emailColumn;
    @FXML private TableColumn<UserBusiness, Set<UserRole>> rolesColumn;
    @FXML private Button deleteUserButton;

    // Role management
    @FXML private ComboBox<UserRole> roleToAssignComboBox;
    @FXML private Button assignRoleButton;
    @FXML private ListView<UserRole> userRolesListView;
    @FXML private Button removeRoleButton;

    // Password reset
    @FXML private PasswordField newPasswordField;
    @FXML private Button resetPasswordButton;

    // Status
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;

    @Override
    protected void setupBindings() {
        // Example implementation for required bindings based on project logic
    }

    @Override
    public void initializeBinding() {
        // Initialize the view model
        getViewModel().initialize();

        // Set up bindings for user creation form
        usernameField.textProperty().bindBidirectional(getViewModel().usernameProperty());
        passwordField.textProperty().bindBidirectional(getViewModel().passwordProperty());
        firstNameField.textProperty().bindBidirectional(getViewModel().firstNameProperty());
        lastNameField.textProperty().bindBidirectional(getViewModel().lastNameProperty());
        emailField.textProperty().bindBidirectional(getViewModel().emailProperty());

        // Set up role combo boxes
        roleComboBox.setItems(getViewModel().getAvailableRoles());
        roleComboBox.valueProperty().bindBidirectional(getViewModel().selectedRoleProperty());

        roleToAssignComboBox.setItems(getViewModel().getAvailableRoles());
        roleToAssignComboBox.valueProperty().bindBidirectional(getViewModel().roleToAssignProperty());

        // Set up user table
        userTable.setItems(getViewModel().getUsers());
        usernameColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> data.getValue().getUsername().value()
        ));
        nameColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> {
                    if (data.getValue().getName() != null) {
                        return data.getValue().getName().firstName() + " " + data.getValue().getName().lastName();
                    }
                    return "";
                }
        ));
        emailColumn.setCellValueFactory(data -> Bindings.createStringBinding(
                () -> {
                    if (data.getValue().getEmail() != null) {
                        return data.getValue().getEmail().value();
                    }
                    return "";
                }
        ));
        rolesColumn.setCellValueFactory(data -> Bindings.createObjectBinding(
                () -> data.getValue().getRoles()
        ));

        // Bind selected user
        userTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    getViewModel().selectedUserProperty().set(newVal);
                    updateUserRolesList();
                }
        );

        // Bind new password field
        newPasswordField.textProperty().bindBidirectional(getViewModel().newPasswordProperty());

        // Bind status and loading
        statusLabel.textProperty().bind(getViewModel().statusMessageProperty());
        loadingIndicator.visibleProperty().bind(getViewModel().loadingProperty());

        // Disable buttons when no user is selected
        deleteUserButton.disableProperty().bind(getViewModel().selectedUserProperty().isNull());
        assignRoleButton.disableProperty().bind(
                getViewModel().selectedUserProperty().isNull().or(getViewModel().roleToAssignProperty().isNull())
        );
        removeRoleButton.disableProperty().bind(
                getViewModel().selectedUserProperty().isNull().or(
                        Bindings.createBooleanBinding(
                                () -> userRolesListView.getSelectionModel().getSelectedItem() == null,
                                userRolesListView.getSelectionModel().selectedItemProperty()
                        )
                )
        );
        resetPasswordButton.disableProperty().bind(
                getViewModel().selectedUserProperty().isNull().or(
                        getViewModel().newPasswordProperty().isEmpty()
                )
        );
    }

    /**
     * Updates the user roles list view based on the selected user.
     */
    private void updateUserRolesList() {
        UserBusiness selectedUser = getViewModel().selectedUserProperty().get();
        if (selectedUser != null) {
            userRolesListView.setItems(FXCollections.observableArrayList(selectedUser.getRoles()));
        } else {
            userRolesListView.setItems(FXCollections.observableArrayList());
        }
    }

    /**
     * Handles the create user button click.
     */
    @FXML
    private void handleCreateUser() {
        getViewModel().createUser();
    }

    /**
     * Handles the clear form button click.
     */
    @FXML
    private void handleClearForm() {
        getViewModel().clearUserForm();
    }

    /**
     * Handles the delete user button click.
     */
    @FXML
    private void handleDeleteUser() {
        getViewModel().deleteUser();
    }

    /**
     * Handles the assign role button click.
     */
    @FXML
    private void handleAssignRole() {
        getViewModel().assignRole();
    }

    /**
     * Handles the remove role button click.
     */
    @FXML
    private void handleRemoveRole() {
        UserRole selectedRole = userRolesListView.getSelectionModel().getSelectedItem();
        if (selectedRole != null) {
            getViewModel().removeRole(selectedRole);
        }
    }

    /**
     * Handles the reset password button click.
     */
    @FXML
    private void handleResetPassword() {
        getViewModel().resetPassword();
    }

    /**
     * Handles the refresh button click.
     */
    @FXML
    private void handleRefresh() {
        getViewModel().loadUsers();
    }
}
