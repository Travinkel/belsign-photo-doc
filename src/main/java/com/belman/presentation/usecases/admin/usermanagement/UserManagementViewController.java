package com.belman.presentation.usecases.admin.usermanagement;

import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the User Management view.
 * Handles UI interactions for the User Management screen.
 */
public class UserManagementViewController extends BaseController<UserManagementViewModel> {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<UserBusiness> userTable;

    @FXML
    private TableColumn<UserBusiness, String> usernameColumn;

    @FXML
    private TableColumn<UserBusiness, String> nameColumn;

    @FXML
    private TableColumn<UserBusiness, String> emailColumn;

    @FXML
    private TableColumn<UserBusiness, String> roleColumn;

    @FXML
    private TableColumn<UserBusiness, String> statusColumn;

    @FXML
    private TableColumn<UserBusiness, Void> actionsColumn;

    @FXML
    private ComboBox<UserRole> roleFilterComboBox;

    @FXML
    private Button editUserButton;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button addUserButton;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label errorLabel;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        searchField.textProperty().bindBidirectional(getViewModel().searchTextProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());

        // Update total users label
        totalUsersLabel.setText("Total Users: " + getViewModel().getUsers().size());
        getViewModel().getUsers().addListener((javafx.collections.ListChangeListener<UserBusiness>) c -> {
            totalUsersLabel.setText("Total Users: " + getViewModel().getUsers().size());
        });

        // Bind the users table to the view model
        userTable.setItems(getViewModel().getUsers());

        // Set up cell value factories for the table columns
        usernameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername().value()));

        nameColumn.setCellValueFactory(cellData -> {
            UserBusiness user = cellData.getValue();
            String name = user.getName() != null ? 
                user.getName().firstName() + " " + user.getName().lastName() : "";
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        emailColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEmail() != null ? cellData.getValue().getEmail().value() : ""));

        roleColumn.setCellValueFactory(cellData -> {
            UserBusiness user = cellData.getValue();
            String roles = user.getRoles().stream()
                    .map(Enum::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            return new javafx.beans.property.SimpleStringProperty(roles);
        });

        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getApprovalState().isApproved() ? "Active" : "Inactive"));

        // Populate the role filter combo box
        roleFilterComboBox.setItems(getViewModel().getUserRoles());
        roleFilterComboBox.getItems().add(0, null); // Add null for "All roles"
        roleFilterComboBox.setPromptText("All roles");

        // Disable buttons when no user is selected
        editUserButton.disableProperty().bind(
                userTable.getSelectionModel().selectedItemProperty().isNull()
        );
        deleteUserButton.disableProperty().bind(
                userTable.getSelectionModel().selectedItemProperty().isNull()
        );

        // Set up selection listener for the users table
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().selectUser(newVal);
            }
        });
    }

    /**
     * Handles the search button click.
     */
    @FXML
    private void handleSearch() {
        getViewModel().searchUsers();
    }

    /**
     * Handles the add user button click.
     */
    @FXML
    private void handleAddUser() {
        getViewModel().showAddUserDialog();
    }

    /**
     * Handles the edit user button click.
     */
    @FXML
    private void handleEditUser() {
        getViewModel().showEditUserDialog();
    }

    /**
     * Handles the delete user button click.
     */
    @FXML
    private void handleDeleteUser() {
        getViewModel().deleteSelectedUser();
    }

    /**
     * Handles the back button click.
     */
    @FXML
    private void handleBack() {
        com.belman.presentation.navigation.Router.navigateBack();
    }

}
