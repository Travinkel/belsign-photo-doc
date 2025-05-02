package com.belman.presentation.viewmodels.admin;

import com.belman.application.admin.AdminService;
import com.belman.backbone.core.base.BaseViewModel;
import com.belman.domain.aggregates.User;
import com.belman.domain.aggregates.User.Role;
import com.belman.domain.rbac.AccessDeniedException;
import com.belman.domain.valueobjects.UserId;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ViewModel for the admin management view.
 * This class is Gluon-aware and uses the backbone framework.
 */
public class AdminViewModel extends BaseViewModel<AdminViewModel> {
    private static final Logger LOGGER = Logger.getLogger(AdminViewModel.class.getName());

    private final AdminService adminService;

    // Properties for creating a user
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final ObjectProperty<Role> selectedRole = new SimpleObjectProperty<>();

    // Properties for user list
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObjectProperty<User> selectedUser = new SimpleObjectProperty<>();

    // Properties for role management
    private final ObservableList<Role> availableRoles = FXCollections.observableArrayList(Role.values());
    private final ObjectProperty<Role> roleToAssign = new SimpleObjectProperty<>();

    // Properties for password reset
    private final StringProperty newPassword = new SimpleStringProperty();

    // Status properties
    private final StringProperty statusMessage = new SimpleStringProperty();
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    /**
     * Creates a new AdminViewModel with the specified AdminService.
     * 
     * @param adminService the admin service
     */
    public AdminViewModel(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Initializes the view model.
     * Loads the list of users.
     */
    public void initialize() {
        loadUsers();
    }

    /**
     * Loads the list of users.
     */
    public void loadUsers() {
        loading.set(true);
        statusMessage.set("Loading users...");

        try {
            List<User> allUsers = adminService.getAllUsers();
            users.clear();
            users.addAll(allUsers);
            statusMessage.set("Loaded " + allUsers.size() + " users");
        } catch (AccessDeniedException e) {
            statusMessage.set("Access denied: " + e.getMessage());
            LOGGER.log(Level.WARNING, "Access denied when loading users", e);
        } catch (Exception e) {
            statusMessage.set("Error loading users: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error loading users", e);
        } finally {
            loading.set(false);
        }
    }

    /**
     * Creates a new user.
     */
    public void createUser() {
        loading.set(true);
        statusMessage.set("Creating user...");

        try {
            User user = adminService.createUser(
                username.get(),
                password.get(),
                firstName.get(),
                lastName.get(),
                email.get(),
                selectedRole.get()
            );

            users.add(user);
            clearUserForm();
            statusMessage.set("User created successfully");
        } catch (AccessDeniedException e) {
            statusMessage.set("Access denied: " + e.getMessage());
            LOGGER.log(Level.WARNING, "Access denied when creating user", e);
        } catch (Exception e) {
            statusMessage.set("Error creating user: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error creating user", e);
        } finally {
            loading.set(false);
        }
    }

    /**
     * Deletes the selected user.
     */
    public void deleteUser() {
        if (selectedUser.get() == null) {
            statusMessage.set("No user selected");
            return;
        }

        loading.set(true);
        statusMessage.set("Deleting user...");

        try {
            boolean deleted = adminService.deleteUser(selectedUser.get().getId());

            if (deleted) {
                users.remove(selectedUser.get());
                selectedUser.set(null);
                statusMessage.set("User deleted successfully");
            } else {
                statusMessage.set("User not found");
            }
        } catch (AccessDeniedException e) {
            statusMessage.set("Access denied: " + e.getMessage());
            LOGGER.log(Level.WARNING, "Access denied when deleting user", e);
        } catch (Exception e) {
            statusMessage.set("Error deleting user: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
        } finally {
            loading.set(false);
        }
    }

    /**
     * Assigns a role to the selected user.
     */
    public void assignRole() {
        if (selectedUser.get() == null) {
            statusMessage.set("No user selected");
            return;
        }

        if (roleToAssign.get() == null) {
            statusMessage.set("No role selected");
            return;
        }

        loading.set(true);
        statusMessage.set("Assigning role...");

        try {
            boolean assigned = adminService.assignRole(selectedUser.get().getId(), roleToAssign.get());

            if (assigned) {
                // Refresh the user to show the updated roles
                loadUsers();
                statusMessage.set("Role assigned successfully");
            } else {
                statusMessage.set("User not found");
            }
        } catch (AccessDeniedException e) {
            statusMessage.set("Access denied: " + e.getMessage());
            LOGGER.log(Level.WARNING, "Access denied when assigning role", e);
        } catch (Exception e) {
            statusMessage.set("Error assigning role: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error assigning role", e);
        } finally {
            loading.set(false);
        }
    }

    /**
     * Removes a role from the selected user.
     * 
     * @param role the role to remove
     */
    public void removeRole(Role role) {
        if (selectedUser.get() == null) {
            statusMessage.set("No user selected");
            return;
        }

        loading.set(true);
        statusMessage.set("Removing role...");

        try {
            boolean removed = adminService.removeRole(selectedUser.get().getId(), role);

            if (removed) {
                // Refresh the user to show the updated roles
                loadUsers();
                statusMessage.set("Role removed successfully");
            } else {
                statusMessage.set("User not found");
            }
        } catch (AccessDeniedException e) {
            statusMessage.set("Access denied: " + e.getMessage());
            LOGGER.log(Level.WARNING, "Access denied when removing role", e);
        } catch (Exception e) {
            statusMessage.set("Error removing role: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error removing role", e);
        } finally {
            loading.set(false);
        }
    }

    /**
     * Resets the password for the selected user.
     */
    public void resetPassword() {
        if (selectedUser.get() == null) {
            statusMessage.set("No user selected");
            return;
        }

        if (newPassword.get() == null || newPassword.get().isEmpty()) {
            statusMessage.set("New password is required");
            return;
        }

        loading.set(true);
        statusMessage.set("Resetting password...");

        try {
            boolean reset = adminService.resetPassword(selectedUser.get().getId(), newPassword.get());

            if (reset) {
                newPassword.set("");
                statusMessage.set("Password reset successfully");
            } else {
                statusMessage.set("User not found");
            }
        } catch (AccessDeniedException e) {
            statusMessage.set("Access denied: " + e.getMessage());
            LOGGER.log(Level.WARNING, "Access denied when resetting password", e);
        } catch (Exception e) {
            statusMessage.set("Error resetting password: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error resetting password", e);
        } finally {
            loading.set(false);
        }
    }

    /**
     * Clears the user form.
     */
    public void clearUserForm() {
        username.set("");
        password.set("");
        firstName.set("");
        lastName.set("");
        email.set("");
        selectedRole.set(null);
    }

    // Getters for properties

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public ObjectProperty<Role> selectedRoleProperty() {
        return selectedRole;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public ObjectProperty<User> selectedUserProperty() {
        return selectedUser;
    }

    public ObservableList<Role> getAvailableRoles() {
        return availableRoles;
    }

    public ObjectProperty<Role> roleToAssignProperty() {
        return roleToAssign;
    }

    public StringProperty newPasswordProperty() {
        return newPassword;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }
}
