package com.belman.presentation.views.admin;

import com.belman.backbone.core.base.BaseController;
import com.belman.domain.aggregates.User;
import com.belman.domain.aggregates.User.Role;
import com.belman.presentation.viewmodels.admin.AdminViewModel;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

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
    @FXML private ComboBox<Role> roleComboBox;
    @FXML private Button createUserButton;
    @FXML private Button clearFormButton;

    // User list
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, Set<Role>> rolesColumn;
    @FXML private Button deleteUserButton;

    // Role management
    @FXML private ComboBox<Role> roleToAssignComboBox;
    @FXML private Button assignRoleButton;
    @FXML private ListView<Role> userRolesListView;
    @FXML private Button removeRoleButton;

    // Password reset
    @FXML private PasswordField newPasswordField;
    @FXML private Button resetPasswordButton;

    // Status
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;

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
            () -> data.getValue().getEmail().getValue()
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
        User selectedUser = getViewModel().selectedUserProperty().get();
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
        Role selectedRole = userRolesListView.getSelectionModel().getSelectedItem();
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
