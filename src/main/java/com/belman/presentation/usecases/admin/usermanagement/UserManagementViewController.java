package com.belman.presentation.usecases.admin.usermanagement;

import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.presentation.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

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
    private ListView<UserBusiness> usersListView;

    @FXML
    private ComboBox<UserRole> roleFilterComboBox;

    @FXML
    private Button addUserButton;

    @FXML
    private Button editUserButton;

    @FXML
    private Button deleteUserButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label errorLabel;

    @Override
    protected void setupBindings() {
        // Bind view model properties to UI elements
        searchField.textProperty().bindBidirectional(getViewModel().searchTextProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());

        // Bind the users list to the view model
        usersListView.setItems(getViewModel().getUsers());

        // Populate the role filter combo box
        roleFilterComboBox.setItems(getViewModel().getUserRoles());
        roleFilterComboBox.getItems().add(0, null); // Add null for "All roles"
        roleFilterComboBox.setPromptText("All roles");

        // Disable buttons when no user is selected
        editUserButton.disableProperty().bind(
                usersListView.getSelectionModel().selectedItemProperty().isNull()
        );
        deleteUserButton.disableProperty().bind(
                usersListView.getSelectionModel().selectedItemProperty().isNull()
        );

        // Set up selection listener for the users list
        usersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                getViewModel().selectUser(newVal);
            }
        });

        // Set up cell factory for the users list
        usersListView.setCellFactory(listView -> new UserListCell());
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
     * Custom list cell for displaying users.
     */
    private static class UserListCell extends javafx.scene.control.ListCell<UserBusiness> {
        @Override
        protected void updateItem(UserBusiness item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                String username = item.getUsername().value();
                String roles = item.getRoles().stream()
                        .map(Enum::name)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
                String status = item.getApprovalState().isApproved() ? "Active" : "Inactive";

                setText(username + " (" + roles + ") - " + status);
            }
        }
    }
}