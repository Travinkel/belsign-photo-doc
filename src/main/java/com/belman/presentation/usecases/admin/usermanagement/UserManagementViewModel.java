package com.belman.presentation.usecases.admin.usermanagement;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.UserRole;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.components.TouchFriendlyDialog;
import com.belman.presentation.navigation.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ViewModel for the User Management view.
 * Provides data and operations for user management functionality.
 */
public class UserManagementViewModel extends BaseViewModel<UserManagementViewModel> {
    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final ObservableList<UserBusiness> users = FXCollections.observableArrayList();
    private final FilteredList<UserBusiness> filteredUsers = new FilteredList<>(users);

    @Inject
    private UserRepository userRepository;

    @Inject
    private SessionContext sessionContext;

    private UserBusiness selectedUser;

    /**
     * Default constructor for use by the ViewLoader.
     */
    public UserManagementViewModel() {
        // Default constructor
    }

    @Override
    public void onShow() {
        // Load all users
        loadUsers();
    }

    /**
     * Loads all users from the repository.
     */
    public void loadUsers() {
        try {
            // Get all users
            List<UserBusiness> allUsers = userRepository.findAll();
            users.setAll(allUsers);

            // Apply any existing search filter
            filterUsers();
        } catch (Exception e) {
            errorMessage.set("Error loading users: " + e.getMessage());
        }
    }

    /**
     * Filters the users list based on the search text.
     */
    private void filterUsers() {
        String filter = searchText.get().toLowerCase();

        if (filter == null || filter.isEmpty()) {
            filteredUsers.setPredicate(user -> true);
        } else {
            filteredUsers.setPredicate(user ->
                    user.getUsername().value().toLowerCase().contains(filter) ||
                    (user.getEmail() != null && user.getEmail().value().toLowerCase().contains(filter))
            );
        }
    }

    /**
     * Searches users based on the search text.
     */
    public void searchUsers() {
        filterUsers();
    }

    /**
     * Selects a user for further operations.
     *
     * @param user the user to select
     */
    public void selectUser(UserBusiness user) {
        this.selectedUser = user;
    }

    /**
     * Shows a dialog for adding a new user.
     */
    public void showAddUserDialog() {
        // Create a dialog for adding a new user
        com.belman.presentation.usecases.admin.components.UserDialog dialog = 
            new com.belman.presentation.usecases.admin.components.UserDialog("Add User");

        // Show the dialog and get the result
        UserBusiness newUser = dialog.showAndWait();

        // If the dialog was not cancelled and a user was created
        if (newUser != null) {
            try {
                // Save the user to the repository
                userRepository.save(newUser);

                // Reload the users list
                loadUsers();
            } catch (Exception e) {
                errorMessage.set("Error creating user: " + e.getMessage());
            }
        }
    }

    /**
     * Shows a dialog for editing the selected user.
     */
    public void showEditUserDialog() {
        if (selectedUser == null) {
            errorMessage.set("No user selected");
            return;
        }

        // Create a dialog for editing the selected user
        com.belman.presentation.usecases.admin.components.UserDialog dialog = 
            new com.belman.presentation.usecases.admin.components.UserDialog("Edit User", selectedUser);

        // Show the dialog and get the result
        UserBusiness updatedUser = dialog.showAndWait();

        // If the dialog was not cancelled and the user was updated
        if (updatedUser != null) {
            try {
                // Save the updated user to the repository
                userRepository.save(updatedUser);

                // Reload the users list
                loadUsers();
            } catch (Exception e) {
                errorMessage.set("Error updating user: " + e.getMessage());
            }
        }
    }

    /**
     * Deletes the selected user after confirmation.
     */
    public void deleteSelectedUser() {
        if (selectedUser == null) {
            errorMessage.set("No user selected");
            return;
        }

        // Check if the selected user is the current user
        Optional<UserBusiness> currentUser = sessionContext.getUser();
        if (currentUser.isPresent() && currentUser.get().getId().equals(selectedUser.getId())) {
            errorMessage.set("Cannot delete the currently logged-in user");
            return;
        }

        // Show confirmation dialog
        boolean confirmed = TouchFriendlyDialog.showConfirmation(
                "Delete User",
                "Are you sure you want to delete user " + selectedUser.getUsername().value() + "?"
        );

        if (confirmed) {
            try {
                // Delete the user
                userRepository.delete(selectedUser);

                // Reload the users list
                loadUsers();

                // Clear the selected user
                selectedUser = null;
            } catch (Exception e) {
                errorMessage.set("Error deleting user: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the search text property.
     *
     * @return the search text property
     */
    public StringProperty searchTextProperty() {
        return searchText;
    }

    /**
     * Gets the error message property.
     *
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Gets the filtered users list.
     *
     * @return the filtered users list
     */
    public ObservableList<UserBusiness> getUsers() {
        return filteredUsers;
    }

    /**
     * Gets the list of available user roles.
     *
     * @return the list of available user roles
     */
    public ObservableList<UserRole> getUserRoles() {
        return FXCollections.observableArrayList(UserRole.values());
    }
}
